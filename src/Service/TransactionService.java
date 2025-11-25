/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import DAO.TransactionDAO;
import Model.Transaction;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionService {
    private final TransactionDAO transactionDAO;
    
    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
    }
    
    // Lấy toàn bộ giao dịch
    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
    }
    
    // Lấy giao dịch theo ID
    public Transaction getById(int id) {
        return transactionDAO.getTransactionById(id);
    }
    
    // Thêm giao dịch 
    public boolean create(Transaction gd) {
        if (gd == null) return false;
        if (gd.getSoTien() == null || gd.getSoTien().signum() <= 0) {
            throw new IllegalArgumentException("Số tiền giao dịch phải > 0");
        }
        if (gd.getNgayGiaoDich() == null) {
            gd.setNgayGiaoDich(LocalDate.now());
        }
        return transactionDAO.insertTransaction(gd);
    }
    
    // Cập nhật giao dịch
    public boolean update(Transaction gd) {
        if (gd == null || gd.getId() <= 0) {
            throw new IllegalArgumentException("Giao dịch không hợp lệ để cập nhật");
        }
        return transactionDAO.updateTransaction(gd);
    }
    
    // Xóa giao dịch
    public boolean delete(int id) {
        if (id <= 0) return false;
        return transactionDAO.deleteTransaction(id);
    }
    
    // Tìm kiếm theo keyword
    public List<Transaction> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllTransactions();
        }
        return transactionDAO.searchTransactions(keyword.trim());
    }
    
    // Lấy giao dịch theo người dùng
    public List<Transaction> getByUser(int userId) {
        return transactionDAO.getTransactionsByUserId(userId);
    }
    
    // Lọc theo nhiều tiêu chí kết hợp
    public List<Transaction> getWithFilters(int userId, String categoryName, 
                                            String loaiGiaoDich, String phuongThuc, 
                                            LocalDate fromDate, LocalDate toDate) {
        return transactionDAO.getTransactionsWithFilters(userId, categoryName, loaiGiaoDich, 
                                                          phuongThuc, fromDate, toDate);
    }
    
    // Lấy tổng chi tiêu theo danh mục trong tháng/năm (cho Pie Chart)
    public Map<String, Double> getExpenseByCategory(int userId, int month, int year) {
        return transactionDAO.getExpenseByCategory(userId, month, year);
    }
    
    // Lấy tổng thu nhập trong tháng/năm
    public double getTotalIncome(int userId, int month, int year) {
        return transactionDAO.getTotalIncome(userId, month, year);
    }
    
    // Lấy tổng chi tiêu trong tháng/năm
    public double getTotalExpense(int userId, int month, int year) {
        return transactionDAO.getTotalExpense(userId, month, year);
    }
    
    // Lấy số giao dịch trong tháng/năm
    public int getTransactionCount(int userId, int month, int year) {
        return transactionDAO.getTransactionCount(userId, month, year);
    }
    
    // Lấy xu hướng chi tiêu theo tháng trong năm (cho Bar Chart)
    public Map<Integer, Double> getExpenseTrendByMonth(int userId, int year) {
        return transactionDAO.getExpenseTrendByMonth(userId, year);
    }
    
    // Thống kê tháng (bao gồm tỷ lệ % từng loại)
    public Map<String, Object> getMonthlyStats(int userId, int month, int year) {
        Map<String, Object> stats = new HashMap<>();
        double totalIncome = getTotalIncome(userId, month, year);
        double totalExpense = getTotalExpense(userId, month, year);
        Map<String, Double> expenses = getExpenseByCategory(userId, month, year);
        int transactionCount = getTransactionCount(userId, month, year);
        stats.put("TongThu", totalIncome);
        stats.put("TongChi", totalExpense);
        stats.put("SoGiaoDich", transactionCount);
        stats.put("NhomChiLonNhat", expenses.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Không có"));
        // Tỷ lệ % từng loại (dựa trên tổng chi)
        Map<String, Double> percentages = new HashMap<>();
        if (totalExpense > 0) {
            for (Map.Entry<String, Double> entry : expenses.entrySet()) {
                percentages.put(entry.getKey(), (entry.getValue() / totalExpense) * 100);
            }
        }
        stats.put("TyLeTungLoai", percentages);
        return stats;
    }
    
    // Lấy tổng thu nhập trong năm 
    public double getTotalIncomeYearly(int userId, int year) {
        return transactionDAO.getTotalIncomeYearly(userId, year);
    }
    
    // Thống kê năm (tổng thu/chi, trung bình chi mỗi tháng, tháng chi lớn nhất) - Đã tối ưu
    public Map<String, Object> getYearlyStats(int userId, int year) {
        Map<String, Object> stats = new HashMap<>();
        Map<Integer, Double> monthlyExpenses = getExpenseTrendByMonth(userId, year);
        double totalExpense = monthlyExpenses.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalIncome = getTotalIncomeYearly(userId, year); // Gọi method riêng, chỉ 1 query
        stats.put("TongThuNam", totalIncome);
        stats.put("TongChiNam", totalExpense);
        stats.put("TrungBinhChiMoiThang", totalExpense / 12);
        stats.put("ThangChiLonNhat", monthlyExpenses.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0));
        return stats;
    }
}

