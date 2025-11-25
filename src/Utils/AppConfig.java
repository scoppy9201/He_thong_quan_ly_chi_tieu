package Utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {
    private static final String CONFIG_FILE = "app_config.properties";
    private static final Properties props = new Properties();

    static {
        load();
    }

    public static void load() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
        } catch (IOException e) {
            // Giá trị mặc định nếu file chưa tồn tại
            props.setProperty("theme", "Light");
            props.setProperty("fontFamily", "Segoe UI");
            props.setProperty("fontSize", "14");
        }
    }

    public static void save() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Cấu hình giao diện người dùng");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Theme
    public static String getTheme() {
        return props.getProperty("theme", "Light");
    }

    public static void setTheme(String value) {
        props.setProperty("theme", value);
    }

    // Font
    public static String getFontFamily() {
        return props.getProperty("fontFamily", "Segoe UI");
    }

    public static void setFontFamily(String value) {
        props.setProperty("fontFamily", value);
    }

    // Font size
    public static int getFontSize() {
        try {
            return Integer.parseInt(props.getProperty("fontSize", "14"));
        } catch (NumberFormatException e) {
            return 14;
        }
    }

    public static void setFontSize(int size) {
        props.setProperty("fontSize", String.valueOf(size));
    }
}

