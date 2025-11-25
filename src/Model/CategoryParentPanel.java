package Model;
import java.awt.*;
import javax.swing.*;

public class CategoryParentPanel extends JPanel {
    public CategoryParentPanel(Category category) {
        setLayout(new BorderLayout(10, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        setBackground(new Color(52, 73, 94)); // Xanh đậm đẹp
        setPreferredSize(new Dimension(0, 50));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Icon
        JLabel lblIcon = new JLabel();
        if (category.getBieuTuong() != null) {
            ImageIcon icon = new ImageIcon(category.getBieuTuong());
            lblIcon.setIcon(new ImageIcon(icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        }
        add(lblIcon, BorderLayout.WEST);
        
        // Tên
        JLabel lblName = new JLabel(category.getTenDanhMuc());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(Color.WHITE);
        add(lblName, BorderLayout.CENTER);
        
        // Hover
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(new Color(41, 128, 185)); // Xanh sáng khi hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(new Color(52, 73, 94));
            }
        });
    }
}