/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Map;

public class PieChartPanel extends JPanel {
    
    private ChartPanel chartPanel;
    private JFreeChart chart;
    private DefaultPieDataset dataset;
    private JLabel lblTitle;
    
    public PieChartPanel() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title
        lblTitle = new JLabel("Cơ cấu chi tiêu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        add(lblTitle, BorderLayout.NORTH);
        
        // Tạo dataset trống
        dataset = new DefaultPieDataset();
        
        // Tạo chart
        chart = ChartFactory.createPieChart(
            "",  // Title (đã có label riêng)
            dataset,
            true,  // legend
            true,  // tooltips
            false  // urls
        );
        
        // Customize chart
        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        plot.setLabelGap(0.02);
        
        // Format label để hiển thị %
        plot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator(
            "{0}: {2}", 
            new DecimalFormat("#,##0"), 
            new DecimalFormat("0.00%")
        ));
        
        // Sử dụng color palette động thay vì fix cứng
        applyColorPalette(plot);
        
        // Chart Panel
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 350));
        chartPanel.setBackground(Color.WHITE);
        add(chartPanel, BorderLayout.CENTER);
    }
    
    /**
     * Cập nhật dữ liệu biểu đồ từ BE
     * @param data Map<String, Double> - key: tên danh mục, value: số tiền
     */
    public void updateData(Map<String, Double> data) {
        dataset.clear();
        
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
        
        // Add data vào dataset
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        
        // Áp dụng màu sắc động sau khi có dữ liệu
        PiePlot plot = (PiePlot) chart.getPlot();
        applyColorPalette(plot);
        
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
     * Update title
     */
    public void setTitle(String title) {
        lblTitle.setText(title);
    }
    
    /**
     * Áp dụng bảng màu tự động cho tất cả danh mục
     * Sử dụng thuật toán HSB để tạo màu đẹp và phân biệt
     */
    private void applyColorPalette(PiePlot plot) {
        // Bảng màu đẹp mắt - Material Design inspired
        Color[] colorPalette = {
            new Color(255, 99, 132),   // Hồng đỏ
            new Color(54, 162, 235),   // Xanh dương
            new Color(255, 206, 86),   // Vàng
            new Color(75, 192, 192),   // Xanh ngọc
            new Color(153, 102, 255),  // Tím
            new Color(255, 159, 64),   // Cam
            new Color(201, 203, 207),  // Xám
            new Color(231, 76, 60),    // Đỏ
            new Color(46, 204, 113),   // Xanh lá
            new Color(52, 152, 219),   // Xanh biển
            new Color(155, 89, 182),   // Tím đậm
            new Color(241, 196, 15),   // Vàng đậm
            new Color(230, 126, 34),   // Cam đậm
            new Color(149, 165, 166),  // Xám đậm
            new Color(236, 112, 99),   // Hồng nhạt
            new Color(133, 193, 233)   // Xanh nhạt
        };
        
        // Nếu có nhiều hơn 16 danh mục, tự động sinh màu bằng HSB
        int index = 0;
        for (Object keyObj : dataset.getKeys()) {
            Comparable key = (Comparable) keyObj;
            Color color;
            if (index < colorPalette.length) {
                // Dùng màu từ palette
                color = colorPalette[index];
            } else {
                // Sinh màu tự động theo thuật toán HSB
                // Hue: chia đều 360 độ, Saturation(độ đậm): 70%, Brightness(độ sáng): 85%
                float hue = (float) (index % 20) / 20f;
                color = Color.getHSBColor(hue, 0.7f, 0.85f);
            }
            plot.setSectionPaint(key, color);
            index++;
        }
    }
}
