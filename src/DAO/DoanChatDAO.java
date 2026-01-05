    package DAO;

    import Model.DoanChat;
    import Utils.DBConnection;
    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class DoanChatDAO {

        public int themDoanChat(DoanChat doanChat) throws ClassNotFoundException {
            String sql = "INSERT INTO doan_chat (nguoi_dung_id, tieu_de, la_ghim) VALUES (?, ?, ?)";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, doanChat.getNguoiDungId());
                ps.setString(2, doanChat.getTieuDe());
                ps.setBoolean(3, doanChat.isLaGhim());

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return -1;
        }

        public List<DoanChat> layDanhSachDoanChat(int nguoiDungId) throws ClassNotFoundException {
            List<DoanChat> list = new ArrayList<>();
            String sql = "SELECT * FROM doan_chat WHERE nguoi_dung_id = ? ORDER BY la_ghim DESC, thu_tu_ghim ASC, ngay_cap_nhat DESC";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, nguoiDungId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    DoanChat dc = new DoanChat();
                    dc.setId(rs.getInt("id"));
                    dc.setNguoiDungId(rs.getInt("nguoi_dung_id"));
                    dc.setTieuDe(rs.getString("tieu_de"));
                    dc.setLaGhim(rs.getBoolean("la_ghim"));

                    Object thuTuObj = rs.getObject("thu_tu_ghim");
                    dc.setThuTuGhim(thuTuObj != null ? (Integer) thuTuObj : null);

                    dc.setNgayTao(rs.getTimestamp("ngay_tao"));
                    dc.setNgayCapNhat(rs.getTimestamp("ngay_cap_nhat"));
                    list.add(dc);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return list;
        }

        public boolean capNhatTieuDe(int doanChatId, String tieuDeMoi) throws ClassNotFoundException {
            String sql = "UPDATE doan_chat SET tieu_de = ? WHERE id = ?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, tieuDeMoi);
                ps.setInt(2, doanChatId);

                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        public boolean ghimDoanChat(int doanChatId, boolean ghim) throws ClassNotFoundException {
            String sql;

            if (ghim) {
                sql = "SELECT COALESCE(MAX(thu_tu_ghim), 0) + 1 as next_order FROM doan_chat WHERE la_ghim = true";
                int nextOrder = 1;

                try (Connection conn = DBConnection.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {

                    if (rs.next()) {
                        nextOrder = rs.getInt("next_order");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                sql = "UPDATE doan_chat SET la_ghim = true, thu_tu_ghim = ? WHERE id = ?";

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setInt(1, nextOrder);
                    ps.setInt(2, doanChatId);
                    return ps.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                sql = "UPDATE doan_chat SET la_ghim = false, thu_tu_ghim = NULL WHERE id = ?";

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setInt(1, doanChatId);
                    return ps.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        public boolean xoaDoanChat(int doanChatId) throws ClassNotFoundException {
            String sql = "DELETE FROM doan_chat WHERE id = ?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, doanChatId);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        public DoanChat layDoanChatTheoId(int id) throws ClassNotFoundException {
            String sql = "SELECT * FROM doan_chat WHERE id = ?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    DoanChat dc = new DoanChat();
                    dc.setId(rs.getInt("id"));
                    dc.setNguoiDungId(rs.getInt("nguoi_dung_id"));
                    dc.setTieuDe(rs.getString("tieu_de"));
                    dc.setLaGhim(rs.getBoolean("la_ghim"));

                    Object thuTuObj = rs.getObject("thu_tu_ghim");
                    dc.setThuTuGhim(thuTuObj != null ? (Integer) thuTuObj : null);

                    dc.setNgayTao(rs.getTimestamp("ngay_tao"));
                    dc.setNgayCapNhat(rs.getTimestamp("ngay_cap_nhat"));
                    return dc;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }