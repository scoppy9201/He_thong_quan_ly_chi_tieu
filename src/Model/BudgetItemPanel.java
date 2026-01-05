/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author Admin
 */
public class BudgetItemPanel extends JPanel {
    private final Budget budget;
    
    public BudgetItemPanel(Budget budget) {
        this.budget = budget;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(0, 120));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Left: Icon và tên
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.setBackground(Color.WHITE);
        
        // Icon
        JLabel lblIcon = new JLabel();
        if (budget.getBieuTuongDanhMuc() != null && !budget.getBieuTuongDanhMuc().isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(budget.getBieuTuongDanhMuc());
                lblIcon.setIcon(new ImageIcon(icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
            } catch (Exception e) {
                lblIcon.setText("");
                lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 32));
            }
        } else {
            lblIcon.setText("");
            lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        }
        leftPanel.add(lblIcon);
        leftPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        
        // Tên danh mục
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setBackground(Color.WHITE);
        
        JLabel lblName = new JLabel(budget.getTenDanhMuc());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(new Color(52, 73, 94));
        namePanel.add(lblName);
        
        if (budget.getTenDanhMucCha() != null) {
            JLabel lblParent = new JLabel(budget.getTenDanhMucCha());
            lblParent.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblParent.setForeground(Color.GRAY);
            namePanel.add(lblParent);
        }
        
        leftPanel.add(namePanel);
        add(leftPanel, BorderLayout.WEST);
        
        // Center: Progress bar
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        
        // Số tiền
        JPanel moneyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        moneyPanel.setBackground(Color.WHITE);
        
        JLabel lblSpent = new JLabel(formatMoney(budget.getDaDung()));
        lblSpent.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSpent.setForeground(getColorByPercent(budget.getPhanTramDaDung()));
        moneyPanel.add(lblSpent);
        
        JLabel lblOf = new JLabel(" / ");
        lblOf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblOf.setForeground(Color.GRAY);
        moneyPanel.add(lblOf);
        
        JLabel lblTotal = new JLabel(formatMoney(budget.getTongNganSach()));
        lblTotal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTotal.setForeground(Color.GRAY);
        moneyPanel.add(lblTotal);
        
        centerPanel.add(moneyPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) budget.getPhanTramDaDung());
        progressBar.setStringPainted(true);
        progressBar.setString(String.format("%.1f%%", budget.getPhanTramDaDung()));
        progressBar.setForeground(getColorByPercent(budget.getPhanTramDaDung()));
        progressBar.setPreferredSize(new Dimension(0, 25));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        centerPanel.add(progressBar);
        
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Còn lại
        JLabel lblRemaining = new JLabel("Còn lại: " + formatMoney(budget.getConLai()));
        lblRemaining.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRemaining.setForeground(budget.isVuotNganSach() ? Color.RED : new Color(40, 167, 69));
        centerPanel.add(lblRemaining);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Right: Status
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setAlignmentY(TOP_ALIGNMENT);
        
        String statusText = budget.isVuotNganSach() ? "⚠️ VƯỢT" : "✓";
        JLabel lblStatus = new JLabel(statusText);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblStatus.setForeground(budget.isVuotNganSach() ? Color.RED : new Color(40, 167, 69));
        rightPanel.add(lblStatus);
        
        add(rightPanel, BorderLayout.EAST);
        
        // Hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(new Color(248, 249, 250));
                leftPanel.setBackground(new Color(248, 249, 250));
                namePanel.setBackground(new Color(248, 249, 250));
                centerPanel.setBackground(new Color(248, 249, 250));
                moneyPanel.setBackground(new Color(248, 249, 250));
                rightPanel.setBackground(new Color(248, 249, 250));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(Color.WHITE);
                leftPanel.setBackground(Color.WHITE);
                namePanel.setBackground(Color.WHITE);
                centerPanel.setBackground(Color.WHITE);
                moneyPanel.setBackground(Color.WHITE);
                rightPanel.setBackground(Color.WHITE);
            }
        });
    }
    
    public Budget getBudget() {
        return budget;
    }
    
    private String formatMoney(java.math.BigDecimal amount) {
        if (amount == null) return "0 đ";
        return String.format("%,d đ", amount.longValue());
    }
    
    private Color getColorByPercent(double percent) {
        if (percent >= 100) return new Color(220, 53, 69); // Đỏ
        if (percent >= 90) return new Color(255, 87, 34); // Cam đậm
        if (percent >= 70) return new Color(255, 193, 7); // Vàng
        if (percent >= 50) return new Color(0, 123, 255); // Xanh dương
        return new Color(40, 167, 69); // Xanh lá
    }
}
