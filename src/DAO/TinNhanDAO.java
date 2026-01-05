package DAO;

import Model.TinNhan;
import Utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TinNhanDAO {
    
    public int themTinNhan(TinNhan tinNhan) throws ClassNotFoundException {
        String sql = "INSERT INTO tin_nhan (doan_chat_id, vai_tro, noi_dung, metadata) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, tinNhan.getDoanChatId());
            ps.setString(2, tinNhan.getVaiTro().name());
            ps.setString(3, tinNhan.getNoiDung());
            ps.setString(4, tinNhan.getMetadata());
            
            ps.executeUpdate();
            
            capNhatThoiGianDoanChat(tinNhan.getDoanChatId());
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public List<TinNhan> layTinNhanTheoDoanChat(int doanChatId) throws ClassNotFoundException {
        List<TinNhan> list = new ArrayList<>();
        String sql = "SELECT * FROM tin_nhan WHERE doan_chat_id = ? ORDER BY ngay_tao ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, doanChatId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                TinNhan tn = new TinNhan();
                tn.setId(rs.getInt("id"));
                tn.setDoanChatId(rs.getInt("doan_chat_id"));
                tn.setVaiTro(TinNhan.VaiTro.valueOf(rs.getString("vai_tro")));
                tn.setNoiDung(rs.getString("noi_dung"));
                tn.setMetadata(rs.getString("metadata"));
                tn.setNgayTao(rs.getTimestamp("ngay_tao"));
                list.add(tn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean capNhatTinNhan(int tinNhanId, String noiDungMoi) throws ClassNotFoundException {
        String sql = "UPDATE tin_nhan SET noi_dung = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, noiDungMoi);
            ps.setInt(2, tinNhanId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean xoaTinNhan(int tinNhanId) throws ClassNotFoundException {
        String sql = "DELETE FROM tin_nhan WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, tinNhanId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public int demTinNhan(int doanChatId) throws ClassNotFoundException {
        String sql = "SELECT COUNT(*) as total FROM tin_nhan WHERE doan_chat_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, doanChatId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private void capNhatThoiGianDoanChat(int doanChatId) throws ClassNotFoundException {
        String sql = "UPDATE doan_chat SET ngay_cap_nhat = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, doanChatId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}