package Utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class AppConfigManager {

    public static void applyConfig(JFrame frame) {
        // Lấy config từ AppConfig
        String theme = AppConfig.getTheme();
        String fontName = AppConfig.getFontFamily();
        int fontSize = AppConfig.getFontSize();

        // Áp dụng font toàn cục
        setGlobalFont(new Font(fontName, Font.PLAIN, fontSize));

        // Áp dụng theme sáng/tối
        if ("Dark".equalsIgnoreCase(theme)) {
            setBackgroundRecursively(frame.getContentPane(), new Color(45, 45, 45), Color.WHITE);
        } else {
            setBackgroundRecursively(frame.getContentPane(), Color.WHITE, Color.BLACK);
        }

        // Cập nhật lại toàn bộ giao diện
        SwingUtilities.updateComponentTreeUI(frame);
    }

    private static void setGlobalFont(Font f) {
        UIManager.getDefaults().keys().asIterator().forEachRemaining(key -> {
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                UIManager.put(key, f);
            }
        });
    }

    private static void setBackgroundRecursively(Component comp, Color bg, Color fg) {
        comp.setBackground(bg);
        comp.setForeground(fg);

        if (comp instanceof javax.swing.JComponent) {
            for (Component child : ((javax.swing.JComponent) comp).getComponents()) {
                setBackgroundRecursively(child, bg, fg);
            }
        }
    }
}

