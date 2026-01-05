package DAO;

import Model.Email;
import Utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmailDAO {
    
    // Thêm email mới
    public boolean insert(Email email) {
        String sql = """
            INSERT INTO email 
            (nguoi_dung_id, tieu_de, noi_dung, email_nguoi_nhan, loai_email, trang_thai, da_gui, la_ghim)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, email.getNguoiDungId());
            ps.setString(2, email.getTieuDe());
            ps.setString(3, email.getNoiDung());
            ps.setString(4, email.getEmailNguoiNhan());
            ps.setString(5, email.getLoaiEmail().name());
            ps.setString(6, email.getTrangThai().name());
            ps.setBoolean(7, email.isDaGui());
            ps.setBoolean(8, email.isLaGhim());
            
            return ps.executeUpdate() == 1;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Lấy tất cả email của user
    public List<Email> getByUserId(int userId) {
        List<Email> list = new ArrayList<>();
        String sql = """
            SELECT * FROM email 
            WHERE nguoi_dung_id = ?
            ORDER BY la_ghim DESC, ngay_tao DESC
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Lấy email theo ID
    public Email getById(int id) {
        String sql = "SELECT * FROM email WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapResultSet(rs);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Đánh dấu đã đọc
    public boolean markAsRead(int id) {
        String sql = """
            UPDATE email 
            SET trang_thai = 'DA_DOC', ngay_doc = CURRENT_TIMESTAMP
            WHERE id = ?
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Toggle ghim
    public boolean toggleGhim(int id) {
        String sql = "UPDATE email SET la_ghim = NOT la_ghim WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Xóa email
    public boolean delete(int id) {
        String sql = "DELETE FROM email WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Đếm email chưa đọc
    public int countUnread(int userId) {
        String sql = """
            SELECT COUNT(*) FROM email 
            WHERE nguoi_dung_id = ? AND trang_thai = 'CHUA_DOC'
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Map ResultSet to Email
    private Email mapResultSet(ResultSet rs) throws SQLException {
        Email email = new Email();
        email.setId(rs.getInt("id"));
        email.setNguoiDungId(rs.getInt("nguoi_dung_id"));
        email.setTieuDe(rs.getString("tieu_de"));
        email.setNoiDung(rs.getString("noi_dung"));
        email.setEmailNguoiNhan(rs.getString("email_nguoi_nhan"));
        email.setLoaiEmail(Email.LoaiEmail.valueOf(rs.getString("loai_email")));
        email.setTrangThai(Email.TrangThai.valueOf(rs.getString("trang_thai")));
        email.setDaGui(rs.getBoolean("da_gui"));
        email.setLaGhim(rs.getBoolean("la_ghim"));
        email.setNgayTao(rs.getTimestamp("ngay_tao"));
        email.setNgayDoc(rs.getTimestamp("ngay_doc"));
        return email;
    }
}