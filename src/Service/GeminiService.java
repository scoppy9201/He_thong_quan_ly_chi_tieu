package Service;

import Model.TinNhan;
import Model.Category;
import Model.Transaction;
import DAO.CategoryDAO;
import DAO.TransactionDAO;
import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GeminiService {
    private static final String API_KEY = "AIzaSyAY_e0SYBaAXb3f1ghBHxderhQaD7w5dNM"; 
    private static final String API_URL =
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;
    
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    
    private static final String SYSTEM_PROMPT = """
        Bạn là "Chi Tiêu Assistant" - trợ lý AI thông minh về quản lý tài chính cá nhân.
        
        🎯 NHIỆM VỤ:
        1. Hỗ trợ THÊM/SỬA/XÓA giao dịch chi tiêu và thu nhập
        2. Phân tích chi tiêu, tạo báo cáo thống kê chi tiết
        3. Tư vấn quản lý tài chính, đề xuất tiết kiệm
        4. Cảnh báo chi tiêu vượt mức, dự báo xu hướng
        
        📅 ĐỊNH DẠNG NGÀY:
        - Hệ thống hỗ trợ NHIỀU định dạng ngày linh hoạt
        - Ngày tương đối: "hôm nay", "hôm qua", "3 ngày trước", "tuần trước"
        - Định dạng số: "20/12/2025", "20-12-2025", "2025-12-20", "20.12.2025"
        - Thứ trong tuần: "thứ 2", "thứ ba", "chủ nhật"
        - Khi user nhập ngày, LUÔN chuyển sang định dạng yyyy-MM-dd trong JSON
        
        📊 DANH MỤC HIỆN CÓ SẼ ĐƯỢC CUNG CẤP TRONG CONTEXT
        
        🔄 QUY TRÌNH XỬ LÝ:
        
        **Khi THÊM giao dịch:**
        1. Xác định loại (CHI/THU) và danh mục
        2. Hỏi số tiền (BẮT BUỘC)
        3. Hỏi ngày (mặc định hôm nay)
        4. Hỏi phương thức (mặc định Tiền mặt)
        5. Hỏi ghi chú (tùy chọn)
        6. XÁC NHẬN với user
        7. Trả JSON
        
        **Khi SỬA giao dịch:**
        1. Tìm giao dịch dựa trên mô tả
        2. Hỏi thông tin cần thay đổi
        3. XÁC NHẬN
        4. Trả JSON
        
        **Khi XÓA giao dịch:**
        1. Tìm giao dịch
        2. XÁC NHẬN
        3. Trả JSON
        
        📈 PHÂN TÍCH:
        - Tổng thu/chi theo thời gian
        - Top danh mục chi nhiều nhất
        - So sánh với kỳ trước
        - Dự báo xu hướng
        - Đề xuất tiết kiệm
        
        💡 CÁCH TRẢ LỜI:
        - Thân thiện, ngắn gọn, dùng emoji
        - Số tiền: 50,000đ
        - Ngày: dd/MM/yyyy
        - LUÔN xác nhận trước khi thao tác
        
        🔧 FORMAT JSON:
        
        **THÊM giao dịch:**
        ```json
        {
          "action": "ADD",
          "entity": "TRANSACTION",
          "data": {
            "amount": 50000,
            "category": "Ăn sáng",
            "date": "2025-12-20",
            "method": "Tiền mặt",
            "note": "Phở bò"
          },
          "message": "Đã thêm chi tiêu 50,000đ cho Ăn sáng"
        }
        ```
        
        **SỬA giao dịch:**
        ```json
        {
          "action": "UPDATE",
          "entity": "TRANSACTION",
          "data": {
            "transactionId": 123,
            "amount": 60000,
            "category": "Ăn sáng",
            "date": "2025-12-20",
            "method": "Tiền mặt",
            "note": "Phở bò đặc biệt"
          },
          "message": "Đã cập nhật giao dịch"
        }
        ```
        
        **XÓA giao dịch:**
        ```json
        {
          "action": "DELETE",
          "entity": "TRANSACTION",
          "data": {
            "transactionId": 123
          },
          "message": "Đã xóa giao dịch chi 50,000đ"
        }
        ```
        
        **QUERY thống kê:**
        ```json
        {
          "action": "QUERY",
          "entity": "STATISTICS",
          "data": {
            "queryType": "SUMMARY|BY_CATEGORY|TREND|COMPARE",
            "startDate": "2025-12-01",
            "endDate": "2025-12-20",
            "transactionType": "CHI|THU|ALL"
          }
        }
        ```
        
        ⚠️ LƯU Ý:
        - CHỈ trả JSON khi cần thao tác/query dữ liệu
        - Câu hỏi thường trả văn bản
        - Đảm bảo đủ thông tin trước khi tạo JSON
        - Luôn xác nhận với user
        """;
    
    /**
     * Gửi tin nhắn đến Gemini AI
     */
    public String sendMessage(List<TinNhan> lichSuTinNhan, String tinNhanMoi, int nguoiDungId) throws Exception {
        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        
        // System prompt
        contents.add(taoNoiDung("user", SYSTEM_PROMPT));
        
        // Context danh mục
        String categoryContext = taoContextDanhMuc();
        contents.add(taoNoiDung("user", categoryContext));
        
        // Context chi tiêu hiện tại
        String spendingContext = taoContextChiTieu(nguoiDungId);
        contents.add(taoNoiDung("user", spendingContext));
        
        // Lịch sử hội thoại
        for (TinNhan tn : lichSuTinNhan) {
            String role = tn.getVaiTro() == TinNhan.VaiTro.USER ? "user" : "model";
            contents.add(taoNoiDung(role, tn.getNoiDung()));
        }
        
        // Tin nhắn mới
        contents.add(taoNoiDung("user", tinNhanMoi));
        
        requestBody.add("contents", contents);
        
        // Config
        JsonObject config = new JsonObject();
        config.addProperty("temperature", 0.7);
        config.addProperty("topK", 40);
        config.addProperty("topP", 0.95);
        config.addProperty("maxOutputTokens", 2048);
        requestBody.add("generationConfig", config);
        
        return guiYeuCau(requestBody);
    }
    
    /**
     * Tạo context danh mục từ database
     */
    private String taoContextDanhMuc() {
        List<Category> danhMucChi = categoryDAO.getCategoriesByType("CHI");
        List<Category> danhMucThu = categoryDAO.getCategoriesByType("THU");
        
        StringBuilder sb = new StringBuilder();
        sb.append("📋 DANH SÁCH DANH MỤC TRONG HỆ THỐNG:\n\n");
        
        sb.append("💸 CHI TIÊU:\n");
        for (Category dm : danhMucChi) {
            sb.append("- ").append(dm.getTenDanhMuc()).append("\n");
        }
        
        sb.append("\n💰 THU NHẬP:\n");
        for (Category dm : danhMucThu) {
            sb.append("- ").append(dm.getTenDanhMuc()).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Tạo context tình hình chi tiêu
     */
    private String taoContextChiTieu(int nguoiDungId) {
        LocalDate now = LocalDate.now();
        LocalDate dauThang = now.withDayOfMonth(1);
        
        double tongChi = transactionDAO.getTotalByType(nguoiDungId, dauThang, now, "CHI");
        double tongThu = transactionDAO.getTotalByType(nguoiDungId, dauThang, now, "THU");
        
        Map<String, Double> thongKeChi = transactionDAO.getStatsByCategory(nguoiDungId, dauThang, now, "CHI");
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n💼 TÌNH HÌNH TÀI CHÍNH THÁNG %d/%d:\n", now.getMonthValue(), now.getYear()));
        sb.append(String.format("- Tổng thu: %,.0fđ\n", tongThu));
        sb.append(String.format("- Tổng chi: %,.0fđ\n", tongChi));
        sb.append(String.format("- Còn lại: %,.0fđ\n\n", tongThu - tongChi));
        
        if (!thongKeChi.isEmpty()) {
            sb.append("📊 Top chi tiêu:\n");
            thongKeChi.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .forEach(entry -> sb.append(String.format("  • %s: %,.0fđ\n", entry.getKey(), entry.getValue())));
        }
        
        return sb.toString();
    }
    
    /**
     * Tạo tiêu đề tự động cho đoạn chat
     */
    public String taoTieuDeTuDong(String tinNhanDauTien) throws Exception {
        String prompt = "Tạo tiêu đề ngắn gọn (tối đa 5 từ) cho đoạn hội thoại: \"" + tinNhanDauTien + "\". Chỉ trả về tiêu đề.";
        
        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        contents.add(taoNoiDung("user", prompt));
        requestBody.add("contents", contents);
        
        return guiYeuCau(requestBody).trim();
    }
    
    /**
     * Tạo nội dung message
     */
    private JsonObject taoNoiDung(String role, String text) {
        JsonObject content = new JsonObject();
        content.addProperty("role", role);
        
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        part.addProperty("text", text);
        parts.add(part);
        
        content.add("parts", parts);
        return content;
    }
    
    /**
     * Gửi request đến Gemini API
     */
    private String guiYeuCau(JsonObject requestBody) throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = gson.toJson(requestBody).getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        int responseCode = conn.getResponseCode();
        
        if (responseCode == 200) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                
                JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
                return trinhXuatVanBan(jsonResponse);
            }
        } else {
            throw new Exception("API Error: " + responseCode);
        }
    }
    
    /**
     * Trích xuất văn bản từ response
     */
    private String trinhXuatVanBan(JsonObject response) {
        try {
            JsonArray candidates = response.getAsJsonArray("candidates");
            if (candidates != null && candidates.size() > 0) {
                JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                JsonObject content = firstCandidate.getAsJsonObject("content");
                JsonArray parts = content.getAsJsonArray("parts");
                
                if (parts != null && parts.size() > 0) {
                    return parts.get(0).getAsJsonObject().get("text").getAsString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Xin lỗi, tôi không thể xử lý yêu cầu này.";
    }
    
    /**
     * Phân tích xem response có chứa action JSON không
     */
    public JsonObject phanTichAction(String response) {
        try {
            int start = response.indexOf("{");
            int end = response.lastIndexOf("}");
            
            if (start != -1 && end != -1 && end > start) {
                String jsonStr = response.substring(start, end + 1);
                JsonObject json = gson.fromJson(jsonStr, JsonObject.class);
                
                if (json.has("action") && json.has("entity")) {
                    return json;
                }
            }
        } catch (Exception e) {
            // Không phải JSON
        }
        return null;
    }
}