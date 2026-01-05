package Service;

import DAO.TransactionDAO;
import Model.Transaction;
import java.time.*;
import java.util.*;

public class ThongKeService {
    private final TransactionDAO dao = new TransactionDAO();
    
    /**
     * Thống kê chi tiêu linh hoạt
     */
    public String thongKeChiTieu(int userId, String cauHoi) {
        cauHoi = cauHoi.toLowerCase();
        
        // Trích xuất khoảng thời gian từ câu hỏi
        TimeRange timeRange = extractTimeRange(cauHoi);
        
        if (timeRange == null) {
            return "Không thể xác định khoảng thời gian. Vui lòng thử lại!";
        }
        
        // Lấy danh sách giao dịch
        List<Transaction> list = dao.getTransactionsByDateRange(
            userId, 
            timeRange.start, 
            timeRange.end
        );
        
        return tongHopKetQua(list, timeRange);
    }
    
    /**
     * Trích xuất khoảng thời gian từ câu hỏi
     */
    private TimeRange extractTimeRange(String text) {
        LocalDate today = LocalDate.now();
        
        // Hôm nay
        if (text.matches(".*(hôm nay|ngày hôm nay).*")) {
            return new TimeRange(today, today, "hôm nay");
        }
        
        // Hôm qua
        if (text.matches(".*(hôm qua).*")) {
            LocalDate yesterday = today.minusDays(1);
            return new TimeRange(yesterday, yesterday, "hôm qua");
        }
        
        // Tuần này
        if (text.matches(".*(tuần này).*")) {
            LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
            return new TimeRange(startOfWeek, today, "tuần này");
        }
        
        // Tuần trước
        if (text.matches(".*(tuần trước|tuần vừa rồi).*")) {
            LocalDate startOfLastWeek = today.minusWeeks(1).minusDays(today.getDayOfWeek().getValue() - 1);
            LocalDate endOfLastWeek = startOfLastWeek.plusDays(6);
            return new TimeRange(startOfLastWeek, endOfLastWeek, "tuần trước");
        }
        
        // Tháng này
        if (text.matches(".*(tháng này).*")) {
            LocalDate startOfMonth = today.withDayOfMonth(1);
            return new TimeRange(startOfMonth, today, "tháng này");
        }
        
        // Tháng trước
        if (text.matches(".*(tháng trước|tháng vừa rồi).*")) {
            YearMonth lastMonth = YearMonth.from(today).minusMonths(1);
            return new TimeRange(
                lastMonth.atDay(1), 
                lastMonth.atEndOfMonth(), 
                "tháng trước"
            );
        }
        
        // Năm này
        if (text.matches(".*(năm nay|năm này).*")) {
            LocalDate startOfYear = LocalDate.of(today.getYear(), 1, 1);
            return new TimeRange(startOfYear, today, "năm nay");
        }
        
        // Năm trước
        if (text.matches(".*(năm trước|năm ngoái).*")) {
            int lastYear = today.getYear() - 1;
            return new TimeRange(
                LocalDate.of(lastYear, 1, 1),
                LocalDate.of(lastYear, 12, 31),
                "năm trước"
            );
        }
        
        // Ngày cụ thể được trích xuất bằng DateParserService
        LocalDate specificDate = DateParserService.extractDateFromText(text);
        if (specificDate != null && !specificDate.equals(today)) {
            return new TimeRange(
                specificDate, 
                specificDate, 
                DateParserService.formatDate(specificDate)
            );
        }
        
        // Khoảng thời gian "từ ... đến ..."
        TimeRange customRange = extractCustomRange(text);
        if (customRange != null) {
            return customRange;
        }
        
        // Mặc định: tháng hiện tại
        LocalDate startOfMonth = today.withDayOfMonth(1);
        return new TimeRange(startOfMonth, today, "tháng này");
    }
    
    /**
     * Trích xuất khoảng thời gian tùy chỉnh "từ ... đến ..."
     */
    private TimeRange extractCustomRange(String text) {
        // Pattern: "từ dd/MM/yyyy đến dd/MM/yyyy"
        String[] parts = text.split("đến");
        
        if (parts.length == 2) {
            String startPart = parts[0].replace("từ", "").trim();
            String endPart = parts[1].trim();
            
            LocalDate startDate = DateParserService.extractDateFromText(startPart);
            LocalDate endDate = DateParserService.extractDateFromText(endPart);
            
            if (startDate != null && endDate != null) {
                return new TimeRange(
                    startDate, 
                    endDate, 
                    String.format("từ %s đến %s", 
                        DateParserService.formatDate(startDate),
                        DateParserService.formatDate(endDate))
                );
            }
        }
        
        return null;
    }
    
    /**
     * Tổng hợp kết quả thống kê
     */
    private String tongHopKetQua(List<Transaction> list, TimeRange timeRange) {
    if (list.isEmpty()) {
        return String.format("Không có giao dịch nào trong %s.", timeRange.description);
    }

    double tongChi = 0;
    double tongThu = 0;
    Map<String, Double> chiTheoDanhMuc = new LinkedHashMap<>();
    Map<String, Double> thuTheoDanhMuc = new LinkedHashMap<>();

    for (Transaction t : list) {
        double amount = t.getSoTien().doubleValue();

        if (t.getLoaiGiaoDich() == Transaction.LoaiGiaoDich.CHI) {
            tongChi += amount;
            chiTheoDanhMuc.merge(t.getTenDanhMuc(), amount, Double::sum);
        } else {
            tongThu += amount;
            thuTheoDanhMuc.merge(t.getTenDanhMuc(), amount, Double::sum);
        }
    }

    StringBuilder sb = new StringBuilder();
    sb.append(String.format("THỐNG KÊ %s\n\n", timeRange.description.toUpperCase()));

    // Tổng quan
    sb.append("TỔNG QUAN:\n");
    sb.append(String.format("  Tổng thu: %,.0fđ\n", tongThu));
    sb.append(String.format("  Tổng chi: %,.0fđ\n", tongChi));
    sb.append(String.format("  Còn lại: %,.0fđ\n\n", tongThu - tongChi));

    // Chi tiêu theo danh mục
    if (!chiTheoDanhMuc.isEmpty()) {
        sb.append("CHI TIÊU THEO DANH MỤC:\n");
        final double tongChiFinal = tongChi;

        chiTheoDanhMuc.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .forEach(entry -> sb.append(String.format(
                "  • %s: %,.0f (%.1f%%)\n",
                entry.getKey(),
                entry.getValue(),
                (entry.getValue() / tongChiFinal * 100)
            )));
        sb.append("\n");
    }

    // Thu nhập theo danh mục
    if (!thuTheoDanhMuc.isEmpty()) {
        sb.append("THU NHẬP THEO DANH MỤC:\n");
        thuTheoDanhMuc.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .forEach(entry -> sb.append(String.format(
                "  • %s: %,.0fđ\n",
                entry.getKey(),
                entry.getValue()
            )));
    }

    // Đánh giá
    if (tongChi > tongThu) {
        sb.append("\n⚠️ Cảnh báo: Chi tiêu vượt thu nhập!");
    } else if (tongChi < tongThu * 0.5) {
        sb.append("\n✅ Tuyệt vời! Bạn đang tiết kiệm rất tốt!");
    } else {
        sb.append("\n✅ Tình hình tài chính ổn định!");
    }

    return sb.toString();
}

    
    /**
     * Class lưu khoảng thời gian
     */
    private static class TimeRange {
        LocalDate start;
        LocalDate end;
        String description;
        
        TimeRange(LocalDate start, LocalDate end, String description) {
            this.start = start;
            this.end = end;
            this.description = description;
        }
    }
}