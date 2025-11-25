/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class HintUtils {

    /**
     * Thiết lập hint cho JTextPane
     * @param pane JTextPane cần set hint
     * @param hint Nội dung hint
     */
    public static void setHint(JTextField field, String hint) {
        field.setText(hint);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(hint)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(hint);
                }
            }
        });
    }
    
    public static void setHint(JPasswordField field, String hint) {
        field.setEchoChar((char)0); // hiển thị hint khi rỗng
        field.setText(hint);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(hint)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setEchoChar('•');
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setForeground(Color.GRAY);
                    field.setText(hint);
                    field.setEchoChar((char)0); // hiển thị hint
                }
            }
        });
    }
}

