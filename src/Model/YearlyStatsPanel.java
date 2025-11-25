package Model;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel hiển thị thống kê chi tiêu theo năm.
 * Tối ưu hóa: Thêm getData() để lấy dữ liệu, cải thiện thread-safe, xử lý lỗi, và làm UI mượt hơn.
 */
public class YearlyStatsPanel extends JPanel {

    private JLabel lblTongThuNam;
    private JLabel lblTongChiNam;
    private JLabel lblTrungBinhChi;
    private JLabel lblThangChiLonNhat;
    private DecimalFormat formatter;
    private JLabel lblIcon;
    private JTextArea txtSummary;
    private Map<String, Object> currentStats; // Lưu trữ dữ liệu hiện tại để getData()

    public YearlyStatsPanel() {
        formatter = new DecimalFormat("#,##0");
        currentStats = new HashMap<>(); // Khởi tạo rỗng
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel lblTitle = new JLabel("Thống kê chi tiêu năm");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(lblTitle, BorderLayout.NORTH);

        // Panel chứa các stat cards
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 10));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Tạo các stat card
        JPanel cardTongThu = createStatCard("Tổng thu năm", "0 ₫", new Color(40, 167, 69));
        JPanel cardTongChi = createStatCard("Tổng chi năm", "0 ₫", new Color(220, 53, 69));
        JPanel cardTrungBinh = createStatCard("Trung bình chi/tháng", "0 ₫", new Color(0, 123, 255));
        JPanel cardThangLonNhat = createStatCard("Tháng chi lớn nhất", "N/A", new Color(255, 193, 7));

        lblTongThuNam = (JLabel) cardTongThu.getComponent(2);
        lblTongChiNam = (JLabel) cardTongChi.getComponent(2);
        lblTrungBinhChi = (JLabel) cardTrungBinh.getComponent(2);
        lblThangChiLonNhat = (JLabel) cardThangLonNhat.getComponent(2);

        statsPanel.add(cardTongThu);
        statsPanel.add(cardTongChi);
        statsPanel.add(cardTrungBinh);
        statsPanel.add(cardThangLonNhat);

        add(statsPanel, BorderLayout.CENTER);

        // Panel tổng kết
        createSummaryPanel();
    }

    /**
     * Tạo panel tổng kết.
     */
    private void createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new BorderLayout(10, 10));
        summaryPanel.setBackground(new Color(248, 249, 250));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerPanel.setBackground(new Color(248, 249, 250));

        lblIcon = new JLabel();
        setIcon(lblIcon, "/resources/chart.png");
        lblIcon.setPreferredSize(new Dimension(20, 20)); // Kích thước cố định

        JLabel lblSummaryTitle = new JLabel("Tổng kết năm");
        lblSummaryTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));

        headerPanel.add(lblIcon);
        headerPanel.add(lblSummaryTitle);

        // Content text
        txtSummary = new JTextArea(3, 0);
        txtSummary.setEditable(false);
        txtSummary.setBackground(new Color(248, 249, 250));
        txtSummary.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSummary.setLineWrap(true);
        txtSummary.setWrapStyleWord(true);
        txtSummary.setText("Chọn năm và nhấn 'Áp dụng' để xem thống kê chi tiết");
        txtSummary.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        summaryPanel.add(headerPanel, BorderLayout.NORTH);
        summaryPanel.add(txtSummary, BorderLayout.CENTER);

        add(summaryPanel, BorderLayout.SOUTH);
    }

    /**
     * Tạo một stat card với title, value, và màu.
     */
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(Color.GRAY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblValue.setForeground(color);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(lblValue);

        return card;
    }

    /**
     * Cập nhật dữ liệu thống kê năm từ backend.
     * @param stats Map chứa: TongThuNam, TongChiNam, TrungBinhChiMoiThang, ThangChiLonNhat
     * Đảm bảo gọi từ EDT hoặc dùng SwingUtilities.invokeLater().
     */
    public void updateData(Map<String, Object> stats) {
        if (stats == null || stats.isEmpty()) {
            clearData();
            return;
        }

        try {
            // Cập nhật currentStats để lưu trữ
            currentStats.clear();
            currentStats.putAll(stats);

            // Cập nhật UI
            double tongThuNam = safeDouble(stats.get("TongThuNam"));
            double tongChiNam = safeDouble(stats.get("TongChiNam"));
            double trungBinhChi = safeDouble(stats.get("TrungBinhChiMoiThang"));
            int thangChiLonNhat = safeInt(stats.get("ThangChiLonNhat"));

            lblTongThuNam.setText(formatter.format(tongThuNam) + " ₫");
            lblTongChiNam.setText(formatter.format(tongChiNam) + " ₫");
            lblTrungBinhChi.setText(formatter.format(trungBinhChi) + " ₫");
            lblThangChiLonNhat.setText(thangChiLonNhat > 0 ? "Tháng " + thangChiLonNhat : "N/A");

            // Cập nhật summary
            updateSummary(tongThuNam, tongChiNam, trungBinhChi, thangChiLonNhat);

            revalidate();
            repaint();
        } catch (Exception e) {
            System.err.println("Lỗi cập nhật dữ liệu YearlyStatsPanel: " + e.getMessage());
            clearData();
        }
    }

    /**
     * Lấy dữ liệu hiện tại (sao chép để tránh thay đổi ngoài).
     * @return Map chứa các thống kê, hoặc Map rỗng nếu chưa có dữ liệu.
     */
    public Map<String, Object> getData() {
        return new HashMap<>(currentStats); // Sao chép để bảo vệ dữ liệu nội bộ
    }

    /**
     * Cập nhật phần tổng kết.
     */
    private void updateSummary(double tongThu, double tongChi, double trungBinh, int thangMax) {
        // Tính toán trạng thái
        double chenh = tongThu - tongChi;
        String trangThai = chenh >= 0 ? "thặng dư" : "thâm hụt";

        if (chenh >= 0) {
            setIcon(lblIcon, "/resources/correct.png");
        } else {
            setIcon(lblIcon, "/resources/crisis.png");
        }

        // Tạo nội dung summary
        String summary = String.format(
            "Năm này bạn %s %s ₫. Chi tiêu trung bình mỗi tháng là %s ₫, " +
            "trong đó tháng %d là tháng chi tiêu nhiều nhất.",
            trangThai,
            formatter.format(Math.abs(chenh)),
            formatter.format(trungBinh),
            thangMax
        );

        txtSummary.setText(summary);
    }

    /**
     * Xóa dữ liệu và reset UI.
     */
    private void clearData() {
        currentStats.clear();
        lblTongThuNam.setText("0 ₫");
        lblTongChiNam.setText("0 ₫");
        lblTrungBinhChi.setText("0 ₫");
        lblThangChiLonNhat.setText("N/A");

        // Reset icon và text
        setIcon(lblIcon, "/resources/chart.png"); // Sửa path đúng
        txtSummary.setText("Chọn năm và nhấn 'Áp dụng' để xem thống kê chi tiết");
    }

    /**
     * Helper method để set icon cho label, với xử lý lỗi.
     * @param label JLabel cần set icon
     * @param iconPath Đường dẫn đến file icon (từ resources)
     */
    private void setIcon(JLabel label, String iconPath) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            // Scale icon về 20x20 để nhất quán
            Image scaledImage = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImage));
            label.setText(""); // Xóa text nếu có
        } catch (Exception e) {
            // Fallback: Sử dụng text emoji hoặc default
            System.err.println("Không thể load icon: " + iconPath + ". Sử dụng fallback.");
            if (iconPath.contains("correct")) {
                label.setText("✓");
            } else if (iconPath.contains("crisis")) {
                label.setText("⚠");
            } else {
                label.setText("📊");
            }
            label.setIcon(null); // Đảm bảo không có icon
        }
    }

    /**
     * Helper: Chuyển đổi an toàn sang double.
     */
    private double safeDouble(Object value) {
        return value instanceof Number ? ((Number) value).doubleValue() : 0.0;
    }

    /**
     * Helper: Chuyển đổi an toàn sang int.
     */
    private int safeInt(Object value) {
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }
}
