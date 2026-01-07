/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI;

import Model.Budget;
import Model.Category;
import Model.Transaction;
import Model.User;
import Service.CategoryService;
import Service.TransactionService;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import Service.EmailService;
import Model.UserSession;
import Service.BudgetService;

/**
 *
 * @author Admin
 */
public class panelThemGiaoDich extends javax.swing.JPanel {
    
    private Category selectedCategory = null;
    private BigDecimal soTienHienTai = null; 
    private int userId;
    private MoneyDisplayPanel moneyPanel;
    private File selectedImageFile = null;
    private static final String UPLOAD_DIR = "uploads/invoices/";
    private static final long MAX_FILE_SIZE_MB = 5;
    private static final int MAX_NOTE_LENGTH = 500;
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("999999999999");
    private static final BigDecimal MIN_AMOUNT = new BigDecimal("1000");
    
    // Constructor chính 
    public panelThemGiaoDich(int userId) {
        this.userId = userId;
        initComponents();
        
        SwingUtilities.invokeLater(() -> setupLabelIcons());
        
        // Tạo và thêm MoneyDisplayPanel
        moneyPanel = new MoneyDisplayPanel();
        panelMoney.setLayout(new BorderLayout());
        panelMoney.add(moneyPanel, BorderLayout.CENTER);
        
        loadCategory();
        setupButtonListeners();
        setupFieldStates();
        setupAvatarClick();
        
        // Khóa các trường không cho nhập tay
        txtTenDM1.setEnabled(false);
        txtLoaiGD.setEnabled(false);
        
        // Setup cursor cho các field có thể click
        txtPhuongThuc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        txtPhuongThuc.setEnabled(false); // Không cho nhập tay
        
        // Thêm listener cho txtPhuongThuc
        txtPhuongThuc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectPaymentMethod();
            }
        });
        
        moneyPanel.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        moneyPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inputTransactionAmount();
            }
        });
        
        // Set hints
        HintUtils.setHint(txtTenDM1, "Chọn danh mục từ danh sách...");
        HintUtils.setHint(txtLoaiGD, "Tự động theo danh mục...");
        HintUtils.setHint(txtPhuongThuc, "Click để chọn phương thức...");
        HintUtils.setHint(txtGhiChu, "Nhập ghi chú cho giao dịch...");
    }
    
    // Gắn action listeners cho các button 
    private void setupButtonListeners() {
        btnThemMoi.addActionListener(e -> createTransaction());
        btnHuyBo.addActionListener(e -> resetTransactionForm());
    }
    
    // Chuyển trạng thái cho các field 
    private void setupFieldStates() {
        // Các field không cho parse thủ công 
        txtTenDM1.setEnabled(false);  
        txtLoaiGD.setEnabled(false);    
        txtPhuongThuc.setEnabled(false);

        // Các field cho phép nhập
        txtGhiChu.setEnabled(true);     
        jspNgayGiaoDich.setEnabled(true);
    }
    
    // Thiết lập click handler cho lblAvatar
    private void setupAvatarClick(){
        lblAvatar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblAvatar.setToolTipText("Click để chọn ảnh hóa đơn");

        lblAvatar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                uploadInvoiceImage();
            }
        });
    }
    
    // Load và resize icons cho các label
    private void setupLabelIcons() {
        resizeLabelIcon(lblTenDM1, "/resources/namecategory.png");
        resizeLabelIcon(lblDMCha, "/resources/classify.png");
        resizeLabelIcon(lblViTri, "/resources/schedule.png");
        resizeLabelIcon(lblPhuongThuc, "/resources/atm.png");
        resizeLabelIcon(lblTrangThai, "/resources/status.png");
    }
    
    // Load danh mục từ db 
    private void loadCategory() {
        panelDanhMuc.removeAll();
        panelDanhMuc.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        ImageIcon iconchi = resizeIcon("/resources/spend.png", 20, 20);
        ImageIcon iconthu = resizeIcon("/resources/revenue.png", 20, 20);
        
        JPanel panelChi = createCategoryPanel("Chi");
        tabbedPane.addTab("Chi tiền", iconchi, panelChi);
        
        JPanel panelThu = createCategoryPanel("Thu");
        tabbedPane.addTab("Thu tiền", iconthu, panelThu);
        
        panelDanhMuc.add(tabbedPane, BorderLayout.CENTER);
        panelDanhMuc.revalidate();
        panelDanhMuc.repaint();
    }
    
    // Tạo panel hiển thị categories theo loại (Chi/Thu)
    private JPanel createCategoryPanel(String loaiDanhMuc) {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(248, 249, 250));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        try {
            List<Category> allCategories = new CategoryService().getAllCategories();

            for (Category parent : allCategories) {
                if ((parent.getDanhMucChaId() == null || parent.getDanhMucChaId() == 0) 
                    && loaiDanhMuc.equalsIgnoreCase(parent.getLoaiDanhMuc())) {

                    // Panel cha
                    CategoryParentPanel parentPanel = new CategoryParentPanel(parent);
                    parentPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            // Không cho chọn danh mục cha
                            JOptionPane.showMessageDialog(
                                panelThemGiaoDich.this,
                                "Vui lòng chọn danh mục con!",
                                "Thông báo",
                                JOptionPane.WARNING_MESSAGE
                            );
                        }
                    });
                    contentPanel.add(parentPanel);

                    // Panel con
                    JPanel childContainer = new JPanel(new BorderLayout());
                    childContainer.setBackground(Color.WHITE);
                    childContainer.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                    ));

                    JPanel childPanel = new JPanel(new GridLayout(0, 4, 15, 15));
                    childPanel.setBackground(Color.WHITE);

                    for (Category child : allCategories) {
                        if (child.getDanhMucChaId() != null && child.getDanhMucChaId().equals(parent.getId())) {
                            CategoryItemPanel childItem = new CategoryItemPanel(child);
                            childItem.addMouseListener(new java.awt.event.MouseAdapter() {
                                public void mouseClicked(java.awt.event.MouseEvent evt) {
                                    selectCategory(child);
                                }
                                public void mouseEntered(java.awt.event.MouseEvent evt) {
                                    childItem.setBackground(new Color(240, 248, 255));
                                }
                                public void mouseExited(java.awt.event.MouseEvent evt) {
                                    childItem.setBackground(Color.WHITE);
                                }
                            });
                            childPanel.add(childItem);
                        }
                    }

                    if (childPanel.getComponentCount() > 0) {
                        childContainer.add(childPanel, BorderLayout.CENTER);
                        contentPanel.add(childContainer);
                        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scrollPane);
        return wrapper;
    }
    
    // Chọn danh mục
    private void selectCategory(Category category) {
        this.selectedCategory = category;

        // Chỉ hiển thị tên danh mục
        txtTenDM1.setText(category.getTenDanhMuc());

        // Tự động set loại giao dịch dựa vào loại danh mục
        String loaiGiaoDich = category.getLoaiDanhMuc(); // "Chi" hoặc "Thu"
        txtLoaiGD.setText(loaiGiaoDich);

        // Cập nhật MoneyPanel với màu sắc phù hợp (nếu đã nhập số tiền)
        if (moneyPanel != null) {
            // Nếu chưa nhập số tiền thì chỉ đổi màu border
            moneyPanel.updateAmount(BigDecimal.ZERO, loaiGiaoDich);
        }

        JOptionPane.showMessageDialog(this, 
            "Đã chọn danh mục: " + category.getTenDanhMuc() + "\nLoại: " + loaiGiaoDich,
            "Thông báo",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Chọn phương thức giao dịch
    private void selectPaymentMethod() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JRadioButton rbTienMat = new JRadioButton("Tiền mặt");
        JRadioButton rbChuyenKhoan = new JRadioButton("Chuyển khoản");

        // Preselect nếu đã có giá trị
        if ("Tiền mặt".equals(txtPhuongThuc.getText())) {
            rbTienMat.setSelected(true);
        } else if ("Chuyển khoản".equals(txtPhuongThuc.getText())) {
            rbChuyenKhoan.setSelected(true);
        }

        ButtonGroup group = new ButtonGroup();
        group.add(rbTienMat);
        group.add(rbChuyenKhoan);

        panel.add(rbTienMat);
        panel.add(rbChuyenKhoan);

        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Chọn phương thức giao dịch",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            if (rbTienMat.isSelected()) {
                txtPhuongThuc.setText("Tiền mặt");
            } else if (rbChuyenKhoan.isSelected()) {
                txtPhuongThuc.setText("Chuyển khoản");
            }
            // khi user không chọn gì
            else {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn phương thức!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    // Hiển thị dialog nhập số tiền 
    private void inputTransactionAmount() {
        if (selectedCategory == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn danh mục trước!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(
            this,
            "Nhập số tiền (VNĐ):",
            soTienHienTai != null ? soTienHienTai.toString() : ""
        );

        if (input == null) return;

        if (input.trim().isEmpty()) {
            soTienHienTai = null;
            moneyPanel.reset();
            return;
        }

        try {
            // Bỏ dấu phẩy, dấu chấm
            input = input.trim().replace(",", "").replace(".", "");
            BigDecimal soTien = new BigDecimal(input);

            // Stack: Tránh giá trị âm 
            if (soTien.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Số tiền phải lớn hơn 0!",
                    "Lỗi nhập liệu",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Stack: Tránh nhập số tiền quá lớn 
            if (soTien.compareTo(new BigDecimal("999999999999")) > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Số tiền quá lớn!\nTối đa: 999,999,999,999 VNĐ",
                    "Lỗi nhập liệu",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Stack: Kiểm tra số tiền tối thiểu => có thể thay đổi giá trị đầu vào 
            if (soTien.compareTo(new BigDecimal("1000")) < 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Số tiền nhỏ hơn 1,000 VNĐ.\nBạn có chắc chắn?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            soTienHienTai = soTien;
            String loaiGiaoDich = selectedCategory.getLoaiDanhMuc();
            moneyPanel.updateAmount(soTien, loaiGiaoDich);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Số tiền không hợp lệ!\nVui lòng chỉ nhập số.",
                "Lỗi nhập liệu",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //Kiểm tra ngân sách trước khi thêm giao dịch
    private boolean checkBudgetBeforeAdd() {
        // Chỉ check với giao dịch CHI
        String loaiGiaoDich = selectedCategory.getLoaiDanhMuc();
        if (!"Chi".equalsIgnoreCase(loaiGiaoDich)) {
            return true; // Thu không cần check budget
        }

        try {
            // Lấy thông tin ngân sách hiện tại của danh mục
            BudgetService budgetService = new BudgetService();
            Budget currentBudget = budgetService.getBudgetByUserAndCategory(userId, selectedCategory.getId());

            if (currentBudget == null) {
                // Chưa có ngân sách cho danh mục này
                int choice = JOptionPane.showConfirmDialog(this,
                    "Danh mục \"" + selectedCategory.getTenDanhMuc() + "\" chưa có ngân sách!\n" +
                    "Bạn có muốn tiếp tục thêm giao dịch không?",
                    "Cảnh báo",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

                return choice == JOptionPane.YES_OPTION;
            }

            // Tính toán ngân sách sau khi thêm giao dịch mới
            BigDecimal tongNganSach = currentBudget.getTongNganSach();
            BigDecimal daDung = currentBudget.getDaDung();
            BigDecimal soTienMoi = soTienHienTai; // Số tiền giao dịch mới

            BigDecimal daDungSauKhiThem = daDung.add(soTienMoi);
            BigDecimal conLaiSauKhiThem = tongNganSach.subtract(daDungSauKhiThem);

            // Tính phần trăm sử dụng
            double phanTramSuDung = 0;
            if (tongNganSach.compareTo(BigDecimal.ZERO) > 0) {
                phanTramSuDung = daDungSauKhiThem.divide(tongNganSach, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100")).doubleValue();
            }

            // CASE 1: Vượt ngân sách (>100%)
            if (conLaiSauKhiThem.compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal soTienVuot = conLaiSauKhiThem.abs();

                String message = String.format(
                    "CẢNH BÁO: VƯỢT NGÂN SÁCH!\n\n" +
                    "Danh mục: %s\n" +
                    "================================\n" +
                    "Ngân sách tổng:        %,d VNĐ\n" +
                    "Đã sử dụng:            %,d VNĐ\n" +
                    "Số tiền giao dịch mới: %,d VNĐ\n" +
                    "=================================\n" +
                    "Tổng sẽ chi:           %,d VNĐ (%.1f%%)\n" +
                    "Vượt quá:              %,d VNĐ\n" +
                    "Còn lại sau giao dịch: %,d VNĐ\n\n" +
                    "Bạn đã vượt %.1f%% ngân sách!\n\n" +
                    "Bạn có chắc chắn muốn tiếp tục?",
                    selectedCategory.getTenDanhMuc(),
                    tongNganSach.longValue(),
                    daDung.longValue(),
                    soTienMoi.longValue(),
                    daDungSauKhiThem.longValue(),
                    phanTramSuDung,
                    soTienVuot.longValue(),
                    conLaiSauKhiThem.longValue(),
                    phanTramSuDung - 100
                );

                int choice = JOptionPane.showConfirmDialog(this,
                    message,
                    "Cảnh báo Vượt Ngân Sách",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE);

                if (choice != JOptionPane.YES_OPTION) {
                    System.out.println("[WARNING] User từ chối thêm giao dịch do vượt ngân sách");
                    return false;
                }

                System.out.println("[WARNING] User xác nhận thêm giao dịch dù vượt ngân sách: " + soTienVuot);
            }
            // CASE 2: Cảnh báo khi sử dụng >80% ngân sách
            else if (phanTramSuDung >= 80 && phanTramSuDung < 100) {
                String message = String.format(
                    "CẢNH BÁO: SẮP HẾT NGÂN SÁCH!\n\n" +
                    "Danh mục: %s\n" +
                    "================================\n" +
                    "Ngân sách tổng:        %,d VNĐ\n" +
                    "Đã sử dụng:            %,d VNĐ\n" +
                    "Số tiền giao dịch mới: %,d VNĐ\n" +
                    "=================================\n" +
                    "Tổng sẽ chi:           %,d VNĐ (%.1f%%)\n" +
                    "Còn lại sau giao dịch: %,d VNĐ\n\n" +
                    "Bạn đã sử dụng %.1f%% ngân sách!\n\n" +
                    "Bạn có muốn tiếp tục?",
                    selectedCategory.getTenDanhMuc(),
                    tongNganSach.longValue(),
                    daDung.longValue(),
                    soTienMoi.longValue(),
                    daDungSauKhiThem.longValue(),
                    phanTramSuDung,
                    conLaiSauKhiThem.longValue(),
                    phanTramSuDung
                );

                int choice = JOptionPane.showConfirmDialog(this,
                    message,
                    "Cảnh báo Ngân Sách",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

                if (choice != JOptionPane.YES_OPTION) {
                    return false;
                }
            }
            // CASE 3: Thông báo info khi sử dụng 50-80%
            else if (phanTramSuDung >= 50 && phanTramSuDung < 80) {
                String message = String.format(
                    "THÔNG BÁO NGÂN SÁCH\n\n" +
                    "Danh mục: %s\n" +
                    "Đã sử dụng: %.1f%% ngân sách\n" +
                    "Còn lại: %,d VNĐ",
                    selectedCategory.getTenDanhMuc(),
                    phanTramSuDung,
                    conLaiSauKhiThem.longValue()
                );

                // Chỉ thông báo, không cần xác nhận
                JOptionPane.showMessageDialog(this,
                    message,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            }

            return true;

        } catch (Exception e) {
            System.err.println("[ERROR] Lỗi khi kiểm tra ngân sách: " + e.getMessage());
            e.printStackTrace();

            // Nếu lỗi, hỏi user có muốn tiếp tục không
            int choice = JOptionPane.showConfirmDialog(this,
                "Không thể kiểm tra ngân sách!\n" +
                "Lỗi: " + e.getMessage() + "\n\n" +
                "Bạn có muốn tiếp tục thêm giao dịch không?",
                "Lỗi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE);

            return choice == JOptionPane.YES_OPTION;
        }
    }

    // Thêm mới giao dịch
    private void createTransaction() {
        try {
            // Validate danh mục
            if (selectedCategory == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục!");
                return;
            }

            // Validate số tiền
            if (soTienHienTai == null || soTienHienTai.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập số tiền!\n(Click vào panel số tiền để nhập)");
                return;
            }

            // Validate ghi chú
            String ghiChu = txtGhiChu.getText().trim();
            if (ghiChu.length() > 500) {
                JOptionPane.showMessageDialog(this,
                    "Ghi chú quá dài!\nTối đa 500 ký tự.",
                    "Lỗi nhập liệu",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate ngày giao dịch
            Date selectedDate = (Date) jspNgayGiaoDich.getValue();
            LocalDate selectedLocalDate = selectedDate.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate today = LocalDate.now();

            if (selectedLocalDate.isAfter(today)) {
                int confirmFuture = JOptionPane.showConfirmDialog(this,
                    "Ngày giao dịch là ngày tương lai!\n" +
                    "Ngày chọn: " + selectedLocalDate + "\n" +
                    "Hôm nay: " + today + "\n\n" +
                    "Bạn có chắc chắn muốn tiếp tục?",
                    "Cảnh báo",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

                if (confirmFuture != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Validate phương thức
            if (txtPhuongThuc.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phương thức giao dịch!");
                return;
            }
            
            // Kiểm tra ngân sách trước khi xác nhận 
            if (!checkBudgetBeforeAdd()) {
                System.out.println("[INFO] User hủy bỏ do vượt ngân sách hoặc không muốn tiếp tục");
                return;
            }

            // Xác nhận
            String loaiGiaoDich = selectedCategory.getLoaiDanhMuc();
            int confirm = JOptionPane.showConfirmDialog(this,
                buildConfirmationMessage(loaiGiaoDich),
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Tạo giao dịch
            Transaction trans = buildTransaction(loaiGiaoDich);

            if (selectedImageFile != null) {
                try {
                    String savedPath = saveImageToFolder(selectedImageFile);
                    if (savedPath != null) {
                        trans.setAnhHoaDon(savedPath);
                    } else {
                        int continueChoice = JOptionPane.showConfirmDialog(this,
                            "Không thể lưu ảnh hóa đơn!\nBạn có muốn tiếp tục thêm giao dịch không?",
                            "Cảnh báo",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                        );

                        if (continueChoice != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
                } catch (Exception imageEx) {
                    System.err.println("Lỗi khi lưu ảnh: " + imageEx.getMessage());
                    imageEx.printStackTrace();

                    int continueChoice = JOptionPane.showConfirmDialog(this,
                        "Lỗi khi lưu ảnh hóa đơn!\nBạn có muốn tiếp tục không?",
                        "Cảnh báo",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );

                    if (continueChoice != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
            }

            boolean success = false;
            try {
                success = new TransactionService().create(trans);
            } catch (Exception dbEx) {
                System.err.println("Lỗi database: " + dbEx.getMessage());
                dbEx.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi lưu giao dịch vào cơ sở dữ liệu!\n" + dbEx.getMessage(),
                    "Lỗi Database",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (success) {
                // Catch cho email 
                handleEmailNotification(trans);
                resetTransactionForm();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Thêm giao dịch thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            // Catch lỗi chung 
            System.err.println("Lỗi không xác định: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi hệ thống: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Gửi thông báo xác nhận trước khi thêm giao dịch => createTransaction()
    private String buildConfirmationMessage(String loaiGiaoDich) {
        StringBuilder msg = new StringBuilder();
        msg.append("Xác nhận thêm giao dịch:\n");
        msg.append("Danh mục: ").append(selectedCategory.getTenDanhMuc()).append("\n");
        msg.append("Số tiền: ").append(formatMoney(soTienHienTai)).append(" đ\n");
        msg.append("Loại: ").append(loaiGiaoDich).append("\n");
        msg.append("Phương thức: ").append(txtPhuongThuc.getText()).append("\n");
        msg.append("Ghi chú: ").append(txtGhiChu.getText().isEmpty() ? "(Không có)" : txtGhiChu.getText()).append("\n");
        msg.append("Có ảnh hóa đơn: ").append(selectedImageFile != null ? "Có" : "Không");
        return msg.toString();
    }

    // Tạo đối tượng Transaction từ dữ liệu trên form
    private Transaction buildTransaction(String loaiGiaoDich) {
        Transaction trans = new Transaction();
        trans.setNguoiDungId(userId);
        trans.setDanhMucId(selectedCategory.getId());
        trans.setSoTien(soTienHienTai);
        trans.setLoaiGiaoDich(
            "Chi".equalsIgnoreCase(loaiGiaoDich) ? 
            Transaction.LoaiGiaoDich.CHI : Transaction.LoaiGiaoDich.THU
        );

        Date selectedDate = (Date) jspNgayGiaoDich.getValue();
        LocalDate localDate = selectedDate.toInstant()
            .atZone(ZoneId.systemDefault()).toLocalDate();
        trans.setNgayGiaoDich(localDate);

        trans.setPhuongThuc(txtPhuongThuc.getText().trim());
        trans.setGhiChu(txtGhiChu.getText().trim());

        return trans;
    }

    // Gửi email thông báo sau khi thêm giao dịch
    private void handleEmailNotification(Transaction trans) {
        try {
            User currentUser = UserSession.getCurrentUser();
            if (currentUser != null && currentUser.getEmail() != null) {
                EmailService emailService = new EmailService();
                boolean emailSent = emailService.sendTransactionNotification(currentUser, trans);

                if (emailSent) {
                    JOptionPane.showMessageDialog(this, 
                        "Thêm giao dịch thành công!\n" +
                        "Email thông báo đã được gửi đến: " + currentUser.getEmail() +
                        (selectedImageFile != null ? "\nẢnh hóa đơn đã được lưu." : ""),
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Thêm giao dịch thành công!\n" +
                        "Không thể gửi email thông báo (đã lưu vào hệ thống)" +
                        (selectedImageFile != null ? "\nẢnh hóa đơn đã được lưu." : ""),
                        "Thành công",
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception emailEx) {
            System.err.println("Lỗi khi gửi email: " + emailEx.getMessage());
            emailEx.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Thêm giao dịch thành công!\n" +
                "Lỗi khi gửi email: " + emailEx.getMessage(),
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0";
        return String.format("%,d", amount.longValue());
    }
    
    // Hủy bỏ thao tác 
    private void resetTransactionForm() {
        selectedCategory = null;
        selectedImageFile = null;
        soTienHienTai = null;
        txtTenDM1.setText("");
        txtLoaiGD.setText("");
        txtPhuongThuc.setText("");
        txtGhiChu.setText("");
        // reset ngày về hiện tại 
        jspNgayGiaoDich.setValue(new Date());
        lblAvatar.setIcon(null);
        lblAvatar.setText("Ảnh");
        if (moneyPanel != null) {
            moneyPanel.reset();
        }
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
                int width = label.getWidth();
                int height = label.getHeight();
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
    
    private ImageIcon resizeIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
    
    // Upload ảnh hóa đơn
    private void uploadInvoiceImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn ảnh hóa đơn");

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Image Files (*.jpg, *.jpeg, *.png)", "jpg", "jpeg", "png");
        fileChooser.setFileFilter(filter);

        File uploadsDir = new File("uploads/invoices/");
        if (uploadsDir.exists()) {
            fileChooser.setCurrentDirectory(uploadsDir);
        }

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();

            // Validate extension
            if (!isValidImageExtension(selectedImageFile)) {
                JOptionPane.showMessageDialog(this,
                    "File không hợp lệ!\nChỉ chấp nhận: .jpg, .jpeg, .png",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                selectedImageFile = null;
                return;
            }

            // Validate MIME type (kiểm tra thực sự là ảnh)
            if (!isActuallyImage(selectedImageFile)) {
                JOptionPane.showMessageDialog(this,
                    "File không phải là ảnh hợp lệ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                selectedImageFile = null;
                return;
            }

            // Kiểm tra kích thước file
            long fileSizeInMB = selectedImageFile.length() / (1024 * 1024);
            if (fileSizeInMB > 5) {
                JOptionPane.showMessageDialog(this,
                    "File ảnh quá lớn! Vui lòng chọn file nhỏ hơn 5MB.",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
                selectedImageFile = null;
                return;
            }

            displayInvoiceImage(selectedImageFile.getAbsolutePath());

            JOptionPane.showMessageDialog(this, 
                "Ảnh đã được chọn! Nhấn 'Thêm mới' để lưu giao dịch.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // KIỂM TRA EXTENSION
    private boolean isValidImageExtension(File file) {
        if (file == null) return false;
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg") || 
               fileName.endsWith(".jpeg") || 
               fileName.endsWith(".png");
    }

    // KIỂM TRA THỰC SỰ LÀ ẢNH (đọc header)
    private boolean isActuallyImage(File file) {
        try {
            // Đọc 12 bytes đầu để check magic number
            java.io.FileInputStream fis = new java.io.FileInputStream(file);
            byte[] header = new byte[12];
            int bytesRead = fis.read(header);
            fis.close();

            if (bytesRead < 2) return false;

            // Check JPEG (FF D8 FF)
            if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8) {
                return true;
            }

            // Check PNG (89 50 4E 47)
            if (header[0] == (byte) 0x89 && header[1] == (byte) 0x50 &&
                header[2] == (byte) 0x4E && header[3] == (byte) 0x47) {
                return true;
            }

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hiển thị ảnh lên lblAvatar
    private void displayInvoiceImage(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                lblAvatar.setIcon(null);
                lblAvatar.setText("Ảnh");
                lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
                return;
            }

            ImageIcon icon;
            File imageFile = new File(imagePath);

            if (imageFile.exists()) {
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
            int width = lblAvatar.getWidth() > 0 ? lblAvatar.getWidth() : 100;
            int height = lblAvatar.getHeight() > 0 ? lblAvatar.getHeight() : 100;

            Image scaledImg = icon.getImage().getScaledInstance(
                width, height, Image.SCALE_SMOOTH);
            lblAvatar.setIcon(new ImageIcon(scaledImg));
            lblAvatar.setText("");

        } catch (Exception e) {
            e.printStackTrace();
            lblAvatar.setIcon(null);
            lblAvatar.setText("Lỗi hiển thị");
        }
    }

    // Lưu ảnh vào thư mục server
    private String saveImageToFolder(File imageFile) {
        try {
            String uploadDir = "uploads/invoices/";
            File directory = new File(uploadDir);

            // Tạo thư mục và kiểm tra quyền
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    throw new Exception("Không thể tạo thư mục lưu trữ!");
                }
            }

            // Kiểm tra quyền ghi
            if (!directory.canWrite()) {
                throw new Exception("Không có quyền ghi vào thư mục: " + uploadDir);
            }

            String extension = imageFile.getName().substring(
                imageFile.getName().lastIndexOf("."));
            String fileName = userId + "_" + System.currentTimeMillis() + extension;
            String targetPath = uploadDir + fileName;

            // Kiểm tra file đã tồn tại
            File targetFile = new File(targetPath);
            if (targetFile.exists()) {
                throw new Exception("File đã tồn tại: " + fileName);
            }

            // Copy file
            java.nio.file.Files.copy(
                imageFile.toPath(), 
                targetFile.toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );

            // Verify file đã được lưu thành công
            if (!targetFile.exists() || targetFile.length() == 0) {
                throw new Exception("Lưu file thất bại!");
            }

            return targetPath;

        } catch (Exception e) {
            System.err.println("Lỗi khi lưu ảnh: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi lưu ảnh: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return null;
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

        panelThongTin = new javax.swing.JPanel();
        btnThemMoi = new javax.swing.JButton();
        btnHuyBo = new javax.swing.JButton();
        lblAvatar = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        txtTenDM1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        lblTenDM1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        lblDMCha = new javax.swing.JLabel();
        txtLoaiGD = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        lblViTri = new javax.swing.JLabel();
        jspNgayGiaoDich = new javax.swing.JSpinner();
        jPanel8 = new javax.swing.JPanel();
        txtPhuongThuc = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        lblPhuongThuc = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        txtGhiChu = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        lblTrangThai = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        panelDanhMuc = new javax.swing.JPanel();
        panelMoney = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));

        panelThongTin.setBackground(new java.awt.Color(255, 255, 255));

        btnThemMoi.setBackground(new java.awt.Color(40, 167, 69));
        btnThemMoi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnThemMoi.setForeground(new java.awt.Color(255, 255, 255));
        btnThemMoi.setText("Thêm mới");
        btnThemMoi.setPreferredSize(new java.awt.Dimension(88, 33));

        btnHuyBo.setBackground(new java.awt.Color(108, 117, 125));
        btnHuyBo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnHuyBo.setForeground(new java.awt.Color(255, 255, 255));
        btnHuyBo.setText("Hủy bỏ");
        btnHuyBo.setPreferredSize(new java.awt.Dimension(88, 33));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setPreferredSize(new java.awt.Dimension(310, 45));

        txtTenDM1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtTenDM1.setBorder(null);

        jLabel5.setBackground(new java.awt.Color(245, 245, 245));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Tên danh mục ");

        lblTenDM1.setBackground(new java.awt.Color(245, 245, 245));
        lblTenDM1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTenDM1.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTenDM1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTenDM1, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTenDM1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtTenDM1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Thông tin giao dịch ");

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
                .addContainerGap(13, Short.MAX_VALUE))
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

        lblPhuongThuc.setBackground(new java.awt.Color(245, 245, 245));
        lblPhuongThuc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblPhuongThuc.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPhuongThuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtPhuongThuc, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPhuongThuc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        javax.swing.GroupLayout panelThongTinLayout = new javax.swing.GroupLayout(panelThongTin);
        panelThongTin.setLayout(panelThongTinLayout);
        panelThongTinLayout.setHorizontalGroup(
            panelThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThongTinLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                    .addGroup(panelThongTinLayout.createSequentialGroup()
                        .addGroup(panelThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelThongTinLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(panelThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelThongTinLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(92, 92, 92))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelThongTinLayout.createSequentialGroup()
                                .addComponent(btnThemMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(59, 59, 59)
                                .addComponent(btnHuyBo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50))))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelThongTinLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblAvatar, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(79, 79, 79))
        );
        panelThongTinLayout.setVerticalGroup(
            panelThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelThongTinLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblAvatar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnHuyBo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnThemMoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelDanhMucLayout = new javax.swing.GroupLayout(panelDanhMuc);
        panelDanhMuc.setLayout(panelDanhMucLayout);
        panelDanhMucLayout.setHorizontalGroup(
            panelDanhMucLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 638, Short.MAX_VALUE)
        );
        panelDanhMucLayout.setVerticalGroup(
            panelDanhMucLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 666, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(panelDanhMuc);

        javax.swing.GroupLayout panelMoneyLayout = new javax.swing.GroupLayout(panelMoney);
        panelMoney.setLayout(panelMoneyLayout);
        panelMoneyLayout.setHorizontalGroup(
            panelMoneyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelMoneyLayout.setVerticalGroup(
            panelMoneyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 77, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelThongTin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(panelMoney, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelMoney, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelThongTin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHuyBo;
    private javax.swing.JButton btnThemMoi;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jspNgayGiaoDich;
    private javax.swing.JLabel lblAvatar;
    private javax.swing.JLabel lblDMCha;
    private javax.swing.JLabel lblPhuongThuc;
    private javax.swing.JLabel lblTenDM1;
    private javax.swing.JLabel lblTrangThai;
    private javax.swing.JLabel lblViTri;
    private javax.swing.JPanel panelDanhMuc;
    private javax.swing.JPanel panelMoney;
    private javax.swing.JPanel panelThongTin;
    private javax.swing.JTextField txtGhiChu;
    private javax.swing.JTextField txtLoaiGD;
    private javax.swing.JTextField txtPhuongThuc;
    private javax.swing.JTextField txtTenDM1;
    // End of variables declaration//GEN-END:variables
}
