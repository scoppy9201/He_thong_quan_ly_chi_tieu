/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI;

import Model.User;
import Service.UserService;
import java.awt.Color;
import java.awt.Image;
import java.net.URL;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 *
 * @author Admin
 */
public class cardThongTin extends javax.swing.JPanel {

    private User currentUser;
    private UserService userService;
    private boolean editMode = false;
    /**
     * Creates new form cardThongTin
     */
    public cardThongTin(UserService service, int userId) {
        initComponents();
        this.userService = service;
        SwingUtilities.invokeLater(() -> setupLabelIcons());
        loadUser(userId);
        setEditable(false); // khóa tất cả giao diện khi load 
        javax.swing.ButtonGroup genderGroup = new javax.swing.ButtonGroup();
        genderGroup.add(jrbNam);
        genderGroup.add(jrbNu);
        genderGroup.add(jrbKhac);
        
        btnCapNhat.addActionListener(e -> {
            if (!editMode) {
                // Bật chế độ edit
                editMode = true;
                setEditable(true);
                btnCapNhat.setText("Xác nhận");
                btnCapNhat.setBackground(Color.WHITE); 
                btnCapNhat.setForeground(Color.BLACK); 
            } else {
                updateUser();
                editMode = false;
                setEditable(false);
                btnCapNhat.setText("Cập nhật");
                btnCapNhat.setBackground(new Color(0,123,255));
                btnCapNhat.setForeground(Color.WHITE); 
            }
        });

        btnExit.addActionListener(e -> cancelEdit());
    }
    
    // Load dữ liệu user lên form
    private void loadUser(int userId) {
        currentUser = userService.getUserById(userId);
        if (currentUser == null) {
            System.out.println("User không tồn tại: " + userId);
            return; // dừng luôn vì không có dữ liệu
        } else {
            System.out.println("User load thành công: " + currentUser.getHoTen());
        }

        // Điền thông tin vào form
        txtHoTen.setText(currentUser.getHoTen() != null ? currentUser.getHoTen() : "");
        txtEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        jSpinner1.setValue(currentUser.getNgaySinh() != null ? currentUser.getNgaySinh() : new Date());

        String gender = currentUser.getGioiTinh();
        if (gender == null) gender = "Khác";

        switch (gender) {
            case "Nam" -> jrbNam.setSelected(true);
            case "Nữ" -> jrbNu.setSelected(true);
            default -> jrbKhac.setSelected(true);
        }
    }

    // Mở / khóa các trường
    private void setEditable(boolean editable) {
        txtHoTen.setEditable(editable);
        txtEmail.setEditable(editable);
        jSpinner1.setEnabled(editable);
        jrbNam.setEnabled(editable);
        jrbNu.setEnabled(editable);
        jrbKhac.setEnabled(editable);
    }
    
    // Lấy giới tính được chọn
    private String getSelectedGender() {
        if (jrbNam.isSelected()) return "Nam";
        if (jrbNu.isSelected()) return "Nữ";
        return "Khác";
    }
    
    // Hủy edit và phục hồi dữ liệu gốc
    private void cancelEdit() {
        if (editMode) {
            if (currentUser != null) {
                // Nạp lại dữ liệu gốc từ DB
                loadUser(currentUser.getId());
            } else {
                // Nếu chẳng may currentUser null (chưa có user)
                clearFields();
            }

            editMode = false;
            setEditable(false);
            btnCapNhat.setText("Cập nhật");
        }
    }
    
    // Hàm xóa trắng form nếu không có user
    private void clearFields() {
        txtHoTen.setText("");
        txtEmail.setText("");
        jSpinner1.setValue(new Date());
        jrbNam.setSelected(false);
        jrbNu.setSelected(false);
        jrbKhac.setSelected(false);
    }
    
    // Cập nhật dữ liệu user
    private void updateUser() {
        boolean changed = !txtHoTen.getText().equals(currentUser.getHoTen()) ||
                !txtEmail.getText().equals(currentUser.getEmail()) ||
                !jSpinner1.getValue().equals(currentUser.getNgaySinh()) ||
                !getSelectedGender().equals(currentUser.getGioiTinh());

        if (!changed) {
            JOptionPane.showMessageDialog(this, "Không có thay đổi để cập nhật!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn cập nhật?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        User updatedUser = new User();
        updatedUser.setId(currentUser.getId()); 
        updatedUser.setHoTen(txtHoTen.getText());
        updatedUser.setEmail(txtEmail.getText());
        updatedUser.setNgaySinh((Date) jSpinner1.getValue());
        updatedUser.setGioiTinh(getSelectedGender());

        // Gọi service
        UserService.UpdateResponse response = userService.updateUserInfo(updatedUser);
        if (response.result == UserService.UpdateResult.SUCCESS) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            currentUser = updatedUser;
            editMode = false;
            setEditable(false);
            btnCapNhat.setText("Cập nhật");
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại: " + response.message);
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnExit = new javax.swing.JButton();
        btnCapNhat = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        txtEmail = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jrbNam = new javax.swing.JRadioButton();
        jrbNu = new javax.swing.JRadioButton();
        jrbKhac = new javax.swing.JRadioButton();
        lblGioiTinh = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtHoTen = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        lblHoTen = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        lblNgaySinh = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(100, 100));

        btnExit.setBackground(new java.awt.Color(255, 0, 51));
        btnExit.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnExit.setForeground(new java.awt.Color(255, 255, 255));
        btnExit.setText("Hủy bỏ");
        btnExit.setPreferredSize(new java.awt.Dimension(88, 33));

        btnCapNhat.setBackground(new java.awt.Color(0, 123, 255));
        btnCapNhat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCapNhat.setForeground(new java.awt.Color(255, 255, 255));
        btnCapNhat.setText("Cập nhật ");
        btnCapNhat.setPreferredSize(new java.awt.Dimension(88, 33));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(310, 45));

        txtEmail.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtEmail.setBorder(null);

        jLabel1.setBackground(new java.awt.Color(245, 245, 245));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Email");

        lblEmail.setBackground(new java.awt.Color(245, 245, 245));
        lblEmail.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(42, 42, 42)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(lblEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(310, 45));

        jLabel2.setBackground(new java.awt.Color(245, 245, 245));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Giới tính ");

        jrbNam.setBackground(new java.awt.Color(255, 255, 255));
        jrbNam.setText("Nam");
        jrbNam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbNamActionPerformed(evt);
            }
        });

        jrbNu.setBackground(new java.awt.Color(255, 255, 255));
        jrbNu.setText("Nữ");

        jrbKhac.setBackground(new java.awt.Color(255, 255, 255));
        jrbKhac.setText("Khác");

        lblGioiTinh.setBackground(new java.awt.Color(245, 245, 245));
        lblGioiTinh.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblGioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(26, 26, 26)
                .addComponent(jrbNam)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 83, Short.MAX_VALUE)
                .addComponent(jrbNu)
                .addGap(79, 79, 79)
                .addComponent(jrbKhac)
                .addGap(23, 23, 23))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblGioiTinh, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jrbNam)
                        .addComponent(jrbNu)
                        .addComponent(jrbKhac)))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(310, 45));

        txtHoTen.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtHoTen.setBorder(null);

        jLabel3.setBackground(new java.awt.Color(245, 245, 245));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Họ tên ");

        lblHoTen.setBackground(new java.awt.Color(245, 245, 245));
        lblHoTen.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblHoTen.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lblHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(lblHoTen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setPreferredSize(new java.awt.Dimension(310, 45));

        jLabel4.setBackground(new java.awt.Color(245, 245, 245));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Ngày sinh");

        jSpinner1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jSpinner1.setModel(new javax.swing.SpinnerDateModel());
        jSpinner1.setEditor(new javax.swing.JSpinner.DateEditor(jSpinner1, "dd/MM/yyyy"));

        lblNgaySinh.setBackground(new java.awt.Color(245, 245, 245));
        lblNgaySinh.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNgaySinh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Thông tin tài khoản ");

        lblUser.setBackground(new java.awt.Color(245, 245, 245));
        lblUser.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE))
                            .addComponent(jLabel5))
                        .addGap(0, 194, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(324, 324, 324)
                    .addComponent(lblUser, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(324, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 97, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(192, 192, 192)
                    .addComponent(lblUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(193, 193, 193)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jrbNamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbNamActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jrbNamActionPerformed

    private void setupLabelIcons() {
        resizeLabelIcon(lblHoTen, "/resources/fullname.png");
        resizeLabelIcon(lblEmail, "/resources/email.png");
        resizeLabelIcon(lblGioiTinh, "/resources/gender.png");
        resizeLabelIcon(lblNgaySinh, "/resources/calendar.png");
    }

    private void resizeLabelIcon(JLabel label, String path) {
        try {
            URL iconURL = getClass().getResource(path);
            if (iconURL == null) {
                iconURL = getClass().getResource("/" + path);
                if (iconURL == null) iconURL = getClass().getClassLoader().getResource(path);
            }

            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                // Lấy kích thước hiện tại của JLabel
                int width = label.getWidth();
                int height = label.getHeight();

                // Nếu label chưa có kích thước (ví dụ khi mới khởi tạo), gán kích thước mặc định
                if (width <= 0) width = 128;
                if (height <= 0) height = 128;

                // Co giãn ảnh theo kích thước của label
                Image scaledImg = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaledImg));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
            } else {
                System.err.println("Không tìm thấy icon cho label: " + path);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi load icon cho label: " + path);
            e.printStackTrace();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCapNhat;
    private javax.swing.JButton btnExit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JRadioButton jrbKhac;
    private javax.swing.JRadioButton jrbNam;
    private javax.swing.JRadioButton jrbNu;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblGioiTinh;
    private javax.swing.JLabel lblHoTen;
    private javax.swing.JLabel lblNgaySinh;
    private javax.swing.JLabel lblUser;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtHoTen;
    // End of variables declaration//GEN-END:variables
}
