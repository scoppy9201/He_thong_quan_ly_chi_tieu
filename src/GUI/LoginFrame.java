package GUI;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */


import Model.User;
import Model.UserSession;
import Service.UserService;
import Utils.ConfigUtils;
import Utils.HintUtils;
import java.awt.BorderLayout;
import java.awt.Image;
import java.net.ServerSocket;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;


/**
 *
 * @author Admin
 */
public class LoginFrame extends javax.swing.JFrame {

    private static ServerSocket LOCK_SOCKET;
    private boolean showing = false; // trạng thái mật khẩu 
    private JLabel backgroundLabel;
    private MainFrame mainframe = null;
   
    /**
     * Creates new form LoginFrame
     */
    public LoginFrame() {
        setUndecorated(true);  // Ẩn thanh tiêu đề
        setResizable(false);   
        initComponents();
        setLocationRelativeTo(null);
        setupLabelIcons(); 
        setupBackground();
        
        HintUtils.setHint(jtbEmail, "Nhập email...");
        HintUtils.setHint(jpfPass, "Nhập mật khẩu...");

        // Sựu kiện thoát 
        lblExit.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Bạn có chắc muốn thoát không?",
                    "Xác nhận thoát",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblExit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                lblExit.setOpaque(true);
                lblExit.setBackground(new java.awt.Color(255, 80, 80));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblExit.setOpaque(false);
                lblExit.setBackground(null);
            }
        });
        
        // Sự kiện chuyển màn hình đăng ký 
        lblDangKy.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Tạo form đăng ký mới
                RegisterFrame registerFrame = new RegisterFrame();
                registerFrame.setVisible(true);

                // Ẩn form đăng nhập hiện tại
                LoginFrame.this.setVisible(false);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblDangKy.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                lblDangKy.setForeground(new java.awt.Color(65, 105, 255).brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblDangKy.setForeground(new java.awt.Color(65, 105, 225));
            }
        });
        
        lblShowPass.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblShowPassMouseClicked(evt);
            }
        });
        
        // Khi mở form, kiểm tra nếu đã lưu thì tự điền email và tự login
        if (ConfigUtils.isRemember()) {
            jtbEmail.setText(ConfigUtils.getSavedEmail());
            jpfPass.setText(ConfigUtils.getSavedPassword());
            jCheckBox1.setSelected(true);

            // Tự động đăng nhập
            btnDangNhap.doClick();
        }
        
        // Thêm sự kiện lưu đăng nhập khi bấm nút login
        btnDangNhap.addActionListener(e -> {
            String email = jtbEmail.getText().trim();
            String password = new String(jpfPass.getPassword()).trim();

            UserService userservice = new UserService();
            UserService.LoginResponse response = userservice.login(email, password);
            if (response.result == UserService.LoginResult.SUCCESS) {
                
                User loggedUser = userservice.getUserByEmail(email); 
                if (loggedUser != null) {
                    // Lưu vào session
                    UserSession.setCurrentUser(loggedUser);
                }
                ConfigUtils.saveLogin(email, password, jCheckBox1.isSelected());

                if (mainframe != null) {
                    // Unlock MainFrame hiện tại
                    mainframe.setEnabled(true);
                    mainframe.toFront(); // đưa lên trước
                    this.dispose();
                } else {
                    // Mở MainFrame mới
                    new MainFrame().setVisible(true);
                    this.dispose();
                }

            } else {
                JOptionPane.showMessageDialog(this, response.message, "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    // Thêm constructor dùng cho mở khóa MainFrame
    public LoginFrame(MainFrame frameToUnlock) {
        this(); // gọi constructor mặc định
        this.mainframe = frameToUnlock;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jplLayout = new javax.swing.JPanel();
        lblAvatar = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        jplUser = new javax.swing.JPanel();
        lblUser = new javax.swing.JLabel();
        jtbEmail = new javax.swing.JTextField();
        jplPass = new javax.swing.JPanel();
        lblPass = new javax.swing.JLabel();
        jpfPass = new javax.swing.JPasswordField();
        lblShowPass = new javax.swing.JLabel();
        btnDangNhap = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        lblDangKy = new javax.swing.JLabel();
        lblExit = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jplLayout.setBackground(new java.awt.Color(255, 255, 255));

        lblAvatar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTitle.setText("Đăng nhập hệ thống");

        jplUser.setBackground(new java.awt.Color(245, 245, 245));
        jplUser.setPreferredSize(new java.awt.Dimension(315, 38));

        lblUser.setBackground(new java.awt.Color(245, 245, 245));
        lblUser.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jtbEmail.setBackground(new java.awt.Color(245, 245, 245));
        jtbEmail.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtbEmail.setBorder(null);

        javax.swing.GroupLayout jplUserLayout = new javax.swing.GroupLayout(jplUser);
        jplUser.setLayout(jplUserLayout);
        jplUserLayout.setHorizontalGroup(
            jplUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jplUserLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblUser, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtbEmail)
                .addContainerGap())
        );
        jplUserLayout.setVerticalGroup(
            jplUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jplUserLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jplUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtbEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE))
                .addContainerGap())
        );

        jplPass.setBackground(new java.awt.Color(245, 245, 245));
        jplPass.setPreferredSize(new java.awt.Dimension(315, 38));

        lblPass.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jpfPass.setBackground(new java.awt.Color(245, 245, 245));
        jpfPass.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jpfPass.setText("jPasswordField1");
        jpfPass.setBorder(null);
        jpfPass.setPreferredSize(new java.awt.Dimension(64, 26));

        lblShowPass.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblShowPass.setPreferredSize(new java.awt.Dimension(25, 25));

        javax.swing.GroupLayout jplPassLayout = new javax.swing.GroupLayout(jplPass);
        jplPass.setLayout(jplPassLayout);
        jplPassLayout.setHorizontalGroup(
            jplPassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jplPassLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblPass, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jpfPass, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblShowPass, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jplPassLayout.setVerticalGroup(
            jplPassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jplPassLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jplPassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jpfPass, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblPass, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblShowPass, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        btnDangNhap.setBackground(new java.awt.Color(76, 175, 80));
        btnDangNhap.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDangNhap.setForeground(new java.awt.Color(255, 255, 255));
        btnDangNhap.setText("Đăng nhập");

        jLabel3.setText("Bạn chưa có tài khoản?");

        jCheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jCheckBox1.setText("Lưu đăng nhập ");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Quên mật khẩu?");

        lblDangKy.setForeground(new java.awt.Color(65, 105, 225));
        lblDangKy.setText("Đăng ký");

        lblExit.setBackground(new java.awt.Color(255, 255, 255));
        lblExit.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout jplLayoutLayout = new javax.swing.GroupLayout(jplLayout);
        jplLayout.setLayout(jplLayoutLayout);
        jplLayoutLayout.setHorizontalGroup(
            jplLayoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jplLayoutLayout.createSequentialGroup()
                .addGap(83, 83, 83)
                .addComponent(lblAvatar, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(88, 88, 88)
                .addGroup(jplLayoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jplLayoutLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4))
                    .addComponent(jplPass, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                    .addComponent(jplUser, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                    .addComponent(btnDangNhap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(28, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jplLayoutLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jplLayoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jplLayoutLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblDangKy)
                        .addGap(79, 79, 79))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jplLayoutLayout.createSequentialGroup()
                        .addComponent(lblExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jplLayoutLayout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addGap(53, 53, 53))))
        );
        jplLayoutLayout.setVerticalGroup(
            jplLayoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jplLayoutLayout.createSequentialGroup()
                .addComponent(lblExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jplLayoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jplLayoutLayout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(lblTitle)
                        .addGap(30, 30, 30)
                        .addComponent(jplUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jplPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jplLayoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCheckBox1)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addComponent(btnDangNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jplLayoutLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                        .addComponent(lblAvatar, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49)))
                .addGroup(jplLayoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDangKy)
                    .addComponent(jLabel3))
                .addGap(21, 21, 21))
        );

        lblTitle.getAccessibleContext().setAccessibleName("Đăng nhập hệ thống ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jplLayout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jplLayout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main (String[] args){
        try {
            LOCK_SOCKET = new ServerSocket(3105); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Ứng dụng đã được mở!\nKhông thể khởi động thêm phiên mới.",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
    
    private void setupBackground() {
        try {
            URL iconURL = getClass().getResource("/resources/bg.png");
            if (iconURL == null) {
                System.err.println("Không tìm thấy ảnh nền!");
                return;
            }

            ImageIcon bgIcon = new ImageIcon(iconURL);
            backgroundLabel = new JLabel(bgIcon);
            backgroundLabel.setOpaque(true);

            // Chuyển layout sang null để tùy ý định vị
            getContentPane().setLayout(null);

            // Lấy kích thước form login
            int formW = jplLayout.getPreferredSize().width;
            int formH = jplLayout.getPreferredSize().height;

            int padding = 50; 
            backgroundLabel.setBounds(0, 0, formW + padding, formH + padding);
            jplLayout.setBounds(padding / 2, padding / 2, formW, formH);

            jplLayout.setOpaque(true);

            getContentPane().add(backgroundLabel);
            getContentPane().add(jplLayout);

            // Đảm bảo form login nổi trên background
            getContentPane().setComponentZOrder(jplLayout, 0);
            getContentPane().setComponentZOrder(backgroundLabel, 1);

            setSize(backgroundLabel.getWidth(), backgroundLabel.getHeight());
            setLocationRelativeTo(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setupLabelIcons() {
        resizeLabelIcon(lblUser, "/resources/email.png");
        resizeLabelIcon(lblPass, "/resources/pass.png");
        resizeLabelIcon(lblAvatar, "/resources/login.png");
        resizeLabelIcon(lblExit, "/resources/close.png");
        resizeLabelIcon(lblShowPass, "/resources/eye_closed.png");
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

    private void lblShowPassMouseClicked(java.awt.event.MouseEvent evt) {
        if (showing) {
            jpfPass.setEchoChar('•'); // ẩn mật khẩu
            resizeLabelIcon(lblShowPass, "/resources/eye_closed.png");
        } else {
            jpfPass.setEchoChar((char)0); // hiện mật khẩu
            resizeLabelIcon(lblShowPass, "/resources/eye_open.png");
        }
        showing = !showing;
   }
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDangNhap;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPasswordField jpfPass;
    private javax.swing.JPanel jplLayout;
    private javax.swing.JPanel jplPass;
    private javax.swing.JPanel jplUser;
    private javax.swing.JTextField jtbEmail;
    private javax.swing.JLabel lblAvatar;
    private javax.swing.JLabel lblDangKy;
    private javax.swing.JLabel lblExit;
    private javax.swing.JLabel lblPass;
    private javax.swing.JLabel lblShowPass;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUser;
    // End of variables declaration//GEN-END:variables
}
