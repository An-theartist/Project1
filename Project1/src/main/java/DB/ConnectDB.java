package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {

    final static String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver"; // Cập nhật cho phiên bản MySQL mới
    final static String DB_URL = "jdbc:mysql://localhost:3306/project1";
    final static String USER = "root";
    final static String PASS = "123456789";

    // Phương thức để thiết lập kết nối
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Đăng ký driver JDBC
            Class.forName(JDBC_DRIVER);
            
            // Mở kết nối
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            System.out.println("Kết nối thành công!");
        } catch (SQLException se) {
            // Xử lý lỗi khi kết nối MySQL
            se.printStackTrace();
        } catch (Exception e) {
            // Xử lý lỗi khi không tìm thấy driver JDBC
            e.printStackTrace();
        }
        return conn;
    }
}