/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.Transaction;
import Utils.DBConnection;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionDAO {

    // Lấy tất cả giao dịch
    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = """
            SELECT gd.*, dm.ten_danh_muc, dm.bieu_tuong 
            FROM giao_dich gd
            LEFT JOIN danh_muc dm ON gd.danh_muc_id = dm.id
            ORDER BY gd.ngay_giao_dich DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }   

    // Lấy giao dịch theo ID
    public Transaction getTransactionById(int id) {
        String sql = """
            SELECT gd.*, dm.ten_danh_muc, dm.bieu_tuong 
            FROM giao_dich gd
            LEFT JOIN danh_muc dm ON gd.danh_muc_id = dm.id
            WHERE gd.id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm giao dịch mới
    public boolean insertTransaction(Transaction gd) {
        String sql = """
            INSERT INTO giao_dich
            (nguoi_dung_id, danh_muc_id, so_tien, loai_giao_dich, ngay_giao_dich, phuong_thuc, ghi_chu, anh_hoa_don, ngay_tao, ngay_cap_nhat)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gd.getNguoiDungId());
            ps.setInt(2, gd.getDanhMucId());
            ps.setBigDecimal(3, gd.getSoTien());
            ps.setString(4, gd.getLoaiGiaoDich().name());
            ps.setDate(5, Date.valueOf(gd.getNgayGiaoDich()));
            ps.setString(6, gd.getPhuongThuc());
            ps.setString(7, gd.getGhiChu());
            ps.setString(8, gd.getAnhHoaDon());

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật giao dịch
    public boolean updateTransaction(Transaction gd) {
        String sql = """
            UPDATE giao_dich SET
            nguoi_dung_id = ?, danh_muc_id = ?, so_tien = ?, loai_giao_dich = ?, ngay_giao_dich = ?, 
            phuong_thuc = ?, ghi_chu = ?, anh_hoa_don = ?, ngay_cap_nhat = CURRENT_TIMESTAMP
            WHERE id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gd.getNguoiDungId());
            ps.setInt(2, gd.getDanhMucId());
            ps.setBigDecimal(3, gd.getSoTien());
            ps.setString(4, gd.getLoaiGiaoDich().name());
            ps.setDate(5, Date.valueOf(gd.getNgayGiaoDich()));
            ps.setString(6, gd.getPhuongThuc());
            ps.setString(7, gd.getGhiChu());
            ps.setString(8, gd.getAnhHoaDon()); 
            ps.setInt(9, gd.getId());

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa giao dịch
    public boolean deleteTransaction(int id) {
        String sql = "DELETE FROM giao_dich WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tìm kiếm theo ghi chú hoặc phương thức, danh mục...
    public List<Transaction> searchTransactions(String keyword) {
        List<Transaction> list = new ArrayList<>();
        String sql = """
            SELECT gd.*, dm.ten_danh_muc, dm.bieu_tuong 
            FROM giao_dich gd
            LEFT JOIN danh_muc dm ON gd.danh_muc_id = dm.id
            WHERE gd.ghi_chu LIKE ? OR gd.phuong_thuc LIKE ? OR dm.ten_danh_muc LIKE ?
            ORDER BY gd.ngay_giao_dich DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String key = "%" + keyword + "%";
            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Lọc theo nhiều tiêu chí kết hợp (combo filter)
    public List<Transaction> getTransactionsWithFilters(int userId, String categoryName, 
                                                         String loaiGiaoDich, String phuongThuc, 
                                                         LocalDate fromDate, LocalDate toDate) {
        List<Transaction> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT gd.*, dm.ten_danh_muc, dm.bieu_tuong 
            FROM giao_dich gd
            LEFT JOIN danh_muc dm ON gd.danh_muc_id = dm.id
            WHERE gd.nguoi_dung_id = ?
        """);

        // Thêm điều kiện lọc động
        if (categoryName != null && !categoryName.isEmpty() && !categoryName.equals("Tất cả")) {
            sql.append(" AND dm.ten_danh_muc = ?");
        }
        if (loaiGiaoDich != null && !loaiGiaoDich.isEmpty() && !loaiGiaoDich.equals("Tất cả")) {
            sql.append(" AND gd.loai_giao_dich = ?");
        }
        if (phuongThuc != null && !phuongThuc.isEmpty() && !phuongThuc.equals("Tất cả")) {
            sql.append(" AND gd.phuong_thuc = ?");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" AND gd.ngay_giao_dich BETWEEN ? AND ?");
        }

        sql.append(" ORDER BY gd.ngay_giao_dich DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            ps.setInt(paramIndex++, userId);

            if (categoryName != null && !categoryName.isEmpty() && !categoryName.equals("Tất cả")) {
                ps.setString(paramIndex++, categoryName);
            }
            if (loaiGiaoDich != null && !loaiGiaoDich.isEmpty() && !loaiGiaoDich.equals("Tất cả")) {
                ps.setString(paramIndex++, loaiGiaoDich);
            }
            if (phuongThuc != null && !phuongThuc.isEmpty() && !phuongThuc.equals("Tất cả")) {
                ps.setString(paramIndex++, phuongThuc);
            }
            if (fromDate != null && toDate != null) {
                ps.setDate(paramIndex++, Date.valueOf(fromDate));
                ps.setDate(paramIndex++, Date.valueOf(toDate));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy giao dịch theo user id 
    public List<Transaction> getTransactionsByUserId(int userId) {
        List<Transaction> list = new ArrayList<>();
        String sql = """
            SELECT gd.*, dm.ten_danh_muc, dm.bieu_tuong 
            FROM giao_dich gd
            LEFT JOIN danh_muc dm ON gd.danh_muc_id = dm.id
            WHERE gd.nguoi_dung_id = ?
            ORDER BY gd.ngay_giao_dich DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy tổng chi tiêu theo danh mục trong tháng/năm cụ thể (cho Pie Char)
    public Map<String, Double> getExpenseByCategory(int userId, int month, int year) {
        Map<String, Double> result = new HashMap<>();
        String sql = """
            SELECT dm.ten_danh_muc, SUM(gd.so_tien) AS total
            FROM giao_dich gd
            JOIN danh_muc dm ON gd.danh_muc_id = dm.id
            WHERE gd.nguoi_dung_id = ? AND gd.loai_giao_dich = 'CHI'
            AND MONTH(gd.ngay_giao_dich) = ? AND YEAR(gd.ngay_giao_dich) = ?
            GROUP BY dm.ten_danh_muc
            ORDER BY total DESC
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("ten_danh_muc"), rs.getDouble("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // Lấy tổng thu nhập cho tháng/năm
    public double getTotalIncome(int userId, int month, int year) {
        String sql = """
            SELECT SUM(so_tien) AS total
            FROM giao_dich
            WHERE nguoi_dung_id = ? AND loai_giao_dich = 'THU'
            AND MONTH(ngay_giao_dich) = ? AND YEAR(ngay_giao_dich) = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    // Lấy tổng chi tiêu trong tháng/năm (cho thống kê)
    public double getTotalExpense(int userId, int month, int year) {
        String sql = """
            SELECT SUM(so_tien) AS total
            FROM giao_dich
            WHERE nguoi_dung_id = ? AND loai_giao_dich = 'CHI'
            AND MONTH(ngay_giao_dich) = ? AND YEAR(ngay_giao_dich) = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    // Lấy số giao dịch trong tháng/năm (cho thống kê)
    public int getTransactionCount(int userId, int month, int year) {
        String sql = """
            SELECT COUNT(*) AS count
            FROM giao_dich
            WHERE nguoi_dung_id = ? AND MONTH(ngay_giao_dich) = ? AND YEAR(ngay_giao_dich) = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Lấy xu hướng chi tiêu theo tháng trong năm (cho Bar Chart)
    public Map<Integer, Double> getExpenseTrendByMonth(int userId, int year) {
        Map<Integer, Double> trend = new HashMap<>();
        String sql = """
            SELECT MONTH(ngay_giao_dich) AS month, SUM(so_tien) AS total
            FROM giao_dich
            WHERE nguoi_dung_id = ? AND loai_giao_dich = 'CHI' AND YEAR(ngay_giao_dich) = ?
            GROUP BY MONTH(ngay_giao_dich)
            ORDER BY month
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                trend.put(rs.getInt("month"), rs.getDouble("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trend;
    }
    
    public Map<String, Object> getYearlyStats(int userId, int year) {
        Map<String, Object> stats = new HashMap<>();
        double totalIncome = 0, totalExpense = 0;
        
        // tính tổng chi trong năm 
        Map<Integer, Double> monthlyExpenses = getExpenseTrendByMonth(userId, year);
        for (double exp : monthlyExpenses.values()) {
            totalExpense += exp;
        }
        // Tương tự query cho totalIncome (thay 'CHI' bằng 'THU')
        String sqlIncome = """
            SELECT SUM(so_tien) AS total
            FROM giao_dich
            WHERE nguoi_dung_id = ? AND loai_giao_dich = 'THU' AND YEAR(ngay_giao_dich) = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlIncome)) {
            ps.setInt(1, userId);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) totalIncome = rs.getDouble("total");
        } catch (Exception e) {
            e.printStackTrace();
        }
        stats.put("TongThuNam", totalIncome);
        stats.put("TongChiNam", totalExpense);
        stats.put("TrungBinhChiMoiThang", totalExpense / 12);
        stats.put("ThangChiLonNhat", monthlyExpenses.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(0));
        return stats;
    }
    
     // Lấy tổng thu nhập trong năm
    public double getTotalIncomeYearly(int userId, int year) {
        String sql = """
            SELECT SUM(so_tien) AS total
            FROM giao_dich
            WHERE nguoi_dung_id = ? AND loai_giao_dich = 'THU' AND YEAR(ngay_giao_dich) = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    // Map ResultSet → GiaoDich
    private Transaction mapResultSet(ResultSet rs) throws SQLException {
        Transaction gd = new Transaction();
        gd.setId(rs.getInt("id"));
        gd.setNguoiDungId(rs.getInt("nguoi_dung_id"));
        gd.setDanhMucId(rs.getInt("danh_muc_id"));
        gd.setSoTien(rs.getBigDecimal("so_tien"));
        gd.setLoaiGiaoDich(Transaction.LoaiGiaoDich.valueOf(rs.getString("loai_giao_dich")));
        gd.setNgayGiaoDich(rs.getDate("ngay_giao_dich").toLocalDate());
        gd.setPhuongThuc(rs.getString("phuong_thuc"));
        gd.setGhiChu(rs.getString("ghi_chu"));
        gd.setNgayTao(rs.getTimestamp("ngay_tao"));
        gd.setNgayCapNhat(rs.getTimestamp("ngay_cap_nhat"));
        gd.setTenDanhMuc(rs.getString("ten_danh_muc"));
        gd.setBieuTuongDanhMuc(rs.getString("bieu_tuong"));
        gd.setAnhHoaDon(rs.getString("anh_hoa_don"));
        return gd;
    }
}

