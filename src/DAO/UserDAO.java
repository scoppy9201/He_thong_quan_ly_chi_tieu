package DAO;

import Model.User;
import Utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

public class UserDAO {

    // Kiểm tra tồn tại user theo email
    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM nguoi_dung WHERE email = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tạo user mới
    public boolean createUser(String fullName, String email, String passwordHash) {
        String sql = "INSERT INTO nguoi_dung (ho_ten, email, mat_khau) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, passwordHash);

            int affected = ps.executeUpdate();
            return affected == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy hashed password theo email
    public String getPasswordHashByEmail(String email) {
        String sql = "SELECT mat_khau FROM nguoi_dung WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("mat_khau");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy thông tin user theo ID
    public User getUserById(int id) {
        String sql = "SELECT id, ho_ten, email, ngay_sinh, gioi_tinh FROM nguoi_dung WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setHoTen(rs.getString("ho_ten"));
                    u.setEmail(rs.getString("email"));
                    u.setNgaySinh(rs.getDate("ngay_sinh"));
                    u.setGioiTinh(rs.getString("gioi_tinh"));
                    return u;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cập nhật user theo ID
    public boolean updateUser(User user) {
        String sql = """
            UPDATE nguoi_dung
            SET ho_ten = ?, ngay_sinh = ?, gioi_tinh = ?, ngay_cap_nhat = CURRENT_TIMESTAMP
            WHERE id = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getHoTen());
            if (user.getNgaySinh() != null) {
                ps.setDate(2, new java.sql.Date(user.getNgaySinh().getTime()));
            } else {
                ps.setNull(2, Types.DATE);
            }
            ps.setString(3, user.getGioiTinh());
            ps.setInt(4, user.getId());

            int affected = ps.executeUpdate();
            return affected == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Lấy thông tin user theo email
    public User getUserByEmail(String email) {
        String sql = "SELECT id, ho_ten, email, ngay_sinh, gioi_tinh FROM nguoi_dung WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setHoTen(rs.getString("ho_ten"));
                    u.setEmail(rs.getString("email"));
                    u.setNgaySinh(rs.getDate("ngay_sinh"));
                    u.setGioiTinh(rs.getString("gioi_tinh"));
                    return u;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Cập nhật mật khẩu theo ID
    public boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE nguoi_dung SET mat_khau = ?, ngay_cap_nhat = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);

            int affected = ps.executeUpdate();
            return affected == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
