/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import java.io.*;
import java.util.Properties;
import java.util.Base64;

public class ConfigUtils {
    private static final String FILE_NAME = "config.properties";

    public static void saveLogin(String email, String password, boolean remember) {
        try (FileOutputStream fos = new FileOutputStream(FILE_NAME)) {
            Properties prop = new Properties();
            prop.setProperty("remember", String.valueOf(remember));
            if (remember) {
                prop.setProperty("email", email);
                // Mã hóa đơn giản
                prop.setProperty("password", Base64.getEncoder().encodeToString(password.getBytes()));
            } else {
                prop.setProperty("email", "");
                prop.setProperty("password", "");
            }
            prop.store(fos, "User login config");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSavedEmail() {
        return getProp("email");
    }

    public static String getSavedPassword() {
        String encoded = getProp("password");
        if (encoded.isEmpty()) return "";
        return new String(Base64.getDecoder().decode(encoded));
    }

    public static boolean isRemember() {
        return Boolean.parseBoolean(getProp("remember"));
    }

    private static String getProp(String key) {
        try (FileInputStream fis = new FileInputStream(FILE_NAME)) {
            Properties prop = new Properties();
            prop.load(fis);
            return prop.getProperty(key, "");
        } catch (IOException e) {
            return "";
        }
    }
}


