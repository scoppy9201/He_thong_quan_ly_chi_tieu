package Service;

import DAO.TransactionDAO;
import Model.Transaction;
import java.math.BigDecimal;
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
        if (id <= 0) {
            throw new IllegalArgumentException("ID giao dịch phải > 0");
        }
        return transactionDAO.getTransactionById(id);
    }
    
    // Thêm giao dịch 
    public boolean create(Transaction gd) {
        // Validation đầy đủ
        if (gd == null) {
            throw new IllegalArgumentException("Giao dịch không được null");
        }
        if (gd.getSoTien() == null || gd.getSoTien().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền giao dịch phải > 0");
        }
        if (gd.getNguoiDungId() <= 0) {
            throw new IllegalArgumentException("Người dùng không hợp lệ");
        }
        if (gd.getDanhMucId() <= 0) {
            throw new IllegalArgumentException("Danh mục không hợp lệ");
        }
        if (gd.getLoaiGiaoDich() == null) {
            throw new IllegalArgumentException("Loại giao dịch không được null");
        }
        if (gd.getNgayGiaoDich() == null) {
            gd.setNgayGiaoDich(LocalDate.now());
        }
        
        return transactionDAO.insertTransaction(gd);
    }
    
    // Cập nhật giao dịch 
    public boolean update(Transaction gd) {
        if (gd == null) {
            throw new IllegalArgumentException("Giao dịch không được null");
        }
        if (gd.getId() <= 0) {
            throw new IllegalArgumentException("ID giao dịch không hợp lệ");
        }
        if (gd.getSoTien() == null || gd.getSoTien().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền giao dịch phải > 0");
        }
        if (gd.getNguoiDungId() <= 0) {
            throw new IllegalArgumentException("Người dùng không hợp lệ");
        }
        if (gd.getDanhMucId() <= 0) {
            throw new IllegalArgumentException("Danh mục không hợp lệ");
        }
        
        return transactionDAO.updateTransaction(gd);
    }
    
    // Xóa giao dịch
    public boolean delete(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID giao dịch phải > 0");
        }
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
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID phải > 0");
        }
        return transactionDAO.getTransactionsByUserId(userId);
    }
    
    // Lọc theo nhiều tiêu chí kết hợp
    public List<Transaction> getWithFilters(int userId, String categoryName, 
                                            String loaiGiaoDich, String phuongThuc, 
                                            LocalDate fromDate, LocalDate toDate) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID phải > 0");
        }
        // Validate date range
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc");
        }
        
        return transactionDAO.getTransactionsWithFilters(userId, categoryName, loaiGiaoDich, 
                                                          phuongThuc, fromDate, toDate);
    }
    
    // Lấy tổng chi tiêu theo danh mục trong tháng/năm (cho Pie Chart)
    public Map<String, Double> getExpenseByCategory(int userId, int month, int year) {
        validateMonthYear(month, year);
        return transactionDAO.getExpenseByCategory(userId, month, year);
    }
    
    // Lấy tổng thu nhập trong tháng/năm
    public double getTotalIncome(int userId, int month, int year) {
        validateMonthYear(month, year);
        return transactionDAO.getTotalIncome(userId, month, year);
    }
    
    // Lấy tổng chi tiêu trong tháng/năm
    public double getTotalExpense(int userId, int month, int year) {
        validateMonthYear(month, year);
        return transactionDAO.getTotalExpense(userId, month, year);
    }
    
    // Lấy số giao dịch trong tháng/năm
    public int getTransactionCount(int userId, int month, int year) {
        validateMonthYear(month, year);
        return transactionDAO.getTransactionCount(userId, month, year);
    }
    
    // Lấy xu hướng chi tiêu theo tháng trong năm (cho Bar Chart)
    public Map<Integer, Double> getExpenseTrendByMonth(int userId, int year) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Năm không hợp lệ");
        }
        return transactionDAO.getExpenseTrendByMonth(userId, year);
    }
    
    // Thống kê tháng
    public Map<String, Object> getMonthlyStats(int userId, int month, int year) {
        validateMonthYear(month, year);
        
        Map<String, Object> stats = new HashMap<>();
        double totalIncome = getTotalIncome(userId, month, year);
        double totalExpense = getTotalExpense(userId, month, year);
        Map<String, Double> expenses = getExpenseByCategory(userId, month, year);
        int transactionCount = getTransactionCount(userId, month, year);
        
        stats.put("TongThu", totalIncome);
        stats.put("TongChi", totalExpense);
        stats.put("SoDu", totalIncome - totalExpense); // Thêm số dư
        stats.put("SoGiaoDich", transactionCount);
        
        // Nhóm chi lớn nhất
        stats.put("NhomChiLonNhat", expenses.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Không có"));
        
        // Tỷ lệ % từng loại (dựa trên tổng chi)
        Map<String, Double> percentages = new HashMap<>();
        if (totalExpense > 0) {
            for (Map.Entry<String, Double> entry : expenses.entrySet()) {
                double percentage = (entry.getValue() / totalExpense) * 100;
                percentages.put(entry.getKey(), Math.round(percentage * 100.0) / 100.0); // Làm tròn 2 chữ số
            }
        }
        stats.put("TyLeTungLoai", percentages);
        
        return stats;
    }
    
    // Lấy tổng thu nhập trong năm 
    public double getTotalIncomeYearly(int userId, int year) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Năm không hợp lệ");
        }
        return transactionDAO.getTotalIncomeYearly(userId, year);
    }
    
    // Thống kê năm 
    public Map<String, Object> getYearlyStats(int userId, int year) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID phải > 0");
        }
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Năm không hợp lệ");
        }
        
        // Gọi trực tiếp DAO để truy vấn trực tiêp
        return transactionDAO.getYearlyStats(userId, year);
    }
    
    // Lấy top N giao dịch chi tiêu lớn nhất 
    public List<Transaction> getTopExpenses(int userId, int limit) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID không hợp lệ");
        }
        if (limit <= 0) {
            limit = 5; // Default 5 items
        }
        return transactionDAO.getTopExpenses(userId, limit);
    }
    
    // Validate tháng và năm
    private void validateMonthYear(int month, int year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Tháng phải từ 1-12");
        }
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Năm không hợp lệ");
        }
    }
}
