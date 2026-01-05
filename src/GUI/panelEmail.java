/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI;

import Model.Email;
import Service.EmailService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Nam
 */
public class panelEmail extends javax.swing.JPanel {

 private int userId;
    private EmailService emailService;
    private DefaultListModel<EmailListItem> listModel;
    private List<Email> allEmails;
    private Email currentEmail;
    private JLabel lblUnreadCount;
    private JTextArea txtEmailContent;
    private JLabel lblEmailTitle;
    private JLabel lblEmailDate;
    private JLabel lblEmailType;
    private JLabel lblEmailStatus;
    
    // Inner class để lưu thông tin email trong JList
    private class EmailListItem {
        private Email email;
        
        public EmailListItem(Email email) {
            this.email = email;
        }
        
        public Email getEmail() {
            return email;
        }
        
        @Override
      public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String pinIcon = email.isLaGhim() ? "📌 " : "";
        String statusIcon = email.getTrangThai() == Email.TrangThai.CHUA_DOC ? "📩 " : "✅ ";
        String title = email.getTieuDe();
        if (title.length() > 25) {
            title = title.substring(0, 22) + "...";
        }

        // Hiển thị ngày đọc (nếu đã đọc) hoặc ngày gửi (nếu chưa đọc)
        String dateInfo;
        if (email.getTrangThai() == Email.TrangThai.DA_DOC && email.getNgayDoc() != null) {
            dateInfo = "Đã đọc: " + sdf.format(email.getNgayDoc());
        } else {
            dateInfo = "Ngày gửi: " + sdf.format(email.getNgayTao());
        }

        return "<html>" + statusIcon + pinIcon + title + "<br><small style='color:gray;'>" + dateInfo + "</small></html>";
        }
    }

    public panelEmail() {
        initComponents();
    }
    
    public panelEmail(int userId) {
        this.userId = userId;
        this.emailService = new EmailService();
        initComponents();
        
        // Setup list model
        listModel = new DefaultListModel<>();
        jList1.setModel(listModel);
        jList1.setCellRenderer(new EmailListCellRenderer());
        
        // Setup email content panel
        setupEmailContentPanel();
        
        // Add list selection listener
        jList1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    displaySelectedEmail();
                }
            }
        });
        
        // Load emails
        loadEmails();
        updateUnreadCount();
    }
    
    // Custom cell renderer cho JList
    private class EmailListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof EmailListItem) {
                EmailListItem item = (EmailListItem) value;
                Email email = item.getEmail();
                
                // Đổi màu cho email chưa đọc
                if (email.getTrangThai() == Email.TrangThai.CHUA_DOC) {
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                }
                
                label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            }
            
            return label;
        }
    }
    
    private void setupEmailContentPanel() {
        jPanel4.setLayout(new BorderLayout(10, 10));
        jPanel4.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header panel với thông tin email
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        lblEmailTitle = new JLabel("Chọn một email để xem nội dung");
        lblEmailTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        lblEmailType = new JLabel("");
        lblEmailType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        lblEmailDate = new JLabel("");
        lblEmailDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        lblEmailStatus = new JLabel("");
        lblEmailStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        
        headerPanel.add(lblEmailTitle);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(lblEmailType);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(lblEmailDate);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(lblEmailStatus);
        
        // Content panel
        txtEmailContent = new JTextArea();
        txtEmailContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtEmailContent.setEditable(false);
        txtEmailContent.setLineWrap(true);
        txtEmailContent.setWrapStyleWord(true);
        txtEmailContent.setBackground(new Color(248, 249, 250));
        txtEmailContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        txtEmailContent.setText("Nội dung email sẽ hiển thị ở đây khi bạn chọn một email từ danh sách bên trái.");
        
        JScrollPane scrollPane = new JScrollPane(txtEmailContent);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        jPanel4.add(headerPanel, BorderLayout.NORTH);
        jPanel4.add(scrollPane, BorderLayout.CENTER);
        

    jLabel2.setText("Email");
    }
    
    private void loadEmails() {
        listModel.clear();
        allEmails = emailService.getEmailsByUserId(userId);
        
        // Sắp xếp: email ghim lên đầu, sau đó theo ngày tạo
        allEmails.sort((e1, e2) -> {
            if (e1.isLaGhim() != e2.isLaGhim()) {
                return e1.isLaGhim() ? -1 : 1;
            }
            return e2.getNgayTao().compareTo(e1.getNgayTao());
        });
        
        for (Email email : allEmails) {
            listModel.addElement(new EmailListItem(email));
        }
        
        // Select first email if available
        if (!allEmails.isEmpty()) {
            jList1.setSelectedIndex(0);
        }
    }
    
    private void displaySelectedEmail() {
        int selectedIndex = jList1.getSelectedIndex();
        if (selectedIndex == -1) {
            return;
        }
        
        EmailListItem item = listModel.getElementAt(selectedIndex);
        currentEmail = item.getEmail();
        
        jLabel2.setText(currentEmail.getTieuDe().length() > 50 ? 
        currentEmail.getTieuDe().substring(0, 47) + "..." : 
        currentEmail.getTieuDe());
        
        // Đánh dấu đã đọc
        if (currentEmail.getTrangThai() == Email.TrangThai.CHUA_DOC) {
            emailService.markAsRead(currentEmail.getId());
            currentEmail.setTrangThai(Email.TrangThai.DA_DOC);
            // Update list display
            listModel.set(selectedIndex, new EmailListItem(currentEmail));
            updateUnreadCount();
        }
        
        // Display email content
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        String pinIcon = currentEmail.isLaGhim() ? "📌 " : "";
        lblEmailTitle.setText(pinIcon + currentEmail.getTieuDe());
        lblEmailType.setText("Loại: " + getLoaiEmailText(currentEmail.getLoaiEmail()));
        lblEmailDate.setText("Ngày gửi: " + sdf.format(currentEmail.getNgayTao()));
        lblEmailStatus.setText("Trạng thái: " + 
            (currentEmail.getTrangThai() == Email.TrangThai.CHUA_DOC ? "Chưa đọc" : "Đã đọc"));
        txtEmailContent.setText(currentEmail.getNoiDung());
        txtEmailContent.setCaretPosition(0);
    }
    
    private String getLoaiEmailText(Email.LoaiEmail loai) {
        switch (loai) {
            case THONG_BAO_GIAO_DICH: return "Giao dịch";
            case CANH_BAO: return "Cảnh báo";
            case BAO_CAO: return "Báo cáo";
            default: return loai.toString();
        }
    }
    
    private void updateUnreadCount() {
        int count = emailService.getUnreadCount(userId);
        jLabel1.setText("Email (" + count + ")");
    }
    
    private void togglePin() {
        if (currentEmail == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn email!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean success = emailService.togglePin(currentEmail.getId());
        if (success) {
            currentEmail.setLaGhim(!currentEmail.isLaGhim());
            refreshData();
            String message = currentEmail.isLaGhim() ? "Đã ghim email!" : "Đã bỏ ghim email!";
            JOptionPane.showMessageDialog(this, message);
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi ghim/bỏ ghim email!");
        }
    }
    
    private void deleteEmail() {
        if (currentEmail == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn email để xóa!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa email này?\n\n" + currentEmail.getTieuDe(),
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        boolean success = emailService.deleteEmail(currentEmail.getId());
        if (success) {
            JOptionPane.showMessageDialog(this, "Xóa email thành công!");
            currentEmail = null;
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa email!");
        }
    }
    
    private void refreshData() {
        int selectedIndex = jList1.getSelectedIndex();
        loadEmails();
        updateUnreadCount();
        
        // Try to maintain selection
        if (selectedIndex >= 0 && selectedIndex < listModel.getSize()) {
            jList1.setSelectedIndex(selectedIndex);
        } else if (!listModel.isEmpty()) {
            jList1.setSelectedIndex(0);
        }
    }
    
    private void showOptionsMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Menu item: Ghim/Bỏ ghim
        JMenuItem menuPin = new JMenuItem(
            currentEmail != null && currentEmail.isLaGhim() ? 
            "📌 Bỏ ghim email" : "📌 Ghim email"
        );
        menuPin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        menuPin.addActionListener(e -> togglePin());
        
        // Menu item: Xóa
        JMenuItem menuDelete = new JMenuItem(" Xóa email");
        menuDelete.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        menuDelete.addActionListener(e -> deleteEmail());
        
        // Menu item: Làm mới
        JMenuItem menuRefresh = new JMenuItem("Làm mới");
        menuRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        menuRefresh.addActionListener(e -> refreshData());
        
        popupMenu.add(menuPin);
        popupMenu.addSeparator();
        popupMenu.add(menuDelete);
        popupMenu.addSeparator();
        popupMenu.add(menuRefresh);
        
        // Show popup menu below button
        popupMenu.show(jButton2, 0, jButton2.getHeight());
    }


    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jPanel3 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(895, 590));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel2MouseExited(evt);
            }
        });

        jToolBar1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/gmail_1.png"))); // NOI18N
        jLabel1.setText("Email");
        jLabel1.setPreferredSize(new java.awt.Dimension(270, 50));
        jToolBar1.add(jLabel1);

        jScrollPane1.setViewportView(jList1);

        jToolBar1.add(jScrollPane1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/3cham.png"))); // NOI18N
        jButton2.setBorder(null);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel2.setText("Email");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 588, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(23, 23, 23))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    
    private void jPanel2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseEntered

    }//GEN-LAST:event_jPanel2MouseEntered

    private void jPanel2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseExited

    }//GEN-LAST:event_jPanel2MouseExited

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        showOptionsMenu();
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList<EmailListItem> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}
