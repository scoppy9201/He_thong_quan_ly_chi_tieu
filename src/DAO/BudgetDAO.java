/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.Budget;
import Utils.DBConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class BudgetDAO {
    
    // Lấy ngân sách theo ID
    public Budget getBudgetById(int id) {
        String sql = """
            SELECT ns.*, dm.bieu_tuong, dm.danh_muc_cha_id,
                   (SELECT ten_danh_muc FROM danh_muc WHERE id = dm.danh_muc_cha_id) as ten_danh_muc_cha
            FROM ngan_sach ns
            LEFT JOIN danh_muc dm ON ns.danh_muc_id = dm.id
            WHERE ns.id = ?
        """;
        
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
    
    // Lấy ngân sách theo user và danh mục
    public Budget getBudgetByUserAndCategory(int userId, int danhMucId) {
        String sql = """
            SELECT ns.*, dm.bieu_tuong, dm.danh_muc_cha_id,
                   (SELECT ten_danh_muc FROM danh_muc WHERE id = dm.danh_muc_cha_id) as ten_danh_muc_cha
            FROM ngan_sach ns
            LEFT JOIN danh_muc dm ON ns.danh_muc_id = dm.id
            WHERE ns.nguoi_dung_id = ? AND ns.danh_muc_id = ?
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, danhMucId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Lấy tất cả ngân sách của user
    public List<Budget> getAllBudgetsByUser(int userId) {
        List<Budget> list = new ArrayList<>();
        String sql = """
            SELECT ns.*, dm.bieu_tuong, dm.danh_muc_cha_id,
                   (SELECT ten_danh_muc FROM danh_muc WHERE id = dm.danh_muc_cha_id) as ten_danh_muc_cha
            FROM ngan_sach ns
            LEFT JOIN danh_muc dm ON ns.danh_muc_id = dm.id
            WHERE ns.nguoi_dung_id = ? AND ns.trang_thai = 'ACTIVE'
            ORDER BY ns.ngay_cap_nhat DESC
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
    
    // Thêm ngân sách mới
    public boolean insertBudget(Budget budget) {
        String sql = """
            INSERT INTO ngan_sach 
            (nguoi_dung_id, danh_muc_id, ten_danh_muc, loai_danh_muc, 
             tong_ngan_sach, da_dung, con_lai, ky_han, ngay_bat_dau, 
             ngay_ket_thuc, trang_thai, ghi_chu)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, budget.getNguoiDungId());
            if (budget.getDanhMucId() != null) {
                ps.setInt(2, budget.getDanhMucId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, budget.getTenDanhMuc());
            ps.setString(4, budget.getLoaiDanhMuc());
            ps.setBigDecimal(5, budget.getTongNganSach());
            ps.setBigDecimal(6, budget.getDaDung());
            ps.setBigDecimal(7, budget.getConLai());
            ps.setString(8, budget.getKyHan());
            ps.setTimestamp(9, budget.getNgayBatDau());
            ps.setTimestamp(10, budget.getNgayKetThuc());
            ps.setString(11, budget.getTrangThai());
            ps.setString(12, budget.getGhiChu());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Cập nhật ngân sách
    public boolean updateBudget(Budget budget) {
        String sql = """
            UPDATE ngan_sach 
            SET ten_danh_muc = ?, loai_danh_muc = ?, tong_ngan_sach = ?, 
                da_dung = ?, con_lai = ?, ky_han = ?, ngay_bat_dau = ?, 
                ngay_ket_thuc = ?, trang_thai = ?, ghi_chu = ?
            WHERE id = ?
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, budget.getTenDanhMuc());
            ps.setString(2, budget.getLoaiDanhMuc());
            ps.setBigDecimal(3, budget.getTongNganSach());
            ps.setBigDecimal(4, budget.getDaDung());
            ps.setBigDecimal(5, budget.getConLai());
            ps.setString(6, budget.getKyHan());
            ps.setTimestamp(7, budget.getNgayBatDau());
            ps.setTimestamp(8, budget.getNgayKetThuc());
            ps.setString(9, budget.getTrangThai());
            ps.setString(10, budget.getGhiChu());
            ps.setInt(11, budget.getId());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Xóa ngân sách (soft delete)
    public boolean deleteBudget(int id) {
        String sql = "UPDATE ngan_sach SET trang_thai = 'INACTIVE' WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Cập nhật số tiền đã dùng dựa trên giao dịch
    public boolean updateDaDung(int userId, int danhMucId) {
        String sql = """
            UPDATE ngan_sach ns
            SET da_dung = (
                SELECT COALESCE(SUM(gd.so_tien), 0)
                FROM giao_dich gd
                WHERE gd.nguoi_dung_id = ns.nguoi_dung_id
                AND gd.danh_muc_id = ns.danh_muc_id
                AND gd.loai_giao_dich = 'CHI'
                AND (ns.ngay_bat_dau IS NULL OR gd.ngay_giao_dich >= DATE(ns.ngay_bat_dau))
                AND (ns.ngay_ket_thuc IS NULL OR gd.ngay_giao_dich <= DATE(ns.ngay_ket_thuc))
            ),
            con_lai = tong_ngan_sach - (
                SELECT COALESCE(SUM(gd.so_tien), 0)
                FROM giao_dich gd
                WHERE gd.nguoi_dung_id = ns.nguoi_dung_id
                AND gd.danh_muc_id = ns.danh_muc_id
                AND gd.loai_giao_dich = 'CHI'
                AND (ns.ngay_bat_dau IS NULL OR gd.ngay_giao_dich >= DATE(ns.ngay_bat_dau))
                AND (ns.ngay_ket_thuc IS NULL OR gd.ngay_giao_dich <= DATE(ns.ngay_ket_thuc))
            )
            WHERE nguoi_dung_id = ? AND danh_muc_id = ?
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, danhMucId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Lấy tổng ngân sách theo danh mục cha
    public BigDecimal getTongNganSachTheoCha(int userId, int danhMucChaId) {
        String sql = """
            SELECT COALESCE(SUM(ns.tong_ngan_sach), 0) as tong
            FROM ngan_sach ns
            JOIN danh_muc dm ON ns.danh_muc_id = dm.id
            WHERE ns.nguoi_dung_id = ? 
            AND dm.danh_muc_cha_id = ? 
            AND ns.trang_thai = 'ACTIVE'
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, danhMucChaId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("tong");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    // Lấy danh sách ngân sách sắp hết hạn
    public List<Budget> getBudgetsExpiringSoon(int userId, int days) {
        List<Budget> list = new ArrayList<>();
        String sql = """
            SELECT ns.*, dm.bieu_tuong, dm.danh_muc_cha_id,
                   (SELECT ten_danh_muc FROM danh_muc WHERE id = dm.danh_muc_cha_id) as ten_danh_muc_cha
            FROM ngan_sach ns
            LEFT JOIN danh_muc dm ON ns.danh_muc_id = dm.id
            WHERE ns.nguoi_dung_id = ? 
            AND ns.trang_thai = 'ACTIVE'
            AND ns.ngay_ket_thuc IS NOT NULL
            AND DATEDIFF(ns.ngay_ket_thuc, NOW()) BETWEEN 0 AND ?
            ORDER BY ns.ngay_ket_thuc ASC
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, days);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    private Budget mapResultSet(ResultSet rs) throws SQLException {
        Budget budget = new Budget();
        budget.setId(rs.getInt("id"));
        budget.setNguoiDungId(rs.getInt("nguoi_dung_id"));
        budget.setDanhMucId(rs.getObject("danh_muc_id", Integer.class));
        budget.setTenDanhMuc(rs.getString("ten_danh_muc"));
        budget.setLoaiDanhMuc(rs.getString("loai_danh_muc"));
        budget.setTongNganSach(rs.getBigDecimal("tong_ngan_sach"));
        budget.setDaDung(rs.getBigDecimal("da_dung"));
        budget.setConLai(rs.getBigDecimal("con_lai"));
        budget.setKyHan(rs.getString("ky_han"));
        budget.setNgayBatDau(rs.getTimestamp("ngay_bat_dau"));
        budget.setNgayKetThuc(rs.getTimestamp("ngay_ket_thuc"));
        budget.setTrangThai(rs.getString("trang_thai"));
        budget.setGhiChu(rs.getString("ghi_chu"));
        budget.setNgayTao(rs.getTimestamp("ngay_tao"));
        budget.setNgayCapNhat(rs.getTimestamp("ngay_cap_nhat"));
        
        // Thông tin từ JOIN
        try {
            budget.setBieuTuongDanhMuc(rs.getString("bieu_tuong"));
            budget.setDanhMucChaId(rs.getObject("danh_muc_cha_id", Integer.class));
            budget.setTenDanhMucCha(rs.getString("ten_danh_muc_cha"));
        } catch (SQLException e) {
            // Ignore if columns don't exist
        }
        
        return budget;
    }
}
