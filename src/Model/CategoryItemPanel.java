package Model;
import java.awt.*;
import javax.swing.*;

public class CategoryItemPanel extends JPanel {
    private final Category category;
    
    public CategoryItemPanel(Category category) {
        this.category = category;
        setPreferredSize(new Dimension(110, 110));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Icon
        JLabel lblIcon = new JLabel();
        lblIcon.setAlignmentX(CENTER_ALIGNMENT);
        if (category.getBieuTuong() != null) {
            ImageIcon icon = new ImageIcon(category.getBieuTuong());
            lblIcon.setIcon(new ImageIcon(icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        }
        add(Box.createVerticalGlue());
        add(lblIcon);
        add(Box.createRigidArea(new Dimension(0, 8)));
        
        // Tên
        JLabel lblName = new JLabel(category.getTenDanhMuc(), SwingConstants.CENTER);
        lblName.setAlignmentX(CENTER_ALIGNMENT);
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblName.setForeground(new Color(52, 73, 94));
        add(lblName);
        add(Box.createVerticalGlue());
    }
    
    public Category getCategory() {
        return category;
    }
}
