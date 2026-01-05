package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Thông tin kết nối MySQL (cập nhật allowPublicKeyRetrieval=true)
    private static final String URL = "jdbc:mysql://localhost:3306/quan_ly_chi_tieu?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Phương thức trả về Connection
    public static Connection getConnection() throws ClassNotFoundException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Kết nối MySQL thành công!");
            return conn;
        } catch (SQLException e) {
            System.err.println("Kết nối MySQL thất bại!");
            e.printStackTrace();
            return null;
        }
    }
}
