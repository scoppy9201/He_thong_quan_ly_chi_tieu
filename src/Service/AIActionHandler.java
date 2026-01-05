package Service;

import Model.Transaction;
import Model.Category;
import DAO.TransactionDAO;
import DAO.CategoryDAO;
import com.google.gson.JsonObject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class AIActionHandler {
    private final TransactionDAO transactionDAO;
    private final CategoryDAO categoryDAO;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public AIActionHandler() {
        this.transactionDAO = new TransactionDAO();
        this.categoryDAO = new CategoryDAO();
    }
    
    /**
     * Xử lý action từ AI
     */
    public ActionResult xuLyAction(JsonObject actionJson, int nguoiDungId) {
        String action = actionJson.get("action").getAsString();
        
        switch (action) {
            case "ADD":
                return themGiaoDich(actionJson, nguoiDungId);
            case "UPDATE":
                return suaGiaoDich(actionJson, nguoiDungId);
            case "DELETE":
                return xoaGiaoDich(actionJson, nguoiDungId);
            case "QUERY":
                return truyVanThongKe(actionJson, nguoiDungId);
            default:
                return new ActionResult(false, "Action không được hỗ trợ: " + action);
        }
    }
    
    /**
     * Thêm giao dịch mới
     */
    private ActionResult themGiaoDich(JsonObject actionJson, int nguoiDungId) {
        try {
            JsonObject data = actionJson.getAsJsonObject("data");
            
            // Lấy thông tin
            double amount = data.get("amount").getAsDouble();
            String categoryName = data.get("category").getAsString();
            
            // Parse ngày linh hoạt
            LocalDate ngayGiaoDich;
            if (data.has("date")) {
                String dateStr = data.get("date").getAsString();
                ngayGiaoDich = DateParserService.parseFlexibleDate(dateStr);
            } else {
                ngayGiaoDich = LocalDate.now();
            }
            
            String method = data.has("method") ? data.get("method").getAsString() : "Tiền mặt";
            String note = data.has("note") ? data.get("note").getAsString() : "";
            
            // Tìm danh mục
            Category category = categoryDAO.timDanhMucTheoTen(categoryName);
            if (category == null) {
                return new ActionResult(false, "Không tìm thấy danh mục: " + categoryName);
            }
            
            // Tạo giao dịch
            Transaction transaction = new Transaction();
            transaction.setNguoiDungId(nguoiDungId);
            transaction.setDanhMucId(category.getId());
            transaction.setSoTien(BigDecimal.valueOf(amount));
            transaction.setLoaiGiaoDich(
                category.getLoaiDanhMuc().equals("CHI") 
                    ? Transaction.LoaiGiaoDich.CHI 
                    : Transaction.LoaiGiaoDich.THU
            );
            transaction.setNgayGiaoDich(ngayGiaoDich);
            transaction.setPhuongThuc(method);
            transaction.setGhiChu(note);
            
            // Lưu vào DB
            boolean success = transactionDAO.insertTransaction(transaction);
            
            if (success) {
                String message = actionJson.has("message") ? actionJson.get("message").getAsString() 
                    : String.format("Đã thêm %s %,.0fđ - %s ngày %s", 
                        transaction.getLoaiGiaoDich() == Transaction.LoaiGiaoDich.CHI ? "chi tiêu" : "thu nhập",
                        amount, categoryName, DateParserService.formatDate(ngayGiaoDich));
                return new ActionResult(true, message, transaction);
            } else {
                return new ActionResult(false, "Lỗi khi lưu giao dịch");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ActionResult(false, "Lỗi: " + e.getMessage());
        }
    }
    
    /**
     * Sửa giao dịch
     */
    private ActionResult suaGiaoDich(JsonObject actionJson, int nguoiDungId) {
        try {
            JsonObject data = actionJson.getAsJsonObject("data");
            int transactionId = data.get("transactionId").getAsInt();
            
            // Lấy giao dịch hiện tại
            Transaction transaction = transactionDAO.getTransactionById(transactionId);
            if (transaction == null || transaction.getNguoiDungId() != nguoiDungId) {
                return new ActionResult(false, "Không tìm thấy giao dịch");
            }
            
            // Cập nhật các trường
            if (data.has("amount")) {
                transaction.setSoTien(BigDecimal.valueOf(data.get("amount").getAsDouble()));
            }
            if (data.has("category")) {
                Category category = categoryDAO.timDanhMucTheoTen(data.get("category").getAsString());
                if (category != null) {
                    transaction.setDanhMucId(category.getId());
                    transaction.setLoaiGiaoDich(
                        category.getLoaiDanhMuc().equals("CHI") 
                            ? Transaction.LoaiGiaoDich.CHI 
                            : Transaction.LoaiGiaoDich.THU
                    );
                }
            }
            if (data.has("date")) {
                String dateStr = data.get("date").getAsString();
                transaction.setNgayGiaoDich(DateParserService.parseFlexibleDate(dateStr));
            }
            if (data.has("method")) {
                transaction.setPhuongThuc(data.get("method").getAsString());
            }
            if (data.has("note")) {
                transaction.setGhiChu(data.get("note").getAsString());
            }
            
            boolean success = transactionDAO.updateTransaction(transaction);
            
            if (success) {
                String message = actionJson.has("message") ? actionJson.get("message").getAsString() 
                    : "✅ Đã cập nhật giao dịch thành công";
                return new ActionResult(true, message);
            } else {
                return new ActionResult(false, "Lỗi khi cập nhật giao dịch");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ActionResult(false, "Lỗi: " + e.getMessage());
        }
    }
    
    /**
     * Xóa giao dịch
     */
    private ActionResult xoaGiaoDich(JsonObject actionJson, int nguoiDungId) {
        try {
            JsonObject data = actionJson.getAsJsonObject("data");
            int transactionId = data.get("transactionId").getAsInt();
            
            // Kiểm tra quyền
            Transaction transaction = transactionDAO.getTransactionById(transactionId);
            if (transaction == null || transaction.getNguoiDungId() != nguoiDungId) {
                return new ActionResult(false, "Không tìm thấy giao dịch");
            }
            
            boolean success = transactionDAO.deleteTransaction(transactionId);
            
            if (success) {
                String message = actionJson.has("message") ? actionJson.get("message").getAsString() 
                    : "Đã xóa giao dịch thành công";
                return new ActionResult(true, message);
            } else {
                return new ActionResult(false, "Lỗi khi xóa giao dịch");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ActionResult(false, "Lỗi: " + e.getMessage());
        }
    }
    
    /**
     * Truy vấn thống kê
     */
    private ActionResult truyVanThongKe(JsonObject actionJson, int nguoiDungId) {
        try {
            JsonObject data = actionJson.getAsJsonObject("data");
            String queryType = data.get("queryType").getAsString();
            
            LocalDate startDate = LocalDate.parse(data.get("startDate").getAsString(), dateFormatter);
            LocalDate endDate = LocalDate.parse(data.get("endDate").getAsString(), dateFormatter);
            String transactionType = data.has("transactionType") ? data.get("transactionType").getAsString() : "ALL";
            
            StringBuilder report = new StringBuilder();
            
            switch (queryType) {
                case "SUMMARY":
                    report.append(taoThongKeTongHop(nguoiDungId, startDate, endDate));
                    break;
                case "BY_CATEGORY":
                    report.append(taoThongKeTheoDanhMuc(nguoiDungId, startDate, endDate, transactionType));
                    break;
                case "TREND":
                    report.append(taoThongKeTrend(nguoiDungId, startDate, endDate));
                    break;
                case "COMPARE":
                    report.append(taoThongKeSoSanh(nguoiDungId, startDate, endDate));
                    break;
                default:
                    return new ActionResult(false, "❌ Loại query không hỗ trợ: " + queryType);
            }
            
            return new ActionResult(true, report.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ActionResult(false, "Lỗi: " + e.getMessage());
        }
    }
    
    private String taoThongKeTongHop(int nguoiDungId, LocalDate startDate, LocalDate endDate) {
        double tongThu = transactionDAO.getTotalByType(nguoiDungId, startDate, endDate, "THU");
        double tongChi = transactionDAO.getTotalByType(nguoiDungId, startDate, endDate, "CHI");
        double conLai = tongThu - tongChi;
        
        return String.format("""
            📊 TỔNG HỢP TÀI CHÍNH
            Từ %s đến %s
            
            💰 Tổng thu: %,.0fđ
            💸 Tổng chi: %,.0fđ
            📈 Còn lại: %,.0fđ
            %s
            """, 
            startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            tongThu, tongChi, conLai,
            conLai < 0 ? "⚠️ Cảnh báo: Chi nhiều hơn thu!" : "✅ Tình hình tài chính ổn định");
    }
    
    private String taoThongKeTheoDanhMuc(int nguoiDungId, LocalDate startDate, LocalDate endDate, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append("THỐNG KÊ THEO DANH MỤC\n\n");
        
        if (type.equals("CHI") || type.equals("ALL")) {
            Map<String, Double> chiTheoDanhMuc = transactionDAO.getStatsByCategory(nguoiDungId, startDate, endDate, "CHI");
            
            if (!chiTheoDanhMuc.isEmpty()) {
                sb.append("CHI TIÊU:\n");
                chiTheoDanhMuc.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .forEach(entry -> sb.append(String.format("  • %s: %,.0fđ\n", entry.getKey(), entry.getValue())));
                sb.append("\n");
            }
        }
        
        if (type.equals("THU") || type.equals("ALL")) {
            Map<String, Double> thuTheoDanhMuc = transactionDAO.getStatsByCategory(nguoiDungId, startDate, endDate, "THU");
            
            if (!thuTheoDanhMuc.isEmpty()) {
                sb.append("THU NHẬP:\n");
                thuTheoDanhMuc.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .forEach(entry -> sb.append(String.format("  • %s: %,.0fđ\n", entry.getKey(), entry.getValue())));
            }
        }
        
        return sb.toString();
    }
    
    private String taoThongKeTrend(int nguoiDungId, LocalDate startDate, LocalDate endDate) {
        long soNgay = endDate.toEpochDay() - startDate.toEpochDay() + 1;
        double tongChi = transactionDAO.getTotalByType(nguoiDungId, startDate, endDate, "CHI");
        double trungBinhNgay = tongChi / soNgay;
        double duKienThang = trungBinhNgay * 30;
        
        return String.format("""
            📈 XU HƯỚNG CHI TIÊU
            
            📅 Khoảng thời gian: %d ngày
            💸 Tổng chi: %,.0fđ
            📊 Trung bình/ngày: %,.0fđ
            📊 Dự kiến tháng: %,.0fđ
            
            💡 Lời khuyên: %s
            """, 
            soNgay, tongChi, trungBinhNgay, duKienThang,
            duKienThang > 10000000 ? "Nên cân nhắc tiết kiệm chi tiêu" : "Chi tiêu hợp lý");
    }
    
    private String taoThongKeSoSanh(int nguoiDungId, LocalDate startDate, LocalDate endDate) {
        long soNgay = endDate.toEpochDay() - startDate.toEpochDay() + 1;
        LocalDate prevStart = startDate.minusDays(soNgay);
        LocalDate prevEnd = startDate.minusDays(1);
        
        double chiHienTai = transactionDAO.getTotalByType(nguoiDungId, startDate, endDate, "CHI");
        double chiTruoc = transactionDAO.getTotalByType(nguoiDungId, prevStart, prevEnd, "CHI");
        
        double phanTramThayDoi = chiTruoc > 0 ? ((chiHienTai - chiTruoc) / chiTruoc * 100) : 0;
        String trend = phanTramThayDoi > 0 ? "Tăng" : "Giảm";
        String nhanXet = Math.abs(phanTramThayDoi) > 20 ? " Thay đổi đáng kể!" : "Ổn định";
        
        return String.format("""
            📊 SO SÁNH VỚI KỲ TRƯỚC
            
            Kỳ trước: %,.0fđ
            Kỳ này: %,.0fđ
            
            %s %.1f%% %s
            """, 
            chiTruoc, chiHienTai, trend, Math.abs(phanTramThayDoi), nhanXet);
    }
    
    /**
     * Class kết quả
     */
    public static class ActionResult {
        private boolean success;
        private String message;
        private Object data;
        
        public ActionResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public ActionResult(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Object getData() {
            return data;
        }
    }
}