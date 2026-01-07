/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MoneyDisplayPanel extends JPanel {
    private JLabel lblTitle;
    private JLabel lblAmount;
    private String loaiGiaoDich; // "Thu" hoặc "Chi"
    
    public MoneyDisplayPanel() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Label tiêu đề "Số tiền"
        lblTitle = new JLabel("Số tiền");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(108, 117, 125));
        lblTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        // Label hiển thị số tiền
        lblAmount = new JLabel("0 đ");
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblAmount.setForeground(new Color(108, 117, 125)); // Màu mặc định
        lblAmount.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        lblAmount.setHorizontalAlignment(SwingConstants.CENTER);
        
        add(lblTitle);
        add(lblAmount);
    }
    
    // Cập nhật số tiền và màu sắc
    public void updateAmount(BigDecimal amount, String loaiGiaoDich) {
        this.loaiGiaoDich = loaiGiaoDich;
        
        if (amount == null) {
            lblAmount.setText("0 đ");
            lblAmount.setForeground(new Color(108, 117, 125));
            return;
        }
        
        // Format số tiền
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedAmount = formatter.format(amount) + " đ";
        lblAmount.setText(formattedAmount);
        
        // Đổi màu theo loại giao dịch
        if ("Chi".equalsIgnoreCase(loaiGiaoDich)) {
            lblAmount.setForeground(new Color(220, 53, 69)); // Đỏ
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 53, 69), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
        } else if ("Thu".equalsIgnoreCase(loaiGiaoDich)) {
            lblAmount.setForeground(new Color(40, 167, 69)); // Xanh lá
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 167, 69), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
        } else {
            lblAmount.setForeground(new Color(108, 117, 125)); // Xám
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
        }
    }
    
    public void reset() {
        updateAmount(BigDecimal.ZERO, null);
    }
}
