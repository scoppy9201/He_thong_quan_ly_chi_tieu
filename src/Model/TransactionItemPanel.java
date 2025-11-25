/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TransactionItemPanel extends JPanel {
    private Transaction transaction;
    
    public TransactionItemPanel(Transaction trans) {
        this.transaction = trans;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        
        // Panel bên trái - Icon danh mục
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setPreferredSize(new Dimension(60, 60));
        
        JLabel lblIcon = new JLabel();
        lblIcon.setPreferredSize(new Dimension(60, 60));
        lblIcon.setHorizontalAlignment(JLabel.CENTER);
        lblIcon.setVerticalAlignment(JLabel.CENTER);
        lblIcon.setOpaque(true);
        lblIcon.setBackground(new Color(248, 249, 250));
        lblIcon.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        
        // Load icon từ danh mục
        if (transaction.getTenDanhMuc() != null && !transaction.getTenDanhMuc().isEmpty()) {
            try {
                String iconPath = transaction.getBieuTuongDanhMuc();
                ImageIcon icon = new ImageIcon(iconPath);
                Image scaledImg = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                lblIcon.setIcon(new ImageIcon(scaledImg));
            } catch (Exception e) {
                // Hiển thị icon mặc định theo loại giao dịch
                if (transaction.getLoaiGiaoDich() == Transaction.LoaiGiaoDich.THU) {
                    lblIcon.setText("Ảnh");
                } else {
                    lblIcon.setText("Ảnh");
                }
                lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            }
        } else {
            if (transaction.getLoaiGiaoDich() == Transaction.LoaiGiaoDich.THU) {
                lblIcon.setText("Ảnh");
            } else {
                lblIcon.setText("Ảnh");
            }
            lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }
        
        leftPanel.add(lblIcon, BorderLayout.CENTER);
        
        // Panel giữa - Thông tin giao dịch
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        
        // Tên danh mục
        String tenDM = transaction.getTenDanhMuc();
        if (tenDM == null || tenDM.trim().isEmpty()) {
            tenDM = "Chưa phân loại";
        }
        JLabel lblTenDanhMuc = new JLabel(tenDM);
        lblTenDanhMuc.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTenDanhMuc.setForeground(new Color(33, 37, 41));
        
        // Ghi chú
        String ghiChu = transaction.getGhiChu();
        if (ghiChu == null || ghiChu.trim().isEmpty()) {
            ghiChu = "Không có ghi chú";
        }
        // Giới hạn độ dài ghi chú
        if (ghiChu.length() > 50) {
            ghiChu = ghiChu.substring(0, 47) + "...";
        }
        JLabel lblGhiChu = new JLabel(ghiChu);
        lblGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblGhiChu.setForeground(new Color(108, 117, 125));
        
        // Ngày giao dịch và phương thức
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String ngay = transaction.getNgayGiaoDich().format(formatter);
        String phuongThuc = transaction.getPhuongThuc() != null ? transaction.getPhuongThuc() : "Tiền mặt";
        JLabel lblNgay = new JLabel(ngay + " • " + phuongThuc);
        lblNgay.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblNgay.setForeground(new Color(108, 117, 125));
        
        centerPanel.add(lblTenDanhMuc);
        centerPanel.add(lblGhiChu);
        centerPanel.add(lblNgay);
        
        // Panel bên phải - Số tiền và loại giao dịch
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(180, 60));
        
        // Format số tiền
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String soTienStr = currencyFormat.format(transaction.getSoTien());
        
        JLabel lblSoTien = new JLabel(soTienStr);
        lblSoTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSoTien.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
        
        // Màu sắc theo loại giao dịch
        if (transaction.getLoaiGiaoDich() == Transaction.LoaiGiaoDich.THU) {
            lblSoTien.setForeground(new Color(40, 167, 69)); // Xanh lá
        } else {
            lblSoTien.setForeground(new Color(220, 53, 69)); // Đỏ
        }
        
        // Label loại giao dịch
        String loaiText = transaction.getLoaiGiaoDich() == Transaction.LoaiGiaoDich.THU ? "Khoản thu" : "Khoản chi";
        JLabel lblLoai = new JLabel(loaiText);
        lblLoai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLoai.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
        lblLoai.setForeground(new Color(108, 117, 125));
        
        rightPanel.add(lblSoTien);
        rightPanel.add(lblLoai);
        
        // Add các panel vào main panel
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
    
    public Transaction getTransaction() {
        return transaction;
    }
}
