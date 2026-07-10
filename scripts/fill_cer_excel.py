from __future__ import annotations

import os
import re
import sys
from collections import defaultdict
from pathlib import Path
from zipfile import ZipFile, ZIP_DEFLATED
import xml.etree.ElementTree as ET

sys.path.insert(0, "/private/tmp/codex_pymysql")
import pymysql  # type: ignore


SRC = Path("/Users/zoujun/Downloads/齐博士 CER数据梳理.xlsx")
OUT = Path("/Users/zoujun/Downloads/齐博士 CER数据梳理_已回填.xlsx")

DB = {
    "host": os.environ.get("CER_DB_HOST", "172.16.14.9"),
    "port": int(os.environ.get("CER_DB_PORT", "13306")),
    "user": os.environ.get("CER_DB_USER", "root"),
    "password": os.environ["CER_DB_PASSWORD"],
    "database": os.environ.get("CER_DB_DATABASE", "bio_cer_prod"),
    "charset": "utf8mb4",
    "connect_timeout": 10,
    "read_timeout": 60,
}

NS_MAIN = "http://schemas.openxmlformats.org/spreadsheetml/2006/main"
NS = {"a": NS_MAIN}
ET.register_namespace("", NS_MAIN)


def col_to_num(col: str) -> int:
    n = 0
    for ch in col:
        n = n * 26 + ord(ch) - 64
    return n


def cell_col(ref: str) -> str:
    return re.match(r"[A-Z]+", ref).group(0)


def natural_key(value: str):
    return [int(x) if x.isdigit() else x for x in re.split(r"(\d+)", value)]


def clean_code(value) -> str:
    return str(value).strip() if value is not None else ""


def unique_sorted(values) -> list[str]:
    seen = set()
    result = []
    for value in sorted((clean_code(v) for v in values), key=natural_key):
        if value and value not in seen:
            seen.add(value)
            result.append(value)
    return result


def join_codes(values: list[str]) -> str:
    return "、".join(values)


def load_shared_strings(z: ZipFile) -> list[str]:
    if "xl/sharedStrings.xml" not in z.namelist():
        return []
    root = ET.fromstring(z.read("xl/sharedStrings.xml"))
    strings = []
    for si in root.findall("a:si", NS):
        strings.append("".join((t.text or "") for t in si.findall(".//a:t", NS)))
    return strings


def cell_text(cell: ET.Element, shared_strings: list[str]) -> str:
    typ = cell.attrib.get("t")
    if typ == "inlineStr":
        return "".join((t.text or "") for t in cell.findall(".//a:t", NS)).strip()
    v = cell.find("a:v", NS)
    if v is None or v.text is None:
        return ""
    if typ == "s":
        return shared_strings[int(v.text)].strip()
    return v.text.strip()


def row_cells(row: ET.Element) -> dict[str, ET.Element]:
    return {cell_col(c.attrib["r"]): c for c in row.findall("a:c", NS) if "r" in c.attrib}


def ensure_cell(row: ET.Element, col: str, row_num: int, style_source: ET.Element | None = None) -> ET.Element:
    cells = row_cells(row)
    if col in cells:
        return cells[col]

    cell = ET.Element(f"{{{NS_MAIN}}}c", {"r": f"{col}{row_num}"})
    if style_source is not None and "s" in style_source.attrib:
        cell.attrib["s"] = style_source.attrib["s"]

    target = col_to_num(col)
    children = list(row)
    insert_at = len(children)
    for idx, child in enumerate(children):
        if child.tag == f"{{{NS_MAIN}}}c" and "r" in child.attrib and col_to_num(cell_col(child.attrib["r"])) > target:
            insert_at = idx
            break
    row.insert(insert_at, cell)
    return cell


def clear_cell(cell: ET.Element) -> None:
    for child in list(cell):
        cell.remove(child)
    cell.attrib.pop("t", None)


def set_text(cell: ET.Element, value: str) -> None:
    clear_cell(cell)
    if not value:
        return
    cell.attrib["t"] = "inlineStr"
    is_el = ET.SubElement(cell, f"{{{NS_MAIN}}}is")
    t_el = ET.SubElement(is_el, f"{{{NS_MAIN}}}t")
    t_el.text = value


def set_number(cell: ET.Element, value: int) -> None:
    clear_cell(cell)
    v_el = ET.SubElement(cell, f"{{{NS_MAIN}}}v")
    v_el.text = str(value)


def fetch_data(vector_task_codes: list[str]):
    sample_rows = defaultdict(list)
    plant_rows = defaultdict(list)
    conn = pymysql.connect(**DB)
    try:
        with conn.cursor() as cur:
            for i in range(0, len(vector_task_codes), 500):
                batch = vector_task_codes[i : i + 500]
                placeholders = ",".join(["%s"] * len(batch))
                cur.execute(
                    f"""
                    select vector_task_code, sample_code, test_result, check_result
                    from bio_sample_test_tb
                    where vector_task_code in ({placeholders})
                    """,
                    batch,
                )
                for row in cur.fetchall():
                    sample_rows[row[0]].append(row)

                cur.execute(
                    f"""
                    select vector_task_code, sample_code, plant_status
                    from plant_single_stock_tb
                    where vector_task_code in ({placeholders})
                    """,
                    batch,
                )
                for row in cur.fetchall():
                    plant_rows[row[0]].append(row)
    finally:
        conn.close()
    return sample_rows, plant_rows


def main() -> None:
    with ZipFile(SRC, "r") as zin:
        shared_strings = load_shared_strings(zin)
        sheet_xml = zin.read("xl/worksheets/sheet1.xml")
        sheet = ET.fromstring(sheet_xml)

        rows = sheet.findall(".//a:sheetData/a:row", NS)
        row_by_vt: dict[int, str] = {}
        for row in rows:
            row_num = int(row.attrib["r"])
            if row_num <= 3:
                continue
            cells = row_cells(row)
            vt_cell = cells.get("C")
            if vt_cell is None:
                continue
            vt = cell_text(vt_cell, shared_strings)
            if vt:
                row_by_vt[row_num] = vt

        vector_task_codes = unique_sorted(row_by_vt.values())
        sample_rows, plant_rows = fetch_data(vector_task_codes)

        changed = 0
        missing = []
        for row in rows:
            row_num = int(row.attrib["r"])
            vt = row_by_vt.get(row_num)
            if not vt:
                continue

            sample_items = sample_rows.get(vt, [])
            plant_items = plant_rows.get(vt, [])

            all_codes = unique_sorted(
                [r[1] for r in sample_items]
                + [r[1] for r in plant_items]
            )
            untested_codes = unique_sorted(
                r[1]
                for r in sample_items
                if clean_code(r[2]).lower() in {"", "notest", "no_test", "未检测"}
            )
            checked_codes = unique_sorted(
                r[1]
                for r in sample_items
                if clean_code(r[3]) and clean_code(r[3]).lower() not in {"nocheck", "no_check", "未审核"}
            )
            harvested_codes = unique_sorted(
                r[1]
                for r in plant_items
                if clean_code(r[2]) == "4"
            )

            if not sample_items and not plant_items:
                missing.append(vt)

            cells = row_cells(row)
            style_source = cells.get("O") or cells.get("N") or cells.get("M")
            updates = {
                "H": ("num", len(untested_codes)),
                "I": ("text", join_codes(all_codes)),
                "J": ("num", len(all_codes)),
                "M": ("text", join_codes(checked_codes)),
                "N": ("num", len(checked_codes)),
                "P": ("text", join_codes(harvested_codes)),
                "Q": ("num", len(harvested_codes)),
            }
            for col, (kind, value) in updates.items():
                cell = ensure_cell(row, col, row_num, cells.get(col) or style_source)
                if kind == "num":
                    set_number(cell, int(value))
                else:
                    set_text(cell, str(value))
            changed += 1

        dimension = sheet.find("a:dimension", NS)
        if dimension is not None:
            dimension.attrib["ref"] = f"A1:S{max(int(r.attrib['r']) for r in rows)}"

        new_sheet = ET.tostring(sheet, encoding="utf-8", xml_declaration=True)

        with ZipFile(OUT, "w", ZIP_DEFLATED) as zout:
            for item in zin.infolist():
                data = zin.read(item.filename)
                if item.filename == "xl/worksheets/sheet1.xml":
                    data = new_sheet
                zout.writestr(item, data)

    print(f"updated_rows={changed}")
    print(f"vector_task_codes={len(vector_task_codes)}")
    print(f"output={OUT}")
    if missing:
        print("no_db_records=" + ",".join(missing[:50]))
        if len(missing) > 50:
            print(f"no_db_records_more={len(missing) - 50}")


if __name__ == "__main__":
    main()
