/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import DAO.CategoryDAO;
import Model.Category;
import java.util.List;

/**
 *
 * @author Admin
 */
public class CategoryService {
    private final CategoryDAO categoryDAO;
    
    public CategoryService(){
        this.categoryDAO = new CategoryDAO();
    }
    
    public enum Result {
        SUCCESS,
        INVALID_INPUT,
        NOT_FOUND,
        FAILED
    }
    
    public static class Response {
        public final Result result; 
        public final String message;
        public Response(Result result, String message) {
            this.result = result;
            this.message = message;
        }
    }
    
    // lấy tất cả danh mục 
    public List<Category> getAllCategories(){
        return categoryDAO.getAllCategories();
    }
    
    // lấy danh mục theo id 
    public Category getCategoryById(int id){
        if(id <= 0) return null;
        return categoryDAO.getCategoryById(id);
    }
    
    //Thêm danh muc mới 
    public Response addCategory(Category c) {
        if (c == null || c.getTenDanhMuc() == null || c.getTenDanhMuc().isEmpty()) {
            return new Response(Result.INVALID_INPUT, "Tên danh mục không được để trống.");
        }
        boolean ok = categoryDAO.insertCategory(c);
        return ok ? new Response(Result.SUCCESS, "Thêm danh mục thành công.") :
                    new Response(Result.FAILED, "Thêm danh mục thất bại.");
    }
    
    // Cập nhật danh mục 
    public Response updateCategory(Category c) {
        if (c == null || c.getId() <= 0) {
            return new Response(Result.INVALID_INPUT, "Dữ liệu danh mục không hợp lệ.");
        }
        if (categoryDAO.getCategoryById(c.getId()) == null) {
            return new Response(Result.NOT_FOUND, "Danh mục không tồn tại.");
        }
        boolean ok = categoryDAO.updateCategory(c);
        return ok ? new Response(Result.SUCCESS, "Cập nhật danh mục thành công.") :
                    new Response(Result.FAILED, "Cập nhật danh mục thất bại.");
    }
    
    // Xóa danh mục
    public Response deleteCategory(int id) {
        if (id <= 0) return new Response(Result.INVALID_INPUT, "ID không hợp lệ.");
        if (categoryDAO.getCategoryById(id) == null) {
            return new Response(Result.NOT_FOUND, "Danh mục không tồn tại.");
        }
        boolean ok = categoryDAO.deleteCategory(id);
        return ok ? new Response(Result.SUCCESS, "Xóa danh mục thành công.") :
                    new Response(Result.FAILED, "Xóa danh mục thất bại.");
    }
    
    // Tìm kiếm danh mục theo tên
    public List<Category> searchCategoriesByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCategories(); // Nếu không có từ khóa thì trả về tất cả
        }
        return categoryDAO.searchCategoriesByName(keyword.trim());
    }
}
