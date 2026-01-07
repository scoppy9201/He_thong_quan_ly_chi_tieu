/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI;

import Model.Category;
import Model.Transaction;
import Model.User;
import Model.UserSession;
import Service.CategoryService;
import Service.EmailService;
import Service.TransactionService;
import Service.UserService;
import Utils.HintUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.List;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import static java.time.LocalDate.parse;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


/**
 *
 * @author Admin
 */
public class panelQuanLyGiaoDich extends javax.swing.JPanel {

    private Transaction selectedTransaction = null;
    private panelQuanLyNganSach panelNganSach;
    private boolean isEditMode = false;
    private javax.swing.Timer searchTimer;
    private int userId;
    private User currentUser;
    private JTabbedPane tabMain;
    
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);  // Formatter để parse
    private File selectedImageFile = null;
    private javax.swing.JButton btnUploadImage;

    // Contructor khởi tạo panel 
    public panelQuanLyGiaoDich(int userId) {
        this.userId = userId;
        this.currentUser = new UserService().getUserById(userId);
        initComponents();
        SwingUtilities.invokeLater(() -> {
            setupLabelIcons();
            loadCategoriesToComboBox();
            loadTransactions(); 
            lockAllFields();
            resetFilter(); 
        });
        setupButtonListeners();
        setupSearchListener();
        tabMain = new javax.swing.JTabbedPane();
        
        // Set hints
        HintUtils.setHint(txtTenDM, "Hệ thống tự động hiển thị...");
        HintUtils.setHint(txtSoTien, "Nhập số tiền...");
        HintUtils.setHint(txtLoaiGD, "Hệ thống tự động hiển thị...");
        HintUtils.setHint(txtPhuongThuc, "Nhập phương thức...");
        HintUtils.setHint(txtGhiChu, "Nhập ghi chú...");
        
        lblAvatar.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        lblAvatar.setPreferredSize(new Dimension(120, 120));
    }

    // Đăng ký sự kiện cho các button và label 
    private void setupButtonListeners() {
        btnCapNhat.addActionListener(e -> updateTransaction());
        btnXoa.addActionListener(e -> deleteTransaction());
        btnHuyBo.addActionListener(e -> cancelTransaction());
        
        // Nút lọc và reset
        lblLoc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLoc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                applyFilter();
            }
        });
        
        lblReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblReset.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resetFilter();
            }
        });
        
        // Date pickers
        lblTime1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblTime1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showDatePickerForStart();
            }
        });
        
        lblTime2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblTime2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showDatePickerForEnd();
            }
        });
        
        setupAvatarClick();
    }
    
    // Đăng ký documentlistener cho ô tìm kiếm 
    private void setupSearchListener() {
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                scheduleSearch();
            }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                scheduleSearch();
            }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                scheduleSearch();
            }
        });
    }

    // delay tìm kiếm -> giảm tải request đẩy lên serve 
    private void scheduleSearch() {
        if (searchTimer != null) {
            searchTimer.stop();
        }
        searchTimer = new javax.swing.Timer(300, e -> performSearch());
        searchTimer.setRepeats(false);
        searchTimer.start();
    }
    
    // Hàm thực hiện tìm kiếm giao dịch 
    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadTransactions();
        } else {
            loadSearchResults(keyword);
        }
    }
    
    // Hiển thị data tìm kiếm => performSearch()
    private void loadSearchResults(String keyword) {
        try {
            List<Transaction> transactions = new TransactionService().search(keyword);
            // Lọc theo userId
            transactions = transactions.stream()
                .filter(t -> t != null && t.getNguoiDungId() == userId)
                .collect(Collectors.toList());
            displayTransactions(transactions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Load danh mục vào ComboBox
    private void loadCategoriesToComboBox() {
        try {
            List<Category> categories = new CategoryService().getAllCategories();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("Tất cả");
            
            for (Category cat : categories) {
                // Chỉ thêm danh mục con (có danh mục cha)
                if (cat.getDanhMucChaId() != null && cat.getDanhMucChaId() != 0) {
                    model.addElement(cat.getTenDanhMuc());
                }
            }
            
            jcbDanhMuc.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Setup ComboBox Loại giao dịch
        DefaultComboBoxModel<String> loaiModel = new DefaultComboBoxModel<>();
        loaiModel.addElement("Tất cả");
        loaiModel.addElement("Thu");
        loaiModel.addElement("Chi");
        jcbLoaiGiaoDich.setModel(loaiModel);
        
        // Setup ComboBox Phương thức
        DefaultComboBoxModel<String> phuongThucModel = new DefaultComboBoxModel<>();
        phuongThucModel.addElement("Tất cả");
        phuongThucModel.addElement("Tiền mặt");
        phuongThucModel.addElement("Chuyển khoản");
        jcbPhuongThuc.setModel(phuongThucModel);
    }

    private void showDatePickerForStart() {
        SpinnerDateModel model = new SpinnerDateModel();
        model.setValue(java.sql.Date.valueOf(LocalDate.now()));

        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);

        int option = JOptionPane.showConfirmDialog(
            this,
            spinner,
            "Chọn ngày bắt đầu",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            Date date = model.getDate();
            lblNgayBatDau.setText(dateFormat.format(date));
        }
    }

    private void showDatePickerForEnd() {
        SpinnerDateModel model = new SpinnerDateModel();
        model.setValue(java.sql.Date.valueOf(LocalDate.now()));

        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);

        int option = JOptionPane.showConfirmDialog(
            this,
            spinner,
            "Chọn ngày kết thúc",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            Date date = model.getDate();
            lblNgayKetThuc.setText(dateFormat.format(date));
        }
    }

    // Hàm xử lý bộ lọc 
    private void applyFilter() {
        try {
            String selectedDanhMuc = (String) jcbDanhMuc.getSelectedItem();
            String selectedLoai = (String) jcbLoaiGiaoDich.getSelectedItem();
            String selectedPhuongThuc = (String) jcbPhuongThuc.getSelectedItem();
            
            String fromDateStr = lblNgayBatDau.getText().trim();
            String toDateStr = lblNgayKetThuc.getText().trim();

            if (fromDateStr.isEmpty() || toDateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khoảng thời gian!");
                return;
            }

            LocalDate fromDate = LocalDate.parse(fromDateStr, DATE_FORMATTER);
            LocalDate toDate = LocalDate.parse(toDateStr, DATE_FORMATTER);

            if (fromDate.isAfter(toDate)) {
                JOptionPane.showMessageDialog(this,
                    "Ngày bắt đầu phải trước hoặc bằng ngày kết thúc!");
                return;
            }

            List<Transaction> transactions = new TransactionService().getWithFilters(
                userId, selectedDanhMuc, selectedLoai, selectedPhuongThuc, fromDate, toDate
            );

            displayTransactions(transactions);

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày không đúng (dd/MM/yyyy)!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lọc: " + e.getMessage());
        }
    }
    
    // Reset bộ lọc về mặc đinh 
    private void resetFilter() {
        jcbDanhMuc.setSelectedIndex(0);
        jcbLoaiGiaoDich.setSelectedIndex(0);
        jcbPhuongThuc.setSelectedIndex(0);
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        lblNgayBatDau.setText(currentDate);
        lblNgayKetThuc.setText(currentDate);
        loadTransactions();
    }
    
    // Hiển thị tất cả giao dịch của user 
    private void loadTransactions() {
        try {
            List<Transaction> transactions = new TransactionService().getByUser(userId);
            displayTransactions(transactions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Hiển thị danh sách các giao dịch của 1 user => loadTransactions()
    private void displayTransactions(List<Transaction> transactions) {
        panelGiaoDich.removeAll();
        panelGiaoDich.setLayout(new BoxLayout(panelGiaoDich, BoxLayout.Y_AXIS));
        panelGiaoDich.setBackground(new Color(248, 249, 250));
        panelGiaoDich.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblSoLuong.setText(String.valueOf(transactions != null ? transactions.size() : 0));

        if (transactions == null || transactions.isEmpty()) {
            showEmptyMessage();
        } else {
            addTransactionItems(transactions);
        }

        panelGiaoDich.revalidate();
        panelGiaoDich.repaint();
    }
    
    // Nếu data từ list Transaction = 0 => empty 
    private void showEmptyMessage() {
        JLabel lblEmpty = new JLabel("Không có giao dịch nào");
        lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblEmpty.setForeground(Color.GRAY);
        lblEmpty.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        panelGiaoDich.add(Box.createRigidArea(new Dimension(0, 50)));
        panelGiaoDich.add(lblEmpty);
    }
    
    // Nếu có data → tạo các TransactionItemPanel
    private void addTransactionItems(List<Transaction> transactions) {
        for (Transaction trans : transactions) {
            if (trans == null) continue;

            TransactionItemPanel itemPanel = createTransactionItem(trans);
            panelGiaoDich.add(itemPanel);
            panelGiaoDich.add(Box.createRigidArea(new Dimension(0, 10)));
        }
    }
    
    // Tạo 1 item panel với sự kiện => 1 giao dịch = 1 compoment riêng 
    private TransactionItemPanel createTransactionItem(Transaction trans) {
        TransactionItemPanel itemPanel = new TransactionItemPanel(trans);
        itemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        itemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showTransactionDetails(trans);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                itemPanel.setBackground(new Color(240, 248, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                itemPanel.setBackground(Color.WHITE);
            }
        });
        return itemPanel;
    }
    
    // Hiển thị chi tiết giao dịch 
    private void showTransactionDetails(Transaction trans) {
        // isEditMode => tránh user thay đổi transaction đang chỉnh sửa
        if (isEditMode || trans == null ) {
            return;
        }

        this.selectedTransaction = trans;
        this.selectedImageFile = null; // Reset

        txtTenDM.setText(trans.getTenDanhMuc() != null ? trans.getTenDanhMuc() : "");
        txtSoTien.setText(trans.getSoTien().toString());
        txtLoaiGD.setText(trans.getLoaiGiaoDich().name());
        txtPhuongThuc.setText(trans.getPhuongThuc());
        txtGhiChu.setText(trans.getGhiChu() != null ? trans.getGhiChu() : "");

        LocalDate ngayGiaoDich = trans.getNgayGiaoDich() != null ? trans.getNgayGiaoDich() : LocalDate.now();
        Date date = Date.from(ngayGiaoDich.atStartOfDay(ZoneId.systemDefault()).toInstant());
        jspNgayGiaoDich.setValue(date);

        // load ảnh hóa đơn
        if (trans.getAnhHoaDon() != null && !trans.getAnhHoaDon().isEmpty()) {
            displayInvoiceImage(trans.getAnhHoaDon());
        } else {
            lblAvatar.setIcon(null);
            lblAvatar.setText("Chưa có hóa đơn");
            lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        }
    }
    
    // Cập nhật giao dịch -> Gửi eamil sau khi cập nhật thành công 
    private void updateTransaction() {
        if (!isEditMode) {
            if (selectedTransaction == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn giao dịch cần cập nhật!");
                return;
            }
            unlockFieldsForEdit();
            btnCapNhat.setText("Xác nhận");
            btnXoa.setEnabled(false);
            btnThemMoi.setEnabled(false);
            return;
        }

        // Kiểm tra đầu vào request 
        if (!validateFields()) {
            return;
        }

        try {
            selectedTransaction.setSoTien(new java.math.BigDecimal(txtSoTien.getText().trim()));
            selectedTransaction.setPhuongThuc(txtPhuongThuc.getText().trim());
            selectedTransaction.setGhiChu(txtGhiChu.getText().trim());

            Date selectedDate = (Date) jspNgayGiaoDich.getValue();
            LocalDate localDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            selectedTransaction.setNgayGiaoDich(localDate);

            if (selectedImageFile != null) {
                String savedPath = saveImageToFolder(selectedImageFile);
                if (savedPath != null) {
                    selectedTransaction.setAnhHoaDon(savedPath);
                }
            }

            boolean success = new TransactionService().update(selectedTransaction);

            if (success) {
                EmailService emailService = new EmailService();
                emailService.sendTransactionUpdateNotification(currentUser, selectedTransaction);

                JOptionPane.showMessageDialog(this, "Cập nhật giao dịch thành công!");
                loadTransactions();
                resetToViewMode();
                selectedImageFile = null;
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật giao dịch thất bại!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
    
    // Xóa giao dịch -> Gửi email khi xóa thành công 
    private void deleteTransaction() {
        if (selectedTransaction == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn giao dịch cần xóa!");
            return;
        }
        
        int confirm = javax.swing.JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa giao dịch này?", "Xác nhận", 
            javax.swing.JOptionPane.YES_NO_OPTION);
        
        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            EmailService emailService = new EmailService();
            emailService.sendTransactionDeleteNotification(currentUser, selectedTransaction);
            boolean success = new TransactionService().delete(selectedTransaction.getId());
            
            if (success) {
                javax.swing.JOptionPane.showMessageDialog(this, "Xóa giao dịch thành công!");
                loadTransactions();
                resetToViewMode();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Xóa giao dịch thất bại!");
            }
        }
    }
    
    // Hủy bỏ thao tác hiện tại 
    private void cancelTransaction() {
        selectedTransaction = null;
        selectedImageFile = null;
        txtTenDM.setText("");
        txtSoTien.setText("");
        txtLoaiGD.setText("");
        txtPhuongThuc.setText("");
        txtGhiChu.setText("");
        jspNgayGiaoDich.setValue(new Date());

        // Reset ảnh
        lblAvatar.setIcon(null);
        lblAvatar.setText("");

        resetToViewMode();
    }
    
    private void lockAllFields() {
        txtTenDM.setEnabled(false);
        txtSoTien.setEnabled(false);
        txtLoaiGD.setEnabled(false);
        txtPhuongThuc.setEnabled(false);
        txtGhiChu.setEnabled(false);
        jspNgayGiaoDich.setEnabled(false);
        isEditMode = false;
    }
    
    private void unlockFieldsForEdit() {
        txtSoTien.setEnabled(true);
        txtPhuongThuc.setEnabled(true);
        txtGhiChu.setEnabled(true);
        jspNgayGiaoDich.setEnabled(true);
        // Danh mục và loại GD không cho sửa
        txtTenDM.setEnabled(false);
        txtLoaiGD.setEnabled(false);
        isEditMode = true;
    }
    
    // Reset về chế độ xem 
    private void resetToViewMode() {
        lockAllFields();
        btnCapNhat.setText("Cập nhật");
        btnCapNhat.setEnabled(true);
        btnXoa.setEnabled(true);
        btnThemMoi.setEnabled(true);
    }
    
    private void setupAvatarClick() {
        lblAvatar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblAvatar.setToolTipText("Click để xem hoặc thay đổi ảnh hóa đơn");

        lblAvatar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                handleAvatarClick(evt);
            }
        });
    }
    
    // Hàm xử lý sự kiện click vào hóa đơn 
    private void handleAvatarClick(java.awt.event.MouseEvent evt) {
        if (selectedTransaction == null) return;

        boolean hasImage = selectedTransaction.getAnhHoaDon() != null 
                           && !selectedTransaction.getAnhHoaDon().isEmpty();

        // Click chuột phải hoặc đang edit
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3 || isEditMode) {
            showImageOptions(hasImage);
        } else {
            // Click chuột trái -> Xem ảnh
            viewImage(hasImage);
        }
    }

    // Hiển thị menu options
    private void showImageOptions(boolean hasImage) {
        String[] options = {"Xem ảnh", "Thay đổi ảnh", "Hủy bỏ"};
        int choice = JOptionPane.showOptionDialog(
            this, "Bạn muốn thực hiện hành động nào?", "Tùy chọn",
            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
            null, options, options[0]
        );

        switch (choice) {
            case 0 -> viewImage(hasImage);
            case 1 -> changeImage();
        }
    }

    // Xem ảnh
    private void viewImage(boolean hasImage) {
        if (hasImage) {
            showFullSizeImage(selectedTransaction.getAnhHoaDon());
        } else {
            JOptionPane.showMessageDialog(this, "Giao dịch này chưa có hóa đơn!");
        }
    }

    // Thay đổi ảnh
    private void changeImage() {
        if (isEditMode) {
            uploadInvoiceImage();
        } else {
            JOptionPane.showMessageDialog(this,
                "Bạn chỉ có thể thay đổi ảnh khi đang chỉnh sửa!");
        }
    }
    
    // Hiển thị ảnh hóa đơn lên lblAvatar
    private void displayInvoiceImage(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                lblAvatar.setIcon(null);
                lblAvatar.setText("Chưa có hóa đơn");
                lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
                return;
            }

            ImageIcon icon;
            File imageFile = new File(imagePath);

            if (imageFile.exists()) {
                // Đường dẫn file local
                icon = new ImageIcon(imagePath);
            } else {
                // Thử load từ resources
                URL iconURL = getClass().getResource(imagePath);
                if (iconURL != null) {
                    icon = new ImageIcon(iconURL);
                } else {
                    lblAvatar.setIcon(null);
                    lblAvatar.setText("Ảnh không tồn tại");
                    return;
                }
            }

            // Scale ảnh cho vừa với lblAvatar
            int width = lblAvatar.getWidth() > 0 ? lblAvatar.getWidth() : 120;
            int height = lblAvatar.getHeight() > 0 ? lblAvatar.getHeight() : 120;

            Image scaledImg = icon.getImage().getScaledInstance(
                width, height, Image.SCALE_SMOOTH);
            lblAvatar.setIcon(new ImageIcon(scaledImg));
            lblAvatar.setText("");

        } catch (Exception e) {
            e.printStackTrace();
            lblAvatar.setIcon(null);
            lblAvatar.setText("Lỗi hiển thị ảnh");
        }
    }
    
    // Xem ảnh full size trong Dialog 
    private void showFullSizeImage(String imagePath) {
        try {
            File imageFile = new File(imagePath);

            if (!imageFile.exists()) {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy file ảnh: " + imagePath);
                return;
            }

            ImageIcon icon = new ImageIcon(imagePath);

            // Giới hạn kích thước tối đa để ảnh không quá lớn
            int maxWidth = 1000;
            int maxHeight = 800;

            Image img = icon.getImage();
            int imgWidth = icon.getIconWidth();
            int imgHeight = icon.getIconHeight();

            // Tính tỷ lệ scale
            double scale = Math.min(
                (double) maxWidth / imgWidth,
                (double) maxHeight / imgHeight
            );

            if (scale < 1) {
                int newWidth = (int) (imgWidth * scale);
                int newHeight = (int) (imgHeight * scale);
                img = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                icon = new ImageIcon(img);
            }

            JLabel label = new JLabel(icon);
            JScrollPane scrollPane = new JScrollPane(label);
            scrollPane.setPreferredSize(new Dimension(
                Math.min(imgWidth, maxWidth) + 20, 
                Math.min(imgHeight, maxHeight) + 20
            ));

            JOptionPane.showMessageDialog(this, 
                scrollPane, 
                "Hóa đơn giao dịch - " + selectedTransaction.getTenDanhMuc(), 
                JOptionPane.PLAIN_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Không thể hiển thị ảnh: " + e.getMessage());
        }
    }
    
    private void uploadInvoiceImage() {
        if (!isEditMode) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chuyển sang chế độ chỉnh sửa (nhấn nút Cập nhật)!");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn ảnh hóa đơn");

        // Chỉ cho phép chọn ảnh
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Image Files (*.jpg, *.jpeg, *.png)", "jpg", "jpeg", "png");
        fileChooser.setFileFilter(filter);

        // Set thư mục mặc định
        fileChooser.setCurrentDirectory(new File("uploads/invoices/"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();

            // Kiểm tra kích thước file (max 5MB)
            long fileSizeInMB = selectedImageFile.length() / (1024 * 1024);
            if (fileSizeInMB > 5) {
                JOptionPane.showMessageDialog(this,
                    "File ảnh quá lớn! Vui lòng chọn file nhỏ hơn 5MB.");
                selectedImageFile = null;
                return;
            }

            // Hiển thị preview
            displayInvoiceImage(selectedImageFile.getAbsolutePath());

            JOptionPane.showMessageDialog(this, 
                "Ảnh đã được chọn! Nhấn 'Xác nhận' để lưu thay đổi.");
        }
    }
    
    private String saveImageToFolder(File imageFile) {
        try {
            // Tạo thư mục lưu ảnh nếu chưa có
            String uploadDir = "uploads/invoices/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Tạo tên file unique (dùng timestamp + userId)
            String extension = imageFile.getName().substring(
                imageFile.getName().lastIndexOf("."));
            String fileName = userId + "_" + System.currentTimeMillis() + extension;
            String targetPath = uploadDir + fileName;

            // Copy file
            java.nio.file.Files.copy(
                imageFile.toPath(), 
                new File(targetPath).toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );

            return targetPath;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi lưu ảnh: " + e.getMessage());
            return null;
        }
    }
    
    public void setPanelQuanLyNganSach(panelQuanLyNganSach panel) {
        this.panelNganSach = panel;
    }
    
    // Kiểm tra dữ liệu trước khi đẩy vào db 
    private boolean validateFields() {
        // Check số tiền
        String soTienStr = txtSoTien.getText().trim();
        if (soTienStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền!");
            txtSoTien.requestFocus();
            return false;
        }

        try {
            java.math.BigDecimal soTien = new java.math.BigDecimal(soTienStr);
            if (soTien.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0!");
                txtSoTien.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!");
            txtSoTien.requestFocus();
            return false;
        }

        // Check phương thức
        if (txtPhuongThuc.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập phương thức!");
            txtPhuongThuc.requestFocus();
            return false;
        }

        return true;
    }
    
    // Mở tab thêm mới giao dịch 
    public void openAddTransactionTab() {
        for (int i = 0; i < tabMain.getTabCount(); i++) {
            if (tabMain.getTitleAt(i).equals("Thêm giao dịch")) {
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
        tabMain.addTab("Thêm giao dịch", pnlQLGD);
        tabMain.setSelectedComponent(pnlQLGD);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelThongTin = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        btnThemMoi = new javax.swing.JButton();
        btnCapNhat = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnHuyBo = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        lblAvatar = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        txtTenDM = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        lblTenDM = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        txtSoTien = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        lblLoaiDM = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        lblDMCha = new javax.swing.JLabel();
        txtLoaiGD = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        lblViTri = new javax.swing.JLabel();
        jspNgayGiaoDich = new javax.swing.JSpinner();
        jPanel8 = new javax.swing.JPanel();
        txtPhuongThuc = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        lblCapDo = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        txtGhiChu = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        lblTrangThai = new javax.swing.JLabel();
        lblSearch = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblTongSo = new javax.swing.JLabel();
        lblSoLuong = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        panelGiaoDich = new javax.swing.JPanel();
        jcbLoaiGiaoDich = new javax.swing.JComboBox<>();
        jcbDanhMuc = new javax.swing.JComboBox<>();
        jcbPhuongThuc = new javax.swing.JComboBox<>();
        lblNgayBatDau = new javax.swing.JTextField();
        lblTime1 = new javax.swing.JLabel();
        lblTime2 = new javax.swing.JLabel();
        lblNgayKetThuc = new javax.swing.JTextField();
        lblReset = new javax.swing.JLabel();
        lblLoc = new javax.swing.JLabel();
        lblDanhMuc = new javax.swing.JLabel();
        lblLGD = new javax.swing.JLabel();
        lblPhuongThuc = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        panelThongTin.setBackground(new java.awt.Color(255, 255, 255));

        txtSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnThemMoi.setBackground(new java.awt.Color(40, 167, 69));
        btnThemMoi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnThemMoi.setForeground(new java.awt.Color(255, 255, 255));
        btnThemMoi.setText("Thêm mới");
        btnThemMoi.setPreferredSize(new java.awt.Dimension(88, 33));
        btnThemMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemMoiActionPerformed(evt);
            }
        });

        btnCapNhat.setBackground(new java.awt.Color(0, 123, 255));
        btnCapNhat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCapNhat.setForeground(new java.awt.Color(255, 255, 255));
        btnCapNhat.setText("Cập nhật ");
        btnCapNhat.setPreferredSize(new java.awt.Dimension(88, 33));

        btnXoa.setBackground(new java.awt.Color(220, 53, 69));
        btnXoa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnXoa.setForeground(new java.awt.Color(255, 255, 255));
        btnXoa.setText("Xóa");
        btnXoa.setPreferredSize(new java.awt.Dimension(88, 33));

        btnHuyBo.setBackground(new java.awt.Color(108, 117, 125));
        btnHuyBo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnHuyBo.setForeground(new java.awt.Color(255, 255, 255));
        btnHuyBo.setText("Hủy bỏ");
        btnHuyBo.setPreferredSize(new java.awt.Dimension(88, 33));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Thông tin chi tiết giao dịch");

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setPreferredSize(new java.awt.Dimension(310, 45));

        txtTenDM.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtTenDM.setBorder(null);

        jLabel4.setBackground(new java.awt.Color(245, 245, 245));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Tên danh mục ");

        lblTenDM.setBackground(new java.awt.Color(245, 245, 245));
        lblTenDM.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTenDM.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTenDM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTenDM, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTenDM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtTenDM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)))
                .addGap(15, 15, 15))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setPreferredSize(new java.awt.Dimension(310, 45));

        txtSoTien.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtSoTien.setBorder(null);

        jLabel5.setBackground(new java.awt.Color(245, 245, 245));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Số tiền");

        lblLoaiDM.setBackground(new java.awt.Color(245, 245, 245));
        lblLoaiDM.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblLoaiDM.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblLoaiDM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtSoTien, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLoaiDM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtSoTien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)))
                .addGap(15, 15, 15))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setPreferredSize(new java.awt.Dimension(310, 45));

        jLabel6.setBackground(new java.awt.Color(245, 245, 245));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Loai giao dịch ");

        lblDMCha.setBackground(new java.awt.Color(245, 245, 245));
        lblDMCha.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDMCha.setPreferredSize(new java.awt.Dimension(20, 20));

        txtLoaiGD.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtLoaiGD.setBorder(null);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDMCha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLoaiGD, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDMCha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(txtLoaiGD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setPreferredSize(new java.awt.Dimension(310, 45));

        jLabel7.setBackground(new java.awt.Color(245, 245, 245));
        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Ngày giao dịch");

        lblViTri.setBackground(new java.awt.Color(245, 245, 245));
        lblViTri.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblViTri.setPreferredSize(new java.awt.Dimension(20, 20));

        jspNgayGiaoDich.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jspNgayGiaoDich.setModel(new javax.swing.SpinnerDateModel());
        jspNgayGiaoDich.setEditor(new javax.swing.JSpinner.DateEditor(jspNgayGiaoDich, "dd/MM/yyyy"));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblViTri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jspNgayGiaoDich, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(0, 14, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblViTri, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(jspNgayGiaoDich, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setPreferredSize(new java.awt.Dimension(310, 45));

        txtPhuongThuc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtPhuongThuc.setBorder(null);

        jLabel8.setBackground(new java.awt.Color(245, 245, 245));
        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Phương thức giao dịch");

        lblCapDo.setBackground(new java.awt.Color(245, 245, 245));
        lblCapDo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblCapDo.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCapDo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtPhuongThuc, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCapDo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPhuongThuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)))
                .addGap(15, 15, 15))
        );

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setPreferredSize(new java.awt.Dimension(310, 45));

        txtGhiChu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtGhiChu.setBorder(null);

        jLabel11.setBackground(new java.awt.Color(245, 245, 245));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Ghi chú");

        lblTrangThai.setBackground(new java.awt.Color(245, 245, 245));
        lblTrangThai.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTrangThai.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtGhiChu, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTrangThai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtGhiChu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11)))
                .addGap(15, 15, 15))
        );

        lblSearch.setBackground(new java.awt.Color(245, 245, 245));
        lblSearch.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSearch.setPreferredSize(new java.awt.Dimension(20, 20));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Tổng số giao dịch: ");

        lblTongSo.setBackground(new java.awt.Color(245, 245, 245));
        lblTongSo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTongSo.setPreferredSize(new java.awt.Dimension(20, 20));

        lblSoLuong.setBackground(new java.awt.Color(245, 245, 245));
        lblSoLuong.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSoLuong.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout panelThongTinLayout = new javax.swing.GroupLayout(panelThongTin);
        panelThongTin.setLayout(panelThongTinLayout);
        panelThongTinLayout.setHorizontalGroup(
            panelThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThongTinLayout.createSequentialGroup()
                .addGroup(panelThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelThongTinLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(lblSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSearch))
                    .addGroup(panelThongTinLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(lblAvatar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                                .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                                .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                                .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                                .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                                .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE))
                            .addGroup(panelThongTinLayout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(lblTongSo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(panelThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelThongTinLayout.createSequentialGroup()
                                        .addComponent(btnThemMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnHuyBo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panelThongTinLayout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 29, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelThongTinLayout.setVerticalGroup(
            panelThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThongTinLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAvatar, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelThongTinLayout.createSequentialGroup()
                        .addGroup(panelThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(lblTongSo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addGroup(panelThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnThemMoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnHuyBo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelThongTinLayout.createSequentialGroup()
                        .addComponent(lblSoLuong, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelGiaoDichLayout = new javax.swing.GroupLayout(panelGiaoDich);
        panelGiaoDich.setLayout(panelGiaoDichLayout);
        panelGiaoDichLayout.setHorizontalGroup(
            panelGiaoDichLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 636, Short.MAX_VALUE)
        );
        panelGiaoDichLayout.setVerticalGroup(
            panelGiaoDichLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 588, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(panelGiaoDich);

        jcbLoaiGiaoDich.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jcbDanhMuc.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jcbPhuongThuc.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblNgayBatDau.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNgayBatDau.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblNgayBatDau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lblNgayBatDauActionPerformed(evt);
            }
        });

        lblTime1.setBackground(new java.awt.Color(245, 245, 245));
        lblTime1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTime1.setPreferredSize(new java.awt.Dimension(20, 20));

        lblTime2.setBackground(new java.awt.Color(245, 245, 245));
        lblTime2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTime2.setPreferredSize(new java.awt.Dimension(20, 20));

        lblNgayKetThuc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNgayKetThuc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblNgayKetThuc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lblNgayKetThucActionPerformed(evt);
            }
        });

        lblReset.setBackground(new java.awt.Color(245, 245, 245));
        lblReset.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblReset.setPreferredSize(new java.awt.Dimension(20, 20));

        lblLoc.setBackground(new java.awt.Color(245, 245, 245));
        lblLoc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblLoc.setPreferredSize(new java.awt.Dimension(20, 20));

        lblDanhMuc.setBackground(new java.awt.Color(245, 245, 245));
        lblDanhMuc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDanhMuc.setPreferredSize(new java.awt.Dimension(20, 20));

        lblLGD.setBackground(new java.awt.Color(245, 245, 245));
        lblLGD.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblLGD.setPreferredSize(new java.awt.Dimension(20, 20));

        lblPhuongThuc.setBackground(new java.awt.Color(245, 245, 245));
        lblPhuongThuc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblPhuongThuc.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelThongTin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblLGD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbLoaiGiaoDich, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblDanhMuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbDanhMuc, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblPhuongThuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbPhuongThuc, 0, 162, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(lblTime1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNgayBatDau, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblTime2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNgayKetThuc, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblReset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jcbDanhMuc)
                    .addComponent(jcbPhuongThuc)
                    .addComponent(lblLGD, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblPhuongThuc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jcbLoaiGiaoDich, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNgayBatDau)
                    .addComponent(lblTime1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTime2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblNgayKetThuc, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblReset, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblLoc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDanhMuc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(panelThongTin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void lblNgayBatDauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lblNgayBatDauActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lblNgayBatDauActionPerformed

    private void lblNgayKetThucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lblNgayKetThucActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lblNgayKetThucActionPerformed

    private void btnThemMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemMoiActionPerformed
        openAddTransactionTab();
    }//GEN-LAST:event_btnThemMoiActionPerformed

    private void setupLabelIcons() {
        resizeLabelIcon(lblTenDM, "/resources/namecategory.png");
        resizeLabelIcon(lblSearch, "/resources/search.png");
        resizeLabelIcon(lblLoaiDM, "/resources/classify.png");
        resizeLabelIcon(lblDMCha, "/resources/originalcatalog.png");
        resizeLabelIcon(lblViTri, "/resources/priorit.png");
        resizeLabelIcon(lblCapDo, "/resources/adjusting.png");
        resizeLabelIcon(lblTrangThai, "/resources/status.png");
        resizeLabelIcon(lblReset, "/resources/rotate.png");
        resizeLabelIcon(lblLoc, "/resources/setting.png");
        resizeLabelIcon(lblTime1, "/resources/schedule.png");
        resizeLabelIcon(lblTime2, "/resources/schedule.png");
        resizeLabelIcon(lblLGD, "/resources/market.png");
        resizeLabelIcon(lblDanhMuc, "/resources/categories.png");
        resizeLabelIcon(lblPhuongThuc, "/resources/atm.png");
        resizeLabelIcon(lblTongSo, "/resources/gross.png");
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
    private javax.swing.JButton btnHuyBo;
    private javax.swing.JButton btnThemMoi;
    private javax.swing.JButton btnXoa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> jcbDanhMuc;
    private javax.swing.JComboBox<String> jcbLoaiGiaoDich;
    private javax.swing.JComboBox<String> jcbPhuongThuc;
    private javax.swing.JSpinner jspNgayGiaoDich;
    private javax.swing.JLabel lblAvatar;
    private javax.swing.JLabel lblCapDo;
    private javax.swing.JLabel lblDMCha;
    private javax.swing.JLabel lblDanhMuc;
    private javax.swing.JLabel lblLGD;
    private javax.swing.JLabel lblLoaiDM;
    private javax.swing.JLabel lblLoc;
    private javax.swing.JTextField lblNgayBatDau;
    private javax.swing.JTextField lblNgayKetThuc;
    private javax.swing.JLabel lblPhuongThuc;
    private javax.swing.JLabel lblReset;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblSoLuong;
    private javax.swing.JLabel lblTenDM;
    private javax.swing.JLabel lblTime1;
    private javax.swing.JLabel lblTime2;
    private javax.swing.JLabel lblTongSo;
    private javax.swing.JLabel lblTrangThai;
    private javax.swing.JLabel lblViTri;
    private javax.swing.JPanel panelGiaoDich;
    private javax.swing.JPanel panelThongTin;
    private javax.swing.JTextField txtGhiChu;
    private javax.swing.JTextField txtLoaiGD;
    private javax.swing.JTextField txtPhuongThuc;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSoTien;
    private javax.swing.JTextField txtTenDM;
    // End of variables declaration//GEN-END:variables
}
