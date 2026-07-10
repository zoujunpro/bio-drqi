import copy
import hashlib
import html
import os
import re
import shutil
import socket
import struct
import tempfile
import zipfile
import xml.etree.ElementTree as ET


INPUT_XLSX = "/Users/zoujun/Downloads/齐博士 CER数据梳理_已回填.xlsx"
OUTPUT_XLSX = "/Users/zoujun/Downloads/齐博士 CER数据梳理_已回填_异常状态.xlsx"

DB_HOST = "172.16.14.9"
DB_PORT = 13306
DB_USER = "root"
DB_PASSWORD = "biodesign@2023"
DB_NAME = "bio_cer_prod"

NS_MAIN = "http://schemas.openxmlformats.org/spreadsheetml/2006/main"
NS_REL = "http://schemas.openxmlformats.org/officeDocument/2006/relationships"
ET.register_namespace("", NS_MAIN)
ET.register_namespace("r", NS_REL)


def col_to_num(col):
    n = 0
    for ch in col:
        n = n * 26 + ord(ch) - ord("A") + 1
    return n


def cell_col(ref):
    return re.match(r"([A-Z]+)", ref).group(1)


def cell_row(ref):
    return int(re.match(r"[A-Z]+(\d+)", ref).group(1))


def read_shared_strings(zf):
    if "xl/sharedStrings.xml" not in zf.namelist():
        return []
    root = ET.fromstring(zf.read("xl/sharedStrings.xml"))
    strings = []
    for si in root.findall("{%s}si" % NS_MAIN):
        texts = []
        for t in si.findall(".//{%s}t" % NS_MAIN):
            texts.append(t.text or "")
        strings.append("".join(texts))
    return strings


def cell_text(cell, shared_strings):
    cell_type = cell.get("t")
    if cell_type == "s":
        v = cell.find("{%s}v" % NS_MAIN)
        if v is None or v.text is None:
            return ""
        return shared_strings[int(v.text)]
    if cell_type == "inlineStr":
        texts = []
        for t in cell.findall(".//{%s}t" % NS_MAIN):
            texts.append(t.text or "")
        return "".join(texts)
    v = cell.find("{%s}v" % NS_MAIN)
    return "" if v is None or v.text is None else v.text


def set_inline_string(row, ref, value):
    cell = find_or_create_cell(row, ref)
    for child in list(cell):
        cell.remove(child)
    cell.set("t", "inlineStr")
    is_el = ET.SubElement(cell, "{%s}is" % NS_MAIN)
    t_el = ET.SubElement(is_el, "{%s}t" % NS_MAIN)
    t_el.text = str(value)
    return cell


def set_number(row, ref, value):
    cell = find_or_create_cell(row, ref)
    for child in list(cell):
        cell.remove(child)
    cell.attrib.pop("t", None)
    v_el = ET.SubElement(cell, "{%s}v" % NS_MAIN)
    v_el.text = str(value)
    return cell


def find_or_create_cell(row, ref):
    for cell in row.findall("{%s}c" % NS_MAIN):
        if cell.get("r") == ref:
            return cell
    new_cell = ET.Element("{%s}c" % NS_MAIN, {"r": ref})
    target_col = col_to_num(cell_col(ref))
    cells = row.findall("{%s}c" % NS_MAIN)
    inserted = False
    for idx, cell in enumerate(cells):
        if col_to_num(cell_col(cell.get("r"))) > target_col:
            row.insert(list(row).index(cell), new_cell)
            inserted = True
            break
    if not inserted:
        row.append(new_cell)
    return new_cell


def read_plan_codes():
    row_to_code = {}
    with zipfile.ZipFile(INPUT_XLSX) as zf:
        shared_strings = read_shared_strings(zf)
        sheet_xml = zf.read("xl/worksheets/sheet1.xml")
        root = ET.fromstring(sheet_xml)
        sheet_data = root.find("{%s}sheetData" % NS_MAIN)
        for row in sheet_data.findall("{%s}row" % NS_MAIN):
            r = int(row.get("r"))
            if r <= 3:
                continue
            code = ""
            for cell in row.findall("{%s}c" % NS_MAIN):
                if cell.get("r") == "C%d" % r:
                    code = cell_text(cell, shared_strings).strip()
                    break
            if code and code not in ("0",):
                row_to_code[r] = code
    return row_to_code


def lenenc_int(data, pos):
    first = data[pos]
    if first < 0xfb:
        return first, pos + 1
    if first == 0xfc:
        return int.from_bytes(data[pos + 1:pos + 3], "little"), pos + 3
    if first == 0xfd:
        return int.from_bytes(data[pos + 1:pos + 4], "little"), pos + 4
    if first == 0xfe:
        return int.from_bytes(data[pos + 1:pos + 9], "little"), pos + 9
    return None, pos + 1


def lenenc_str(data, pos):
    if data[pos] == 0xfb:
        return None, pos + 1
    length, pos = lenenc_int(data, pos)
    if length is None:
        return None, pos
    raw = data[pos:pos + length]
    return raw.decode("utf-8", errors="replace"), pos + length


class MysqlNativeClient:
    CLIENT_LONG_PASSWORD = 0x00000001
    CLIENT_LONG_FLAG = 0x00000004
    CLIENT_CONNECT_WITH_DB = 0x00000008
    CLIENT_PROTOCOL_41 = 0x00000200
    CLIENT_TRANSACTIONS = 0x00002000
    CLIENT_SECURE_CONNECTION = 0x00008000
    CLIENT_MULTI_RESULTS = 0x00020000
    CLIENT_PLUGIN_AUTH = 0x00080000

    def __init__(self, host, port, user, password, database):
        self.sock = socket.socket()
        self.sock.settimeout(30)
        self.sock.connect((host, port))
        self.seq = 0
        self.user = user
        self.password = password
        self.database = database

    def close(self):
        self.sock.close()

    def read_packet(self):
        header = self._read_exact(4)
        length = header[0] | (header[1] << 8) | (header[2] << 16)
        self.seq = header[3]
        return self._read_exact(length)

    def write_packet(self, payload, seq):
        header = struct.pack("<I", len(payload))[:3] + bytes([seq])
        self.sock.sendall(header + payload)

    def _read_exact(self, length):
        data = b""
        while len(data) < length:
            chunk = self.sock.recv(length - len(data))
            if not chunk:
                raise RuntimeError("socket closed")
            data += chunk
        return data

    def login(self):
        handshake = self.read_packet()
        pos = 1
        end = handshake.index(b"\x00", pos)
        pos = end + 1
        pos += 4
        salt1 = handshake[pos:pos + 8]
        pos += 9
        cap_low = int.from_bytes(handshake[pos:pos + 2], "little")
        pos += 2
        pos += 1
        pos += 2
        cap_high = int.from_bytes(handshake[pos:pos + 2], "little")
        pos += 2
        caps = cap_low | (cap_high << 16)
        auth_len = handshake[pos]
        pos += 1
        pos += 10
        salt2_len = max(13, auth_len - 8)
        salt2 = handshake[pos:pos + salt2_len]
        salt = (salt1 + salt2).rstrip(b"\x00")

        flags = (
            self.CLIENT_LONG_PASSWORD
            | self.CLIENT_LONG_FLAG
            | self.CLIENT_PROTOCOL_41
            | self.CLIENT_TRANSACTIONS
            | self.CLIENT_SECURE_CONNECTION
            | self.CLIENT_MULTI_RESULTS
            | self.CLIENT_PLUGIN_AUTH
            | self.CLIENT_CONNECT_WITH_DB
        )
        flags &= caps
        auth = self.scramble_native(self.password.encode("utf-8"), salt)
        payload = struct.pack("<IIB23s", flags, 16 * 1024 * 1024, 33, b"\x00" * 23)
        payload += self.user.encode("utf-8") + b"\x00"
        payload += bytes([len(auth)]) + auth
        payload += self.database.encode("utf-8") + b"\x00"
        payload += b"mysql_native_password\x00"
        self.write_packet(payload, 1)
        resp = self.read_packet()
        if resp[0] == 0xff:
            errno = int.from_bytes(resp[1:3], "little")
            msg = resp[9:].decode("utf-8", errors="replace")
            raise RuntimeError("mysql login failed %s %s" % (errno, msg))
        if resp[0] != 0x00:
            raise RuntimeError("unexpected login response: %r" % resp[:20])

    @staticmethod
    def scramble_native(password, salt):
        if not password:
            return b""
        s1 = hashlib.sha1(password).digest()
        s2 = hashlib.sha1(s1).digest()
        s3 = hashlib.sha1(salt + s2).digest()
        return bytes(a ^ b for a, b in zip(s1, s3))

    def query(self, sql):
        self.write_packet(b"\x03" + sql.encode("utf-8"), 0)
        first = self.read_packet()
        if first[0] == 0xff:
            errno = int.from_bytes(first[1:3], "little")
            msg = first[9:].decode("utf-8", errors="replace")
            raise RuntimeError("query failed %s %s" % (errno, msg))
        col_count, _ = lenenc_int(first, 0)
        columns = []
        for _ in range(col_count):
            packet = self.read_packet()
            pos = 0
            parts = []
            for __ in range(6):
                value, pos = lenenc_str(packet, pos)
                parts.append(value)
            columns.append(parts[4])
        self.read_packet()
        rows = []
        while True:
            packet = self.read_packet()
            if packet[0] == 0xfe and len(packet) < 9:
                break
            pos = 0
            row = {}
            for col in columns:
                value, pos = lenenc_str(packet, pos)
                row[col] = value
            rows.append(row)
        return rows


def sql_literal(value):
    return "'" + value.replace("\\", "\\\\").replace("'", "''") + "'"


def query_abnormal_plants(codes):
    result = {code: [] for code in codes}
    if not codes:
        return result
    client = MysqlNativeClient(DB_HOST, DB_PORT, DB_USER, DB_PASSWORD, DB_NAME)
    try:
        client.login()
        for i in range(0, len(codes), 200):
            chunk = codes[i:i + 200]
            sql = (
                "select vector_task_code, plant_code "
                "from plant_single_stock_tb "
                "where vector_task_code in (%s) "
                "and plant_code is not null and plant_code <> '' "
                "and (plant_status is null or cast(plant_status as char) <> '4') "
                "group by vector_task_code, plant_code "
                "order by vector_task_code, plant_code"
            ) % ",".join(sql_literal(c) for c in chunk)
            for row in client.query(sql):
                result.setdefault(row["vector_task_code"], []).append(row["plant_code"])
    finally:
        client.close()
    return result


def write_excel(row_to_code, abnormal_map):
    tmpdir = tempfile.mkdtemp(prefix="cer_xlsx_")
    try:
        with zipfile.ZipFile(INPUT_XLSX) as zin:
            zin.extractall(tmpdir)

        sheet_path = os.path.join(tmpdir, "xl", "worksheets", "sheet1.xml")
        tree = ET.parse(sheet_path)
        root = tree.getroot()
        sheet_data = root.find("{%s}sheetData" % NS_MAIN)
        for row in sheet_data.findall("{%s}row" % NS_MAIN):
            if int(row.get("r")) == 3:
                set_inline_string(row, "R3", "异常状态种植编号")
                set_inline_string(row, "S3", "状态异常编号数目")
                break
        for row in sheet_data.findall("{%s}row" % NS_MAIN):
            r = int(row.get("r"))
            code = row_to_code.get(r)
            if not code:
                continue
            plants = abnormal_map.get(code, [])
            text = "、".join(plants) if plants else "0"
            set_inline_string(row, "R%d" % r, text)
            set_number(row, "S%d" % r, len(plants))
        tree.write(sheet_path, encoding="UTF-8", xml_declaration=True)

        if os.path.exists(OUTPUT_XLSX):
            os.remove(OUTPUT_XLSX)
        with zipfile.ZipFile(OUTPUT_XLSX, "w", zipfile.ZIP_DEFLATED) as zout:
            for root_dir, _, files in os.walk(tmpdir):
                for file in files:
                    full = os.path.join(root_dir, file)
                    rel = os.path.relpath(full, tmpdir)
                    zout.write(full, rel)
    finally:
        shutil.rmtree(tmpdir)


def main():
    row_to_code = read_plan_codes()
    codes = sorted(set(row_to_code.values()))
    abnormal_map = query_abnormal_plants(codes)
    write_excel(row_to_code, abnormal_map)
    filled_rows = len(row_to_code)
    abnormal_rows = sum(1 for code in row_to_code.values() if abnormal_map.get(code))
    abnormal_total = sum(len(v) for v in abnormal_map.values())
    print("rows=%d codes=%d abnormal_rows=%d abnormal_total=%d output=%s" %
          (filled_rows, len(codes), abnormal_rows, abnormal_total, OUTPUT_XLSX))


if __name__ == "__main__":
    main()
