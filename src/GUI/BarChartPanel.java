/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class BarChartPanel extends JPanel {
    
    private ChartPanel chartPanel;
    private JFreeChart chart;
    private DefaultCategoryDataset dataset;
    private JLabel lblTitle;
    
    // Lưu trữ dữ liệu để có thể get lại
    private Map<Integer, Double> currentData;
    
    public BarChartPanel() {
        this.currentData = new HashMap<>();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title
        lblTitle = new JLabel("Xu hướng chi tiêu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        add(lblTitle, BorderLayout.NORTH);
        
        // Tạo dataset trống
        dataset = new DefaultCategoryDataset();
        
        // Tạo chart
        chart = ChartFactory.createBarChart(
            "",  // Title
            "Tháng",  // X-axis label
            "Số tiền (VNĐ)",  // Y-axis label
            dataset,
            PlotOrientation.VERTICAL,
            false,  // legend
            true,  // tooltips
            false  // urls
        );
        
        // Tùy chỉnh biểu đồ 
        chart.setBackgroundPaint(Color.WHITE);
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        
        // Customize renderer
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(54, 162, 235));
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.1);
        
        // Customize axes
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        rangeAxis.setNumberFormatOverride(new DecimalFormat("#,##0"));
        
        // Chart Panel
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 350));
        chartPanel.setBackground(Color.WHITE);
        add(chartPanel, BorderLayout.CENTER);
    }
    
    /**
     * Cập nhật dữ liệu biểu đồ từ BE
     * @param data Map<Integer, Double> - key: tháng (1-12), value: số tiền
     */
    public void updateData(Map<Integer, Double> data) {
        dataset.clear();
        
        if (data != null) {
            this.currentData = new HashMap<>(data);
        } else {
            this.currentData = new HashMap<>();
        }
        
        if (data == null || data.isEmpty()) {
            // Hiển thị thông báo không có dữ liệu
            JLabel lblNoData = new JLabel("Không có dữ liệu chi tiêu");
            lblNoData.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lblNoData.setForeground(Color.GRAY);
            lblNoData.setHorizontalAlignment(SwingConstants.CENTER);
            removeAll();
            add(lblTitle, BorderLayout.NORTH);
            add(lblNoData, BorderLayout.CENTER);
            revalidate();
            repaint();
            return;
        }
        
        // Add data vào dataset - đảm bảo có đủ 12 tháng
        for (int month = 1; month <= 12; month++) {
            Double value = data.getOrDefault(month, 0.0);
            dataset.addValue(value, "Chi tiêu", "T" + month);
        }
        
        // Refresh chart
        if (getComponentCount() > 1 && !(getComponent(1) instanceof ChartPanel)) {
            removeAll();
            add(lblTitle, BorderLayout.NORTH);
            add(chartPanel, BorderLayout.CENTER);
        }
        
        revalidate();
        repaint();
    }
    
    /**
     * Lấy dữ liệu hiện tại của biểu đồ
     * @return Map<Integer, Double> - key: tháng (1-12), value: số tiền
     */
    public Map<Integer, Double> getData() {
        // Trả về copy để tránh modification từ bên ngoài
        return new HashMap<>(currentData);
    }
    
    /**
     * Kiểm tra có dữ liệu không
     * @return true nếu có dữ liệu
     */
    public boolean hasData() {
        return currentData != null && !currentData.isEmpty();
    }
    
    /**
     * Lấy tổng chi tiêu trong năm
     * @return Tổng số tiền đã chi
     */
    public double getTotalExpense() {
        if (currentData == null || currentData.isEmpty()) {
            return 0.0;
        }
        return currentData.values().stream()
            .mapToDouble(Double::doubleValue)
            .sum();
    }
    
    /**
     * Lấy tháng chi tiêu cao nhất
     * @return Số tháng (1-12) hoặc 0 nếu không có dữ liệu
     */
    public int getHighestExpenseMonth() {
        if (currentData == null || currentData.isEmpty()) {
            return 0;
        }
        return currentData.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(0);
    }
    
    /**
     * Lấy tháng chi tiêu thấp nhất (không tính tháng = 0)
     * @return Số tháng (1-12) hoặc 0 nếu không có dữ liệu
     */
    public int getLowestExpenseMonth() {
        if (currentData == null || currentData.isEmpty()) {
            return 0;
        }
        return currentData.entrySet().stream()
            .filter(e -> e.getValue() > 0) // Bỏ qua tháng = 0
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(0);
    }
    
    /**
     * Lấy chi tiêu trung bình mỗi tháng
     * @return Số tiền trung bình
     */
    public double getAverageExpensePerMonth() {
        if (currentData == null || currentData.isEmpty()) {
            return 0.0;
        }
        
        // Tính trung bình chỉ với các tháng có dữ liệu > 0
        long countNonZero = currentData.values().stream()
            .filter(v -> v > 0)
            .count();
        
        if (countNonZero == 0) {
            return 0.0;
        }
        
        double total = getTotalExpense();
        return total / countNonZero;
    }
    
    /**
     * Update title
     */
    public void setTitle(String title) {
        lblTitle.setText(title);
    }
    
    /**
     * Clear all data
     */
    public void clearData() {
        currentData.clear();
        dataset.clear();
        revalidate();
        repaint();
    }
}