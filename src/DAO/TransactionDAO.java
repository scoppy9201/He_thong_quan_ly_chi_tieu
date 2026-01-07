package DAO;

import Model.Transaction;
import Utils.DBConnection;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
        } catch (SQLException e) {
            System.err.println("Lỗi getAllTransactions: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }   

    // Lấy giao dịch theo ID 
    public Transaction getTransactionById(int id) {
        return getTransactionById(id, false);
    }

    public Transaction getTransactionById(int id, boolean forUpdate) {
        String sql = """
            SELECT gd.*, dm.ten_danh_muc, dm.bieu_tuong 
            FROM giao_dich gd
            LEFT JOIN danh_muc dm ON gd.danh_muc_id = dm.id
            WHERE gd.id = ?
        """ + (forUpdate ? " FOR UPDATE" : "");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getTransactionById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Thêm mới giao dịch 
    public boolean insertTransaction(Transaction gd) {
        String sqlInsert = """
            INSERT INTO giao_dich
            (nguoi_dung_id, danh_muc_id, so_tien, loai_giao_dich, ngay_giao_dich, 
             phuong_thuc, ghi_chu, anh_hoa_don, ngay_tao, ngay_cap_nhat)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                // 1. Insert giao dịch
                pstmt.setInt(1, gd.getNguoiDungId());
                pstmt.setInt(2, gd.getDanhMucId());
                pstmt.setBigDecimal(3, gd.getSoTien());
                pstmt.setString(4, gd.getLoaiGiaoDich().name());
                pstmt.setDate(5, Date.valueOf(gd.getNgayGiaoDich()));
                pstmt.setString(6, gd.getPhuongThuc());
                pstmt.setString(7, gd.getGhiChu());
                pstmt.setString(8, gd.getAnhHoaDon());
                int rows = pstmt.executeUpdate();

                // 2. Update ngân sách 
                if (rows > 0 && gd.getLoaiGiaoDich() == Transaction.LoaiGiaoDich.CHI) {
                    // Bước 1: Cộng vào da_dung
                    String sqlUpdateDaDung = """
                        UPDATE ngan_sach 
                        SET da_dung = da_dung + ?,
                            ngay_cap_nhat = CURRENT_TIMESTAMP
                        WHERE nguoi_dung_id = ? AND danh_muc_id = ?
                    """;

                    try (PreparedStatement ps = conn.prepareStatement(sqlUpdateDaDung)) {
                        ps.setBigDecimal(1, gd.getSoTien());
                        ps.setInt(2, gd.getNguoiDungId());
                        ps.setInt(3, gd.getDanhMucId());
                        ps.executeUpdate();
                    }

                    // Bước 2: Tính lại con_lai
                    String sqlUpdateConLai = """
                        UPDATE ngan_sach 
                        SET con_lai = tong_ngan_sach - da_dung
                        WHERE nguoi_dung_id = ? AND danh_muc_id = ?
                    """;

                    try (PreparedStatement ps = conn.prepareStatement(sqlUpdateConLai)) {
                        ps.setInt(1, gd.getNguoiDungId());
                        ps.setInt(2, gd.getDanhMucId());
                        ps.executeUpdate();
                    }

                    System.out.println("INSERT: Cộng " + gd.getSoTien() + " vào da_dung");
                }

                conn.commit();
                System.out.println("INSERT: Thêm GD và cập nhật budget thành công");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi insert: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật giao dịch 
    public boolean updateTransaction(Transaction gdNew) {
        String sqlUpdate = """
            UPDATE giao_dich SET
            nguoi_dung_id = ?, danh_muc_id = ?, so_tien = ?, loai_giao_dich = ?, 
            ngay_giao_dich = ?, phuong_thuc = ?, ghi_chu = ?, anh_hoa_don = ?, 
            ngay_cap_nhat = CURRENT_TIMESTAMP
            WHERE id = ?
        """;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Lock và lấy thông tin giao dịch cũ
            Transaction gdOld = getTransactionByIdWithLock(conn, gdNew.getId());
            if (gdOld == null) {
                conn.rollback();
                System.err.println("UPDATE: Không tìm thấy giao dịch ID=" + gdNew.getId());
                return false;
            }

            // 2. Update giao dịch
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setInt(1, gdNew.getNguoiDungId());
                ps.setInt(2, gdNew.getDanhMucId());
                ps.setBigDecimal(3, gdNew.getSoTien());
                ps.setString(4, gdNew.getLoaiGiaoDich().name());
                ps.setDate(5, Date.valueOf(gdNew.getNgayGiaoDich()));
                ps.setString(6, gdNew.getPhuongThuc());
                ps.setString(7, gdNew.getGhiChu());
                ps.setString(8, gdNew.getAnhHoaDon());
                ps.setInt(9, gdNew.getId());
                ps.executeUpdate();
            }

            // 3. Update budget nếu là CHI
            if (gdNew.getLoaiGiaoDich() == Transaction.LoaiGiaoDich.CHI) {
                BigDecimal soTienCu = gdOld.getSoTien();
                BigDecimal soTienMoi = gdNew.getSoTien();
                int danhMucCu = gdOld.getDanhMucId();
                int danhMucMoi = gdNew.getDanhMucId();

                // CASE 1: Cùng danh mục
                if (danhMucCu == danhMucMoi) {
                    BigDecimal chenhLech = soTienMoi.subtract(soTienCu);

                    if (chenhLech.compareTo(BigDecimal.ZERO) != 0) {
                        // Bước 1: Update da_dung
                        String sqlUpdateDaDung = """
                            UPDATE ngan_sach 
                            SET da_dung = da_dung + ?
                            WHERE nguoi_dung_id = ? AND danh_muc_id = ?
                        """;

                        try (PreparedStatement ps = conn.prepareStatement(sqlUpdateDaDung)) {
                            ps.setBigDecimal(1, chenhLech);
                            ps.setInt(2, gdNew.getNguoiDungId());
                            ps.setInt(3, danhMucMoi);
                            ps.executeUpdate();
                        }

                        // Bước 2: Tính lại con_lai
                        String sqlUpdateConLai = """
                            UPDATE ngan_sach 
                            SET con_lai = tong_ngan_sach - da_dung
                            WHERE nguoi_dung_id = ? AND danh_muc_id = ?
                        """;

                        try (PreparedStatement ps = conn.prepareStatement(sqlUpdateConLai)) {
                            ps.setInt(1, gdNew.getNguoiDungId());
                            ps.setInt(2, danhMucMoi);
                            ps.executeUpdate();
                        }

                        System.out.println("UPDATE: Cùng danh mục, chênh lệch: " + chenhLech);
                    }
                } 
                // CASE 2: Khác danh mục
                else {
                    // Hoàn trả vào danh mục cũ
                    String sqlRevertDaDung = """
                        UPDATE ngan_sach 
                        SET da_dung = da_dung - ?
                        WHERE nguoi_dung_id = ? AND danh_muc_id = ?
                    """;

                    try (PreparedStatement ps = conn.prepareStatement(sqlRevertDaDung)) {
                        ps.setBigDecimal(1, soTienCu);
                        ps.setInt(2, gdOld.getNguoiDungId());
                        ps.setInt(3, danhMucCu);
                        ps.executeUpdate();
                    }

                    String sqlRevertConLai = """
                        UPDATE ngan_sach 
                        SET con_lai = tong_ngan_sach - da_dung
                        WHERE nguoi_dung_id = ? AND danh_muc_id = ?
                    """;

                    try (PreparedStatement ps = conn.prepareStatement(sqlRevertConLai)) {
                        ps.setInt(1, gdOld.getNguoiDungId());
                        ps.setInt(2, danhMucCu);
                        ps.executeUpdate();
                    }

                    // Trừ từ danh mục mới
                    String sqlAddDaDung = """
                        UPDATE ngan_sach 
                        SET da_dung = da_dung + ?
                        WHERE nguoi_dung_id = ? AND danh_muc_id = ?
                    """;

                    try (PreparedStatement ps = conn.prepareStatement(sqlAddDaDung)) {
                        ps.setBigDecimal(1, soTienMoi);
                        ps.setInt(2, gdNew.getNguoiDungId());
                        ps.setInt(3, danhMucMoi);
                        ps.executeUpdate();
                    }

                    String sqlAddConLai = """
                        UPDATE ngan_sach 
                        SET con_lai = tong_ngan_sach - da_dung
                        WHERE nguoi_dung_id = ? AND danh_muc_id = ?
                    """;

                    try (PreparedStatement ps = conn.prepareStatement(sqlAddConLai)) {
                        ps.setInt(1, gdNew.getNguoiDungId());
                        ps.setInt(2, danhMucMoi);
                        ps.executeUpdate();
                    }

                    System.out.println("UPDATE: Khác danh mục - Hoàn " + soTienCu + ", Trừ " + soTienMoi);
                }
            }

            conn.commit();
            System.out.println("UPDATE: Cập nhật GD và budget thành công");
            return true;

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi update: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Xóa giao dịch 
    public boolean deleteTransaction(int id) {
        String sqlDelete = "DELETE FROM giao_dich WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Lấy thông tin giao dịch trước khi xóa (với lock)
            Transaction trans = getTransactionByIdWithLock(conn, id);
            if (trans == null) {
                conn.rollback();
                System.err.println("DELETE: Không tìm thấy giao dịch ID=" + id);
                return false;
            }

            // 2. Xóa giao dịch
            try (PreparedStatement ps = conn.prepareStatement(sqlDelete)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // 3. Hoàn trả ngân sách (TÁCH 2 BƯỚC)
            if (trans.getLoaiGiaoDich() == Transaction.LoaiGiaoDich.CHI) {
                // Bước 1: Trừ da_dung
                String sqlUpdateDaDung = """
                    UPDATE ngan_sach 
                    SET da_dung = da_dung - ?,
                        ngay_cap_nhat = CURRENT_TIMESTAMP
                    WHERE nguoi_dung_id = ? AND danh_muc_id = ?
                """;

                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateDaDung)) {
                    ps.setBigDecimal(1, trans.getSoTien());
                    ps.setInt(2, trans.getNguoiDungId());
                    ps.setInt(3, trans.getDanhMucId());
                    ps.executeUpdate();
                }

                // Bước 2: Tính lại con_lai
                String sqlUpdateConLai = """
                    UPDATE ngan_sach 
                    SET con_lai = tong_ngan_sach - da_dung
                    WHERE nguoi_dung_id = ? AND danh_muc_id = ?
                """;

                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateConLai)) {
                    ps.setInt(1, trans.getNguoiDungId());
                    ps.setInt(2, trans.getDanhMucId());
                    ps.executeUpdate();
                }

                System.out.println("DELETE: Hoàn trả " + trans.getSoTien() + " vào ngân sách");
            }

            conn.commit();
            System.out.println("DELETE: Xóa GD và cập nhật budget thành công");
            return true;

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi delete: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Tìm kiếm theo từ khóa
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

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi searchTransactions: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    // Lọc theo nhiều tiêu chí
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

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi getTransactionsWithFilters: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // Lấy giao dịch theo user
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
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi getTransactionsByUserId: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    // Chi tiêu theo danh mục (PIE CHART)
    public Map<String, Double> getExpenseByCategory(int userId, int month, int year) {
        Map<String, Double> result = new HashMap<>();

        // Tính ngày đầu và cuối tháng
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);

        String sql = """
            SELECT COALESCE(dm.ten_danh_muc, 'Không xác định') AS ten_danh_muc, 
                   SUM(gd.so_tien) AS total
            FROM giao_dich gd
            LEFT JOIN danh_muc dm ON gd.danh_muc_id = dm.id
            WHERE gd.nguoi_dung_id = ? 
              AND gd.loai_giao_dich = 'CHI'
              AND gd.ngay_giao_dich BETWEEN ? AND ?
            GROUP BY COALESCE(dm.ten_danh_muc, 'Không xác định')
            ORDER BY total DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(firstDay));
            ps.setDate(3, Date.valueOf(lastDay));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("ten_danh_muc"), rs.getDouble("total"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getExpenseByCategory: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    
    // Xu hướng chi tiêu theo tháng (BAR CHART)
    public Map<Integer, Double> getExpenseTrendByMonth(int userId, int year) {
        // Khởi tạo 12 tháng với giá trị 0
        Map<Integer, Double> trend = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) {
            trend.put(i, 0.0);
        }

        // Tính range của cả năm
        LocalDate firstDay = LocalDate.of(year, 1, 1);
        LocalDate lastDay = LocalDate.of(year, 12, 31);

        String sql = """
            SELECT MONTH(ngay_giao_dich) AS month, SUM(so_tien) AS total
            FROM giao_dich
            WHERE nguoi_dung_id = ? 
              AND loai_giao_dich = 'CHI' 
              AND ngay_giao_dich BETWEEN ? AND ?
            GROUP BY MONTH(ngay_giao_dich)
            ORDER BY month
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(firstDay));
            ps.setDate(3, Date.valueOf(lastDay));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    trend.put(rs.getInt("month"), rs.getDouble("total"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getExpenseTrendByMonth: " + e.getMessage());
            e.printStackTrace();
        }

        return trend;
    }
    
    //  Tổng thu tháng
    public double getTotalIncome(int userId, int month, int year) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);

        String sql = """
            SELECT COALESCE(SUM(so_tien), 0) AS total
            FROM giao_dich
            WHERE nguoi_dung_id = ? 
              AND loai_giao_dich = 'THU'
              AND ngay_giao_dich BETWEEN ? AND ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(firstDay));
            ps.setDate(3, Date.valueOf(lastDay));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getTotalIncome: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }
    
    // Tổng chi tháng
    public double getTotalExpense(int userId, int month, int year) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);

        String sql = """
            SELECT COALESCE(SUM(so_tien), 0) AS total
            FROM giao_dich
            WHERE nguoi_dung_id = ? 
              AND loai_giao_dich = 'CHI'
              AND ngay_giao_dich BETWEEN ? AND ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(firstDay));
            ps.setDate(3, Date.valueOf(lastDay));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getTotalExpense: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }
    
    // Tổng thu năm
    public double getTotalIncomeYearly(int userId, int year) {
        LocalDate firstDay = LocalDate.of(year, 1, 1);
        LocalDate lastDay = LocalDate.of(year, 12, 31);

        String sql = """
            SELECT COALESCE(SUM(so_tien), 0) AS total
            FROM giao_dich
            WHERE nguoi_dung_id = ? 
              AND loai_giao_dich = 'THU' 
              AND ngay_giao_dich BETWEEN ? AND ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(firstDay));
            ps.setDate(3, Date.valueOf(lastDay));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getTotalIncomeYearly: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }
    
    // Số lượng giao dịch trong tháng
    public int getTransactionCount(int userId, int month, int year) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);

        String sql = """
            SELECT COUNT(*) AS count
            FROM giao_dich
            WHERE nguoi_dung_id = ? 
              AND ngay_giao_dich BETWEEN ? AND ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(firstDay));
            ps.setDate(3, Date.valueOf(lastDay));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getTransactionCount: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    // Thống kê năm 
    public Map<String, Object> getYearlyStats(int userId, int year) {
        Map<String, Object> stats = new HashMap<>();

        LocalDate firstDay = LocalDate.of(year, 1, 1);
        LocalDate lastDay = LocalDate.of(year, 12, 31);

        // Query tổng hợp tất cả trong 1 lần
        String sql = """
            SELECT 
                SUM(CASE WHEN loai_giao_dich = 'THU' THEN so_tien ELSE 0 END) AS tong_thu,
                SUM(CASE WHEN loai_giao_dich = 'CHI' THEN so_tien ELSE 0 END) AS tong_chi,
                COUNT(CASE WHEN loai_giao_dich = 'CHI' THEN 1 END) AS so_giao_dich_chi
            FROM giao_dich
            WHERE nguoi_dung_id = ? 
              AND ngay_giao_dich BETWEEN ? AND ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(firstDay));
            ps.setDate(3, Date.valueOf(lastDay));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double tongThu = rs.getDouble("tong_thu");
                    double tongChi = rs.getDouble("tong_chi");

                    stats.put("TongThuNam", tongThu);
                    stats.put("TongChiNam", tongChi);
                    stats.put("SoDu", tongThu - tongChi);
                    stats.put("TrungBinhChiMoiThang", tongChi / 12);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getYearlyStats: " + e.getMessage());
            e.printStackTrace();
        }

        // Tìm tháng chi nhiều nhất (query riêng)
        String sqlMaxMonth = """
            SELECT MONTH(ngay_giao_dich) AS thang, SUM(so_tien) AS tong
            FROM giao_dich
            WHERE nguoi_dung_id = ? 
              AND loai_giao_dich = 'CHI' 
              AND ngay_giao_dich BETWEEN ? AND ?
            GROUP BY MONTH(ngay_giao_dich)
            ORDER BY tong DESC
            LIMIT 1
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlMaxMonth)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(firstDay));
            ps.setDate(3, Date.valueOf(lastDay));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("ThangChiLonNhat", rs.getInt("thang"));
                } else {
                    stats.put("ThangChiLonNhat", 0);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm tháng chi lớn nhất: " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }
    
    // Top giao dịch chi tiêu lớn nhất
    public List<Transaction> getTopExpenses(int userId, int limit) {
        List<Transaction> list = new ArrayList<>();
        String sql = """
            SELECT gd.*, dm.ten_danh_muc, dm.bieu_tuong 
            FROM giao_dich gd
            LEFT JOIN danh_muc dm ON gd.danh_muc_id = dm.id
            WHERE gd.nguoi_dung_id = ? AND gd.loai_giao_dich = 'CHI'
            ORDER BY gd.so_tien DESC
            LIMIT ?
        """;
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

           ps.setInt(1, userId);
           ps.setInt(2, limit);

           try (ResultSet rs = ps.executeQuery()) {
               while (rs.next()) {
                   list.add(mapResultSet(rs));
               }
           }

       } catch (SQLException e) {
           System.err.println("Lỗi getTopExpenses: " + e.getMessage());
           e.printStackTrace();
       }
       return list;
    }
    
    // Validate ngân sách (tránh chi vượt)
    private boolean validateBudget(Connection conn, int userId, int categoryId, BigDecimal amount) 
            throws SQLException {
        String sql = "SELECT con_lai FROM ngan_sach WHERE nguoi_dung_id = ? AND danh_muc_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal conLai = rs.getBigDecimal("con_lai");
                    // Cho phép chi vượt tối đa 10% ngân sách 
                    return conLai.compareTo(amount.multiply(new BigDecimal("-0.1"))) >= 0;
                }
            }
        }
        return true; // Nếu chưa có budget thì cho phép
    }

    // Lấy giao dịch (dùng trong transaction)
    private Transaction getTransactionByIdWithLock(Connection conn, int id) throws SQLException {
        String sql = """
            SELECT gd.*, dm.ten_danh_muc, dm.bieu_tuong 
            FROM giao_dich gd
            LEFT JOIN danh_muc dm ON gd.danh_muc_id = dm.id
            WHERE gd.id = ?
            FOR UPDATE
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    // Map ResultSet sang Object Transaction
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