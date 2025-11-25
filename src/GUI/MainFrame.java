/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUI;

import Model.User;
import Model.UserSession;
import java.awt.Image;
import java.net.ServerSocket;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import Utils.AppConfig;
import java.awt.BorderLayout;

/**
 *
 * @author Admin
 */
public class MainFrame extends javax.swing.JFrame {

    private static ServerSocket LOCK_SOCKET;
    /**
     * Creates new form MainFrame
     */ 
    public MainFrame() {
        AppConfig.load();
        initComponents();
        setupIcon();
        setupMenuIcons();
        setSize(1300, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        // Mở tab thống kê báo cáo mặc định
        openThongKeBaoCao();
        // Action listener cho menu và button
        setupActionListeners();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.applyAppTheme();
            frame.updateFont();
            frame.setVisible(true);
        });
    }
    
    public void openCategoryTab() {
        User currentUser = getCurrentUserOrShowError();
        if (currentUser == null) return;
        panelQuanLyDanhMuc pnlQLDM = new panelQuanLyDanhMuc(currentUser.getId());
        openTab("Danh mục chi tiêu", pnlQLDM);
    }
    
    // Tab thống kê báo cáo
    public void openThongKeBaoCao() {
        User currentUser = getCurrentUserOrShowError();
        if (currentUser == null) return;
        JPanel defaultTab = (JPanel) tabMain.getComponentAt(0);
        defaultTab.removeAll();
        panelBaoCaoThongKe pnlTKBC = new panelBaoCaoThongKe(currentUser.getId());
        defaultTab.setLayout(new BorderLayout());
        defaultTab.add(pnlTKBC, BorderLayout.CENTER);
        defaultTab.revalidate();
        defaultTab.repaint();
        tabMain.setSelectedIndex(0);
        refreshUI(); 
    }
    
    public void openTransactionTab(){
        for (int i = 0; i < tabMain.getTabCount(); i++) {
            if (tabMain.getTitleAt(i).equals("Danh sách giao dịch")) {
                tabMain.removeTabAt(i);
                break;
            }
        }

        User currentUser = UserSession.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Chưa đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        panelQuanLyGiaoDich pnlQLGD = new panelQuanLyGiaoDich(currentUser.getId());
        tabMain.addTab("Danh sách giao dịch", pnlQLGD);
        tabMain.setSelectedComponent(pnlQLGD);
    }
    
    //tab thêm gia dịch 
    public void openAddTransactionTab() {
        User currentUser = getCurrentUserOrShowError();
        if (currentUser == null) return;
        panelThemGiaoDich pnlTGD = new panelThemGiaoDich(currentUser.getId());
        openTab("Thêm giao dịch", pnlTGD);
    }
    
    // Phương thức chung để mở tab: Xóa tab cũ nếu có, thêm tab mới, và refresh UI
    private void openTab(String title, JPanel panel) {
        // Xóa tab cũ nếu tồn tại
        for (int i = 0; i < tabMain.getTabCount(); i++) {
            if (tabMain.getTitleAt(i).equals(title)) {
                tabMain.removeTabAt(i);
                break;
            }
        }
        // Thêm tab mới
        tabMain.addTab(title, panel);
        tabMain.setSelectedComponent(panel);
        // Refresh UI để áp dụng theme/font cho tab mới
        refreshUI();
    }
    
    // lấy userid hiện tại
    private User getCurrentUserOrShowError() {
        User currentUser = UserSession.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Chưa đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return currentUser;
    }
    
    private void refreshUI() {
        applyAppTheme();
        updateFont();
        repaint();
    }
    
    // Setup action listeners cho menu và button
    private void setupActionListeners() {
        // Menu Đăng xuất
        mnDangXuat.addActionListener(evt -> {
            try {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    this.dispose();
                    new LoginFrame().setVisible(true);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi đăng xuất: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Menu Khóa
        mnKhoa.addActionListener(evt -> {
            try {
                this.setEnabled(false);
                LoginFrame lockFrame = new LoginFrame(this);
                lockFrame.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi khóa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Button Lock
        btnLock.addActionListener(evt -> {
            try {
                this.setEnabled(false);
                LoginFrame lockFrame = new LoginFrame(this);
                lockFrame.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi khóa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Các button và menu khác sử dụng lambda ngắn gọn
        btnThemChiTieu.addActionListener(evt -> openAddTransactionTab());
        btnListChiTieu.addActionListener(evt -> openTransactionTab());
        btnListDanhMuc.addActionListener(evt -> openCategoryTab());
        btnBieuDoChTieu.addActionListener(evt -> openThongKeBaoCao());
        btnCloseTab.addActionListener(evt -> {
            int selectedIndex = tabMain.getSelectedIndex();
            if (selectedIndex > 0) {
                tabMain.removeTabAt(selectedIndex);
            }
            if (tabMain.getTabCount() > 0) {
                tabMain.setSelectedIndex(0);
            }
        });
        
        // Menu Cài đặt
        mnCaiDat.addActionListener(evt -> openSettingsTab());
        // Menu Hướng dẫn
        mnHuongDan.addActionListener(evt -> openHelpTab());
        // Menu Về hệ thống
        mnHeThong.addActionListener(evt -> openAboutTab());
        // Các menu quản lý
        mnChi.addActionListener(evt -> openAddTransactionTab());
        mnDSChi.addActionListener(evt -> openTransactionTab());
        mnDSDM.addActionListener(evt -> openCategoryTab());
        mnBieuDo.addActionListener(evt -> openThongKeBaoCao());
    }
    
    private void openSettingsTab() {
        User currentUser = getCurrentUserOrShowError();
        if (currentUser == null) return;
        panelCaiDat pnlCaiDat = new panelCaiDat(currentUser.getId());
        openTab("Cài đặt", pnlCaiDat);
    }

    private void openHelpTab() {
        panelHuongDan pnlHuongDan = new panelHuongDan();
        openTab("Hướng dẫn", pnlHuongDan);
    }
 
    private void openAboutTab() {
        panelHeThong pnlHeThong = new panelHeThong();
        openTab("Về chúng tôi", pnlHeThong);
    }
    /**
     * This method is called from within the constructor to initialize sthe form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jToolBar1 = new javax.swing.JToolBar();
        btnThemChiTieu = new javax.swing.JButton();
        btnListChiTieu = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        btnListDanhMuc = new javax.swing.JButton();
        jSeparator11 = new javax.swing.JToolBar.Separator();
        btnBieuDoChTieu = new javax.swing.JButton();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        btnLock = new javax.swing.JButton();
        btnCloseTab = new javax.swing.JButton();
        tabMain = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnDagXuat = new javax.swing.JMenu();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        mnDangXuat = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        mnKhoa = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mnCaiDat = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenu2 = new javax.swing.JMenu();
        mnChi = new javax.swing.JMenuItem();
        mnDSChi = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        mnDSDM = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        mnBieuDo = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        mnHuongDan = new javax.swing.JMenuItem();
        mnHeThong = new javax.swing.JMenuItem();

        jMenuItem1.setText("jMenuItem1");

        jMenuItem3.setText("jMenuItem3");

        jMenuItem4.setText("jMenuItem4");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar1.setRollover(true);

        btnThemChiTieu.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnThemChiTieu.setFocusable(false);
        btnThemChiTieu.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnThemChiTieu.setIconTextGap(3);
        btnThemChiTieu.setLabel("Thêm chi tiêu");
        btnThemChiTieu.setMargin(new java.awt.Insets(5, 10, 5, 10));
        btnThemChiTieu.setMultiClickThreshhold(6L);
        btnThemChiTieu.setName("160"); // NOI18N
        btnThemChiTieu.setRolloverEnabled(false);
        btnThemChiTieu.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnThemChiTieu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemChiTieuActionPerformed(evt);
            }
        });
        jToolBar1.add(btnThemChiTieu);

        btnListChiTieu.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnListChiTieu.setText("Danh sách chi tiêu ");
        btnListChiTieu.setFocusable(false);
        btnListChiTieu.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnListChiTieu.setIconTextGap(3);
        btnListChiTieu.setMargin(new java.awt.Insets(5, 10, 5, 10));
        btnListChiTieu.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnListChiTieu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListChiTieuActionPerformed(evt);
            }
        });
        jToolBar1.add(btnListChiTieu);
        btnListChiTieu.getAccessibleContext().setAccessibleName("Danh sách chi tiêu");

        jToolBar1.add(jSeparator8);

        btnListDanhMuc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnListDanhMuc.setText("Danh sách danh mục ");
        btnListDanhMuc.setFocusable(false);
        btnListDanhMuc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnListDanhMuc.setIconTextGap(3);
        btnListDanhMuc.setMargin(new java.awt.Insets(5, 10, 5, 10));
        btnListDanhMuc.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnListDanhMuc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListDanhMucActionPerformed(evt);
            }
        });
        jToolBar1.add(btnListDanhMuc);
        jToolBar1.add(jSeparator11);

        btnBieuDoChTieu.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBieuDoChTieu.setText("Biểu đồ chi tiêu");
        btnBieuDoChTieu.setFocusable(false);
        btnBieuDoChTieu.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBieuDoChTieu.setIconTextGap(3);
        btnBieuDoChTieu.setMargin(new java.awt.Insets(5, 10, 5, 10));
        btnBieuDoChTieu.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBieuDoChTieu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBieuDoChTieuActionPerformed(evt);
            }
        });
        jToolBar1.add(btnBieuDoChTieu);
        jToolBar1.add(jSeparator10);

        btnLock.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLock.setText("Lock");
        btnLock.setFocusable(false);
        btnLock.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLock.setIconTextGap(3);
        btnLock.setMargin(new java.awt.Insets(5, 10, 5, 10));
        btnLock.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnLock);

        btnCloseTab.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCloseTab.setText("Close Tab");
        btnCloseTab.setFocusable(false);
        btnCloseTab.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCloseTab.setIconTextGap(3);
        btnCloseTab.setMargin(new java.awt.Insets(5, 10, 5, 10));
        btnCloseTab.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCloseTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseTabActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCloseTab);

        tabMain.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 841, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 430, Short.MAX_VALUE)
        );

        tabMain.addTab("Trang chủ ", jPanel1);

        mnDagXuat.setText("Hệ thống");
        mnDagXuat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        mnDagXuat.add(jSeparator1);
        mnDagXuat.add(jSeparator7);

        mnDangXuat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        mnDangXuat.setText("Đăng xuất ");
        mnDagXuat.add(mnDangXuat);
        mnDagXuat.add(jSeparator9);

        mnKhoa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        mnKhoa.setLabel("Khóa hệ thống");
        mnDagXuat.add(mnKhoa);
        mnDagXuat.add(jSeparator2);
        mnDagXuat.add(jSeparator3);

        mnCaiDat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        mnCaiDat.setText("Cái đặt ");
        mnCaiDat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnCaiDatActionPerformed(evt);
            }
        });
        mnDagXuat.add(mnCaiDat);
        mnDagXuat.add(jSeparator4);

        jMenuBar1.add(mnDagXuat);

        jMenu2.setText("Quản lý");
        jMenu2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        mnChi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        mnChi.setText("Thêm mới giao dịch");
        mnChi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnChiActionPerformed(evt);
            }
        });
        jMenu2.add(mnChi);

        mnDSChi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        mnDSChi.setText("Danh sách giao dịch");
        mnDSChi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnDSChiActionPerformed(evt);
            }
        });
        jMenu2.add(mnDSChi);
        jMenu2.add(jSeparator5);
        jMenu2.add(jSeparator6);

        mnDSDM.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        mnDSDM.setText("Danh sách danh mục hệ thống ");
        mnDSDM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnDSDMActionPerformed(evt);
            }
        });
        jMenu2.add(mnDSDM);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Thống kê");
        jMenu3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        mnBieuDo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        mnBieuDo.setText("Bào cáo & thống kê");
        mnBieuDo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnBieuDoActionPerformed(evt);
            }
        });
        jMenu3.add(mnBieuDo);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("Trợ giúp");
        jMenu4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        mnHuongDan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        mnHuongDan.setText("Hướng dẫn ");
        mnHuongDan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnHuongDanActionPerformed(evt);
            }
        });
        jMenu4.add(mnHuongDan);

        mnHeThong.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        mnHeThong.setText("Về hệ thống ");
        mnHeThong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnHeThongActionPerformed(evt);
            }
        });
        jMenu4.add(mnHeThong);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabMain))
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tabMain))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnThemChiTieuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemChiTieuActionPerformed
        openAddTransactionTab();
    }//GEN-LAST:event_btnThemChiTieuActionPerformed

    private void btnListChiTieuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListChiTieuActionPerformed
        openTransactionTab();
    }//GEN-LAST:event_btnListChiTieuActionPerformed

    // mở tab cài đặt 
    private void mnCaiDatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnCaiDatActionPerformed
        // Kiểm tra xm tab cài đặt có chưa 
        int index = -1;
        for (int i = 0; i < tabMain.getTabCount(); i++) {
            if (tabMain.getTitleAt(i).equals("Cài đặt")) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            // Lấy user hiện tại từ session
            User currentUser = UserSession.getCurrentUser();
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "Chưa đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tạo panelCaiDat mới, truyền UserService hoặc userId nếu cần
            panelCaiDat pnlCaiDat = new panelCaiDat(currentUser.getId()); 
            tabMain.addTab("Cài đặt", pnlCaiDat);
            tabMain.setSelectedComponent(pnlCaiDat); 
        } else {
            // Nếu có rồi thì chỉ chuyển sang tab đó
            tabMain.setSelectedIndex(index);
        }
    }//GEN-LAST:event_mnCaiDatActionPerformed

    // close tab 
    private void btnCloseTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseTabActionPerformed
        int selectedIndex = tabMain.getSelectedIndex(); 
        if(selectedIndex > 0) {
            tabMain.removeTabAt(selectedIndex);
        }
        if(tabMain.getTabCount() > 0){
            tabMain.setSelectedIndex(0);
        }
    }//GEN-LAST:event_btnCloseTabActionPerformed

    private void mnHuongDanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnHuongDanActionPerformed
        // Kiểm tra xm tab hướng dẫn có chưa 
        int index = -1;
        for (int i = 0; i < tabMain.getTabCount(); i++) {
            if (tabMain.getTitleAt(i).equals("Hưỡng dẫn")) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            // Nếu chưa có thì thêm mới
            panelHuongDan pnlHuongDan = new panelHuongDan(); 
            tabMain.addTab("Hướng dẫn", pnlHuongDan);
            tabMain.setSelectedComponent(pnlHuongDan); 
        } else {
            // Nếu có rồi thì chỉ chuyển sang tab đó
            tabMain.setSelectedIndex(index);
        }
    }//GEN-LAST:event_mnHuongDanActionPerformed

    private void mnHeThongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnHeThongActionPerformed
        // Kiểm tra xm tab thông tin có chưa 
        int index = -1;
        for (int i = 0; i < tabMain.getTabCount(); i++) {
            if (tabMain.getTitleAt(i).equals("Về chúng tôi")) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            // Nếu chưa có thì thêm mới
            panelHeThong pnlHeThong = new panelHeThong(); 
            tabMain.addTab("Vè chúng tôi", pnlHeThong);
            tabMain.setSelectedComponent(pnlHeThong); 
        } else {
            // Nếu có rồi thì chỉ chuyển sang tab đó
            tabMain.setSelectedIndex(index);
        }
    }//GEN-LAST:event_mnHeThongActionPerformed

    private void btnListDanhMucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListDanhMucActionPerformed
        openCategoryTab();
    }//GEN-LAST:event_btnListDanhMucActionPerformed

    private void btnBieuDoChTieuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBieuDoChTieuActionPerformed
        openThongKeBaoCao();
    }//GEN-LAST:event_btnBieuDoChTieuActionPerformed

    private void mnDSDMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnDSDMActionPerformed
        openCategoryTab();
    }//GEN-LAST:event_mnDSDMActionPerformed

    private void mnDSChiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnDSChiActionPerformed
        openTransactionTab();
    }//GEN-LAST:event_mnDSChiActionPerformed

    private void mnChiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnChiActionPerformed
        openAddTransactionTab();
    }//GEN-LAST:event_mnChiActionPerformed

    private void mnBieuDoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnBieuDoActionPerformed
        openThongKeBaoCao();
    }//GEN-LAST:event_mnBieuDoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBieuDoChTieu;
    private javax.swing.JButton btnCloseTab;
    private javax.swing.JButton btnListChiTieu;
    private javax.swing.JButton btnListDanhMuc;
    private javax.swing.JButton btnLock;
    private javax.swing.JButton btnThemChiTieu;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuItem mnBieuDo;
    private javax.swing.JMenuItem mnCaiDat;
    private javax.swing.JMenuItem mnChi;
    private javax.swing.JMenuItem mnDSChi;
    private javax.swing.JMenuItem mnDSDM;
    private javax.swing.JMenu mnDagXuat;
    private javax.swing.JMenuItem mnDangXuat;
    private javax.swing.JMenuItem mnHeThong;
    private javax.swing.JMenuItem mnHuongDan;
    private javax.swing.JMenuItem mnKhoa;
    private javax.swing.JTabbedPane tabMain;
    // End of variables declaration//GEN-END:variables

    private void setupIcon() {
        resizeIcon(btnThemChiTieu, "/resources/add.png");
        resizeIcon(btnListChiTieu, "/resources/expenses.png");
        resizeIcon(btnBieuDoChTieu, "/resources/pie_chart.png");
        resizeIcon(btnListDanhMuc, "/resources/categories.png");
        resizeIcon(btnLock, "/resources/lock.png");
        resizeIcon(btnCloseTab, "/resources/tab.png");
    }

    private void resizeIcon(JButton btn, String path) {
        try {
            URL iconURL = getClass().getResource(path);
            if (iconURL != null) {
                ImageIcon originalIcon = new ImageIcon(iconURL);
                Image img = originalIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
            } else {
                System.err.println("Không tìm thấy icon: " + path);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi load icon: " + path + " - " + e.getMessage());
        }
    }
    
    private void setupMenuIcons() {
        resizeMenuIcon(mnDangXuat, "/resources/logout.png");   
        resizeMenuIcon(mnKhoa, "/resources/lock.png");
        resizeMenuIcon(mnCaiDat, "/resources/gear.png");       
        resizeMenuIcon(mnBieuDo, "/resources/chart.png");    
        resizeMenuIcon(mnHuongDan, "/resources/instruct.png");    
        resizeMenuIcon(mnHeThong, "/resources/systems.png");   
        resizeMenuIcon(mnCaiDat, "/resources/gear.png");    
        resizeMenuIcon(mnChi, "/resources/add.png");   
        resizeMenuIcon(mnDSChi, "/resources/accounting.png");   
        resizeMenuIcon(mnDSDM, "/resources/categories.png");   
  
    }

    private void resizeMenuIcon(javax.swing.JMenuItem item, String path) {
        try {
            URL iconURL = getClass().getResource(path);
            if (iconURL == null) {
                iconURL = getClass().getResource("/" + path);
                if (iconURL == null) iconURL = getClass().getClassLoader().getResource(path);
            }
            if (iconURL != null) {
                ImageIcon originalIcon = new ImageIcon(iconURL);
                Image img = originalIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                item.setIcon(new ImageIcon(img));
            } else {
                System.err.println("Không tìm thấy icon: " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void applyAppTheme() {
        String theme = AppConfig.getTheme();
        applyThemeToContainer(this.getContentPane(), theme);
    }
    
    private static void applyThemeToContainer(java.awt.Container container, String theme) {
        java.awt.Color bgColor = "Dark".equalsIgnoreCase(theme) ? new java.awt.Color(45, 45, 45) : java.awt.Color.WHITE;
        java.awt.Color fgColor = "Dark".equalsIgnoreCase(theme) ? java.awt.Color.WHITE : java.awt.Color.BLACK;
        container.setBackground(bgColor);
        container.setForeground(fgColor);
        for (java.awt.Component comp : container.getComponents()) {
            comp.setBackground(bgColor);
            comp.setForeground(fgColor);
            if (comp instanceof javax.swing.JPanel || comp instanceof javax.swing.JTabbedPane) {
                applyThemeToContainer((java.awt.Container) comp, theme);
            }
        }
        container.repaint();
    }
    
    public void updateFont() {
        String fontFamily = AppConfig.getFontFamily();
        int fontSize = AppConfig.getFontSize();
        java.awt.Font font = new java.awt.Font(fontFamily, java.awt.Font.PLAIN, fontSize);
        updateFontForContainer(this.getContentPane(), font);
    }
    
    private static void updateFontForContainer(java.awt.Container container, java.awt.Font font) {
        container.setFont(font);
        for (java.awt.Component comp : container.getComponents()) {
            comp.setFont(font);
            if (comp instanceof javax.swing.JPanel || comp instanceof javax.swing.JTabbedPane) {
                updateFontForContainer((java.awt.Container) comp, font); // đệ quy
            }
        }
        container.repaint();
    }
}
