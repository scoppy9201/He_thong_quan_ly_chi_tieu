/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.Category;
import Utils.DBConnection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 *
 * @author Admin
 */
public class CategoryDAO {
    // Lấy tất cả danh mục 
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM danh_muc ORDER BY thu_tu_hien_thi ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Category c = mapResultSet(rs);
                list.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    } 
    
    // Lấy danh mục theo ID
    public Category getCategoryById(int id) {
        String sql = "SELECT * FROM danh_muc WHERE id = ?";
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
    
    // Thêm danh mục mới
    public boolean insertCategory(Category c) {
        String sql = """
            INSERT INTO danh_muc
            (ten_danh_muc, loai_danh_muc, danh_muc_cha_id, bieu_tuong, thu_tu_hien_thi, cap_do, trang_thai, mo_ta, ngay_tao, ngay_cap_nhat)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getTenDanhMuc());
            ps.setString(2, c.getLoaiDanhMuc());
            if (c.getDanhMucChaId() != null) {
                ps.setInt(3, c.getDanhMucChaId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setString(4, c.getBieuTuong());
            ps.setInt(5, c.getThuTuHienThi());
            ps.setInt(6, c.getCapDo());
            ps.setString(7, c.getTrangThai());
            ps.setString(8, c.getMoTa());

            int affected = ps.executeUpdate();
            return affected == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật danh mục
    public boolean updateCategory(Category c) {
        String sql = """
            UPDATE danh_muc
            SET ten_danh_muc = ?, loai_danh_muc = ?, danh_muc_cha_id = ?, bieu_tuong = ?, thu_tu_hien_thi = ?, cap_do = ?, trang_thai = ?, mo_ta = ?, ngay_cap_nhat = CURRENT_TIMESTAMP
            WHERE id = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getTenDanhMuc());
            ps.setString(2, c.getLoaiDanhMuc());
            if (c.getDanhMucChaId() != null) {
                ps.setInt(3, c.getDanhMucChaId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setString(4, c.getBieuTuong());
            ps.setInt(5, c.getThuTuHienThi());
            ps.setInt(6, c.getCapDo());
            ps.setString(7, c.getTrangThai());
            ps.setString(8, c.getMoTa());
            ps.setInt(9, c.getId());

            int affected = ps.executeUpdate();
            return affected == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa danh mục theo ID
    public boolean deleteCategory(int id) {
        String sql = "DELETE FROM danh_muc WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            return affected == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tìm kiếm danh mục theo tên 
    public List<Category> searchCategoriesByName(String keyword) {
        List<Category> result = new ArrayList<>();
        String sql = "SELECT * FROM danh_muc WHERE ten_danh_muc LIKE ? ORDER BY thu_tu_hien_thi";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                c.setTenDanhMuc(rs.getString("ten_danh_muc"));
                c.setLoaiDanhMuc(rs.getString("loai_danh_muc"));
                c.setDanhMucChaId(rs.getObject("danh_muc_cha_id", Integer.class));
                c.setThuTuHienThi(rs.getInt("thu_tu_hien_thi"));
                c.setCapDo(rs.getInt("cap_do"));
                c.setMoTa(rs.getString("mo_ta"));
                c.setTrangThai(rs.getString("trang_thai"));
                c.setBieuTuong(rs.getString("bieu_tuong"));
                result.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // Map ResultSet sang Category
    private Category mapResultSet(ResultSet rs) throws SQLException{
        Category c = new Category();
        c.setId(rs.getInt("id"));
        c.setTenDanhMuc(rs.getString("ten_danh_muc"));
        c.setLoaiDanhMuc(rs.getString("loai_danh_muc"));
        int parentId = rs.getInt("danh_muc_cha_id");
        if (!rs.wasNull()) c.setDanhMucChaId(parentId);
        c.setBieuTuong(rs.getString("bieu_tuong"));
        c.setThuTuHienThi(rs.getInt("thu_tu_hien_thi"));
        c.setCapDo(rs.getInt("cap_do"));
        c.setTrangThai(rs.getString("trang_thai"));
        c.setMoTa(rs.getString("mo_ta"));
        c.setNgayTao(rs.getString("ngay_tao"));
        c.setNgayCapNhat(rs.getString("ngay_cap_nhat"));
        return c;
    }

// Lấy danh mục theo loại (CHI/THU)
public List<Category> getCategoriesByType(String loaiDanhMuc) {
    List<Category> list = new ArrayList<>();
    String sql = "SELECT * FROM danh_muc WHERE loai_danh_muc = ? AND trang_thai = 'ACTIVE' ORDER BY thu_tu_hien_thi";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, loaiDanhMuc);
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            list.add(mapResultSet(rs));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}
    
// Tìm danh mục theo tên (cho AI)
public Category timDanhMucTheoTen(String tenDanhMuc) {
    String sql = "SELECT * FROM danh_muc WHERE ten_danh_muc LIKE ? AND trang_thai = 'ACTIVE' LIMIT 1";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, "%" + tenDanhMuc + "%");
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            return mapResultSet(rs);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}
}
