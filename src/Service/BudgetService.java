/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;
import DAO.BudgetDAO;
import Model.Budget;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


/**
 *
 * @author Admin
 */
public class BudgetService {

    private final BudgetDAO budgetDAO;

    public BudgetService() {
        this.budgetDAO = new BudgetDAO();
    }

    public enum Result {
        SUCCESS,
        INVALID_INPUT,
        NOT_FOUND,
        ALREADY_EXISTS,
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

   

    public Budget getBudgetById(int id) {
        return budgetDAO.getBudgetById(id);
    }

    public Budget getBudgetByUserAndCategory(int userId, int danhMucId) {
        return budgetDAO.getBudgetByUserAndCategory(userId, danhMucId);
    }

    public List<Budget> getAllBudgetsByUser(int userId) {
        return budgetDAO.getAllBudgetsByUser(userId);
    }

    /* ================= CREATE ================= */

    public Response createBudget(int userId, Integer danhMucId, String tenDanhMuc,
                                 String loaiDanhMuc, BigDecimal tongNganSach,
                                 String kyHan, String ghiChu) {

        if (userId <= 0) {
            return new Response(Result.INVALID_INPUT, "User không hợp lệ");
        }

        if (danhMucId == null) {
            return new Response(Result.INVALID_INPUT, "Danh mục không hợp lệ");
        }

        if (tongNganSach == null || tongNganSach.compareTo(BigDecimal.ZERO) <= 0) {
            return new Response(Result.INVALID_INPUT, "Ngân sách phải lớn hơn 0");
        }

        
        Budget existing = budgetDAO.getBudgetByUserAndCategory(userId, danhMucId);
        if (existing != null) {
            return new Response(Result.ALREADY_EXISTS,
                    "Danh mục này đã có ngân sách, vui lòng cập nhật");
        }

        Budget budget = new Budget(
                userId,
                danhMucId,
                tenDanhMuc,
                loaiDanhMuc,
                tongNganSach
        );

        budget.setKyHan(kyHan != null ? kyHan : "Tháng");
        budget.setGhiChu(ghiChu);
        budget.setTrangThai("ACTIVE");

        setDateRangeBasedOnKyHan(budget, budget.getKyHan());

        boolean success = budgetDAO.insertBudget(budget);

        if (success) {
            budgetDAO.updateDaDung(userId, danhMucId);
            return new Response(Result.SUCCESS, "Tạo ngân sách thành công");
        }

        return new Response(Result.FAILED, "Tạo ngân sách thất bại");
    }

   // Cập nhật ngân sách
    public Response updateBudget(Budget budget) {

        if (budget == null || budget.getId() <= 0) {
            return new Response(Result.INVALID_INPUT, "Ngân sách không hợp lệ");
        }

        Budget old = budgetDAO.getBudgetById(budget.getId());
        if (old == null) {
            return new Response(Result.NOT_FOUND, "Không tìm thấy ngân sách");
        }

        if (budget.getTongNganSach().compareTo(BigDecimal.ZERO) <= 0) {
            return new Response(Result.INVALID_INPUT,
                    "Ngân sách phải lớn hơn 0");
        }

        // Điều kiện 
        if (budget.getTongNganSach().compareTo(old.getDaDung()) < 0) {
            return new Response(Result.INVALID_INPUT,
                    "Ngân sách không được nhỏ hơn số tiền đã chi");
        }

        budget.updateConLai();

        boolean success = budgetDAO.updateBudget(budget);

        return success
                ? new Response(Result.SUCCESS, "Cập nhật ngân sách thành công")
                : new Response(Result.FAILED, "Cập nhật ngân sách thất bại");
    }

   // Xóa 

    public Response deleteBudget(int id) {

        Budget existing = budgetDAO.getBudgetById(id);
        if (existing == null) {
            return new Response(Result.NOT_FOUND, "Không tìm thấy ngân sách");
        }

        boolean success = budgetDAO.deleteBudget(id);

        return success
                ? new Response(Result.SUCCESS, "Xóa ngân sách thành công")
                : new Response(Result.FAILED, "Xóa ngân sách thất bại");
    }


    public void refreshSpentAmount(int userId, int danhMucId) {
        budgetDAO.updateDaDung(userId, danhMucId);
    }

    //  Hiển thị tổng ngân sách
    public BigDecimal getTongNganSachTheoCha(int userId, int danhMucChaId) {
        return budgetDAO.getTongNganSachTheoCha(userId, danhMucChaId);
    }

    /* ================= WARNING ================= */

    public String getBudgetWarning(Budget budget) {

        if (budget == null) return "";

        double percent = budget.getPhanTramDaDung();

        if (budget.isVuotNganSach()) {
            return String.format("⚠️ VƯỢT NGÂN SÁCH (%.1f%%)", percent);
        } else if (percent >= 90) {
            return String.format("⚠️ Sắp vượt (%.1f%%)", percent);
        } else if (percent >= 70) {
            return String.format("⚠️ Cảnh báo (%.1f%%)", percent);
        } else if (percent >= 50) {
            return String.format("ℹ️ Đã dùng %.1f%%", percent);
        }

        return String.format("✓ An toàn (%.1f%%)", percent);
    }

    public java.awt.Color getWarningColor(Budget budget) {

        if (budget == null) return java.awt.Color.GRAY;

        double percent = budget.getPhanTramDaDung();

        if (budget.isVuotNganSach() || percent >= 90) {
            return new java.awt.Color(220, 53, 69);
        } else if (percent >= 70) {
            return new java.awt.Color(255, 193, 7);
        } else if (percent >= 50) {
            return new java.awt.Color(0, 123, 255);
        }

        return new java.awt.Color(40, 167, 69);
    }

    /* ================= DATE RANGE ================= */

    private void setDateRangeBasedOnKyHan(Budget budget, String kyHan) {

        LocalDateTime now = LocalDateTime.now();

        if ("Tháng".equals(kyHan)) {
            LocalDate first = LocalDate.now().withDayOfMonth(1);
            LocalDate last = first.plusMonths(1).minusDays(1);
            budget.setNgayBatDau(Timestamp.valueOf(first.atStartOfDay()));
            budget.setNgayKetThuc(Timestamp.valueOf(last.atTime(23, 59, 59)));
        } else if ("Quý".equals(kyHan)) {
            int m = LocalDate.now().getMonthValue();
            int startMonth = ((m - 1) / 3) * 3 + 1;
            LocalDate first = LocalDate.now().withMonth(startMonth).withDayOfMonth(1);
            LocalDate last = first.plusMonths(3).minusDays(1);
            budget.setNgayBatDau(Timestamp.valueOf(first.atStartOfDay()));
            budget.setNgayKetThuc(Timestamp.valueOf(last.atTime(23, 59, 59)));
        } else if ("Năm".equals(kyHan)) {
            LocalDate first = LocalDate.now().withDayOfYear(1);
            LocalDate last = LocalDate.now().withMonth(12).withDayOfMonth(31);
            budget.setNgayBatDau(Timestamp.valueOf(first.atStartOfDay()));
            budget.setNgayKetThuc(Timestamp.valueOf(last.atTime(23, 59, 59)));
        } else {
            budget.setNgayBatDau(Timestamp.valueOf(now));
            budget.setNgayKetThuc(Timestamp.valueOf(now.plusMonths(1)));
        }
    }
}
