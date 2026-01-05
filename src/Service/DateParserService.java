package Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateParserService {
    
    /**
     * Phân tích chuỗi ngày với nhiều định dạng khác nhau
     */
    public static LocalDate parseFlexibleDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return LocalDate.now();
        }
        
        dateStr = dateStr.trim().toLowerCase();
        
        // 1. Xử lý ngày tương đối
        LocalDate relativeDate = parseRelativeDate(dateStr);
        if (relativeDate != null) {
            return relativeDate;
        }
        
        // 2. Chuẩn hóa định dạng
        String normalized = normalizeDateString(dateStr);
        
        // 3. Thử các định dạng phổ biến
        List<DateTimeFormatter> formatters = getCommonFormatters();
        
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(normalized, formatter);
            } catch (DateTimeParseException e) {
                // Thử format tiếp theo
            }
        }
        
        // 4. Không parse được -> trả về hôm nay
        return LocalDate.now();
    }
    
    /**
     * Xử lý ngày tương đối (hôm nay, hôm qua, tuần trước...)
     */
    private static LocalDate parseRelativeDate(String text) {
        LocalDate today = LocalDate.now();
        
        // Hôm nay
        if (text.matches(".*(hôm nay|bây giờ|hiện tại).*")) {
            return today;
        }
        
        // Hôm qua
        if (text.matches(".*(hôm qua|qua).*")) {
            return today.minusDays(1);
        }
        
        // Hôm kia
        if (text.matches(".*(hôm kia|kia).*")) {
            return today.minusDays(2);
        }
        
        // X ngày trước
        Pattern daysAgoPattern = Pattern.compile("(\\d+)\\s*ngày\\s*(trước|trước đó)");
        Matcher matcher = daysAgoPattern.matcher(text);
        if (matcher.find()) {
            int days = Integer.parseInt(matcher.group(1));
            return today.minusDays(days);
        }
        
        // Tuần trước
        if (text.matches(".*(tuần trước|tuần vừa rồi).*")) {
            return today.minusWeeks(1);
        }
        
        // Tháng trước
        if (text.matches(".*(tháng trước|tháng vừa rồi).*")) {
            return today.minusMonths(1);
        }
        
        // Thứ trong tuần
        if (text.contains("thứ 2") || text.contains("thứ hai")) {
            return getLastDayOfWeek(today, java.time.DayOfWeek.MONDAY);
        }
        if (text.contains("thứ 3") || text.contains("thứ ba")) {
            return getLastDayOfWeek(today, java.time.DayOfWeek.TUESDAY);
        }
        if (text.contains("thứ 4") || text.contains("thứ tư")) {
            return getLastDayOfWeek(today, java.time.DayOfWeek.WEDNESDAY);
        }
        if (text.contains("thứ 5") || text.contains("thứ năm")) {
            return getLastDayOfWeek(today, java.time.DayOfWeek.THURSDAY);
        }
        if (text.contains("thứ 6") || text.contains("thứ sáu")) {
            return getLastDayOfWeek(today, java.time.DayOfWeek.FRIDAY);
        }
        if (text.contains("thứ 7") || text.contains("thứ bảy")) {
            return getLastDayOfWeek(today, java.time.DayOfWeek.SATURDAY);
        }
        if (text.contains("chủ nhật") || text.contains("cn")) {
            return getLastDayOfWeek(today, java.time.DayOfWeek.SUNDAY);
        }
        
        return null;
    }
    
    /**
     * Lấy ngày gần nhất của thứ trong tuần
     */
    private static LocalDate getLastDayOfWeek(LocalDate from, java.time.DayOfWeek targetDay) {
        LocalDate date = from;
        while (date.getDayOfWeek() != targetDay) {
            date = date.minusDays(1);
        }
        return date;
    }
    
    /**
     * Chuẩn hóa chuỗi ngày sang định dạng chuẩn
     */
    private static String normalizeDateString(String dateStr) {
        // Loại bỏ các ký tự không cần thiết
        dateStr = dateStr.replaceAll("[^0-9/-]", "");
        
        // Thay thế các dấu phân cách khác nhau thành "/"
        dateStr = dateStr.replaceAll("[-\\.]", "/");
        
        // Xử lý các trường hợp thiếu số 0 đầu
        // VD: 1/1/2025 -> 01/01/2025
        Pattern pattern = Pattern.compile("(\\d{1,2})/(\\d{1,2})/(\\d{4})");
        Matcher matcher = pattern.matcher(dateStr);
        
        if (matcher.find()) {
            String day = String.format("%02d", Integer.parseInt(matcher.group(1)));
            String month = String.format("%02d", Integer.parseInt(matcher.group(2)));
            String year = matcher.group(3);
            return day + "/" + month + "/" + year;
        }
        
        // Xử lý định dạng ngắn: dd/MM
        Pattern shortPattern = Pattern.compile("(\\d{1,2})/(\\d{1,2})$");
        Matcher shortMatcher = shortPattern.matcher(dateStr);
        
        if (shortMatcher.find()) {
            String day = String.format("%02d", Integer.parseInt(shortMatcher.group(1)));
            String month = String.format("%02d", Integer.parseInt(shortMatcher.group(2)));
            String year = String.valueOf(LocalDate.now().getYear());
            return day + "/" + month + "/" + year;
        }
        
        return dateStr;
    }
    
    /**
     * Danh sách các formatter phổ biến
     */
    private static List<DateTimeFormatter> getCommonFormatters() {
        List<DateTimeFormatter> formatters = new ArrayList<>();
        
        // dd/MM/yyyy
        formatters.add(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        
        // dd-MM-yyyy
        formatters.add(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        
        // dd.MM.yyyy
        formatters.add(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        
        // yyyy-MM-dd (ISO)
        formatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // yyyy/MM/dd
        formatters.add(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        
        // MM/dd/yyyy (US)
        formatters.add(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        
        // ddMMyyyy (không dấu)
        formatters.add(DateTimeFormatter.ofPattern("ddMMyyyy"));
        
        return formatters;
    }
    
    /**
     * Trích xuất ngày từ văn bản tự nhiên
     */
    public static LocalDate extractDateFromText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return LocalDate.now();
        }
        
        text = text.toLowerCase();
        
        // Pattern cho các định dạng ngày
        List<Pattern> datePatterns = new ArrayList<>();
        
        // dd/MM/yyyy hoặc dd-MM-yyyy hoặc dd.MM.yyyy
        Pattern.compile("(\\d{1,2})[\\-/\\.](\\d{1,2})[\\-/\\.](\\d{4})");
        
        // yyyy-MM-dd
        datePatterns.add(Pattern.compile("(\\d{4})[/-](\\d{1,2})[/-](\\d{1,2})"));
        
        // dd/MM (năm hiện tại)
        datePatterns.add(
                        Pattern.compile("(\\d{1,2})[\\-/\\.](\\d{1,2})(?!\\d)")
                        );
        
        for (Pattern pattern : datePatterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String dateStr = matcher.group(0);
                return parseFlexibleDate(dateStr);
            }
        }
        
        // Nếu không tìm thấy pattern ngày, thử relative date
        LocalDate relativeDate = parseRelativeDate(text);
        if (relativeDate != null) {
            return relativeDate;
        }
        
        // Mặc định hôm nay
        return LocalDate.now();
    }
    
    /**
     * Chuyển LocalDate sang format dd/MM/yyyy
     */
    public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    
    /**
     * Chuyển LocalDate sang format yyyy-MM-dd (cho database)
     */
    public static String formatDateForDB(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}