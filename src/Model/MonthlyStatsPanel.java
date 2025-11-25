package Model;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Panel hiển thị thống kê chi tiêu theo tháng.
 * Tối ưu hóa: Thêm getData() để lấy dữ liệu, cải thiện thread-safe, xử lý lỗi, và làm UI mượt hơn.
 */
public class MonthlyStatsPanel extends JPanel {

    private JLabel lblTongThu;
    private JLabel lblTongChi;
    private JLabel lblSoGiaoDich;
    private JLabel lblNhomChiLonNhat;
    private JPanel panelTyLe;
    private DecimalFormat formatter;
    private Map<String, Object> currentStats; // Lưu trữ dữ liệu hiện tại để getData()

    public MonthlyStatsPanel() {
        formatter = new DecimalFormat("#,##0");
        currentStats = new HashMap<>(); // Khởi tạo rỗng
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel lblTitle = new JLabel("Thống kê chi tiêu tháng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(lblTitle, BorderLayout.NORTH);

        // Panel chính chứa các thống kê
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        // Panel thống kê tổng quan (4 chỉ số chính)
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 10));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Tạo các stat card
        JPanel cardTongThu = createStatCard("Tổng thu", "0 ₫", new Color(40, 167, 69));
        JPanel cardTongChi = createStatCard("Tổng chi", "0 ₫", new Color(220, 53, 69));
        JPanel cardSoGiaoDich = createStatCard("Số giao dịch", "0", new Color(0, 123, 255));
        JPanel cardNhomChi = createStatCard("Nhóm chi lớn nhất", "N/A", new Color(108, 117, 125));

        lblTongThu = (JLabel) cardTongThu.getComponent(2);
        lblTongChi = (JLabel) cardTongChi.getComponent(2);
        lblSoGiaoDich = (JLabel) cardSoGiaoDich.getComponent(2);
        lblNhomChiLonNhat = (JLabel) cardNhomChi.getComponent(2);

        statsPanel.add(cardTongThu);
        statsPanel.add(cardTongChi);
        statsPanel.add(cardSoGiaoDich);
        statsPanel.add(cardNhomChi);

        mainPanel.add(statsPanel);

        // Panel tỷ lệ % từng loại
        JPanel tyLeContainer = new JPanel(new BorderLayout());
        tyLeContainer.setBackground(Color.WHITE);
        tyLeContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Tỷ lệ chi tiêu theo danh mục"),
            BorderFactory.createEmptyBorder(5, 10, 10, 10)
        ));

        panelTyLe = new JPanel();
        panelTyLe.setLayout(new BoxLayout(panelTyLe, BoxLayout.Y_AXIS));
        panelTyLe.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(panelTyLe);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(0, 150));

        tyLeContainer.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tyLeContainer);

        add(mainPanel, BorderLayout.CENTER);
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
     * Cập nhật dữ liệu thống kê tháng từ backend.
     * @param stats Map chứa: TongThu, TongChi, SoGiaoDich, NhomChiLonNhat, TyLeTungLoai
     * Đảm bảo gọi từ EDT hoặc dùng SwingUtilities.invokeLater().
     */
    @SuppressWarnings("unchecked")
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
            double tongThu = safeDouble(stats.get("TongThu"));
            double tongChi = safeDouble(stats.get("TongChi"));
            int soGiaoDich = safeInt(stats.get("SoGiaoDich"));
            String nhomChiLonNhat = (String) stats.getOrDefault("NhomChiLonNhat", "N/A");

            lblTongThu.setText(formatter.format(tongThu) + " ₫");
            lblTongChi.setText(formatter.format(tongChi) + " ₫");
            lblSoGiaoDich.setText(String.valueOf(soGiaoDich));
            lblNhomChiLonNhat.setText(nhomChiLonNhat);

            // Cập nhật tỷ lệ % từng loại
            Map<String, Double> tyLeTungLoai = (Map<String, Double>) stats.get("TyLeTungLoai");
            updatePercentages(tyLeTungLoai);

            revalidate();
            repaint();
        } catch (Exception e) {
            System.err.println("Lỗi cập nhật dữ liệu MonthlyStatsPanel: " + e.getMessage());
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
     * Cập nhật panel tỷ lệ %.
     */
    private void updatePercentages(Map<String, Double> percentages) {
        panelTyLe.removeAll();

        if (percentages == null || percentages.isEmpty()) {
            JLabel lblNoData = new JLabel("Chưa có dữ liệu");
            lblNoData.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblNoData.setForeground(Color.GRAY);
            panelTyLe.add(lblNoData);
        } else {
            // Sắp xếp theo % giảm dần và tạo rows
            percentages.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(entry -> createPercentageRow(entry.getKey(), entry.getValue()))
                .forEach(row -> {
                    panelTyLe.add(row);
                    panelTyLe.add(Box.createRigidArea(new Dimension(0, 5)));
                });
        }

        panelTyLe.revalidate();
        panelTyLe.repaint();
    }

    /**
     * Tạo một dòng hiển thị % danh mục.
     */
    private JPanel createPercentageRow(String category, double percentage) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel lblCategory = new JLabel(category);
        lblCategory.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblPercent = new JLabel(String.format("%.2f%%", percentage));
        lblPercent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPercent.setForeground(new Color(0, 123, 255));

        row.add(lblCategory, BorderLayout.WEST);
        row.add(lblPercent, BorderLayout.EAST);

        return row;
    }

    /**
     * Xóa dữ liệu và reset UI.
     */
    private void clearData() {
        currentStats.clear();
        lblTongThu.setText("0 ₫");
        lblTongChi.setText("0 ₫");
        lblSoGiaoDich.setText("0");
        lblNhomChiLonNhat.setText("N/A");
        panelTyLe.removeAll();
        panelTyLe.revalidate();
        panelTyLe.repaint();
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
