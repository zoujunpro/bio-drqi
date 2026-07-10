import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CerProdMeta {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String host = args.length > 0 ? args[0] : "172.16.14.9";
        String port = args.length > 1 ? args[1] : "13306";
        String database = args.length > 2 ? args[2] : "bio_cer_prod";
        String username = args.length > 3 ? args[3] : "root";
        String password = args.length > 4 ? args[4] : "biodesign@2023";
        String params = args.length > 5 ? args[5] : "useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?" + params;
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            String sql = "select column_name, column_comment, data_type " +
                    "from information_schema.columns " +
                    "where table_schema='" + database + "' and table_name='plant_single_stock_tb' " +
                    "order by ordinal_position";
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3));
                }
            }
        }
    }
}
