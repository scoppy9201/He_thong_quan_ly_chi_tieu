/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI;

import Model.Budget;
import Model.BudgetItemPanel;
import Model.Category;
import Model.CategoryItemPanel;
import Model.CategoryParentPanel;
import Service.BudgetService;
import Service.CategoryService;
import Utils.HintUtils;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Admin
 */
public class panelQuanLyNganSach extends javax.swing.JPanel {

    private int userId;
    private Category selectedCategory = null;
    private Category selectedParentCategory = null;
    private Budget selectedBudget = null;
    private BudgetService budgetService;
    private CategoryService categoryService;
    

    public panelQuanLyNganSach(int userId) {
        this.userId = userId;
        initComponents();
        setupSplitPane(); 
        budgetService = new BudgetService();
        categoryService = new CategoryService();

        setupUI();
        loadCategories();
        loadBudgets();
    }

    private void setupUI() {
        txtDanhMucCha.setEnabled(false);
        txtTongNganSach.setEnabled(false);
        txtTenDanhMuc.setEnabled(false);

        HintUtils.setHint(txtSoTien, "Nhập số tiền ngân sách...");

        btnDat.addActionListener(e -> datNganSach());
        btnHuy.addActionListener(e -> huyBo());

        lblNganSach.setText("Quản lý ngân sách chi tiêu");

        btnDat.setBackground(new Color(40, 167, 69));
        btnDat.setForeground(Color.WHITE);
        btnHuy.setBackground(new Color(108, 117, 125));
        btnHuy.setForeground(Color.WHITE);
    }
    
    private void setupSplitPane() {
        JScrollPane scrollLeft = new JScrollPane(jPanel1);
        scrollLeft.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollLeft.getVerticalScrollBar().setUnitIncrement(16);
        scrollLeft.setBorder(null);

        JPanel rightPanel = buildRightPanel(); // panel mới chứa jPanel2 + form + buttons

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                scrollLeft,
                rightPanel
        );
        splitPane.setResizeWeight(0.4); // 40/60
        splitPane.setDividerSize(8);

        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);
    }

    private void loadCategories() {
        jPanel1.removeAll();
        jPanel1.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblHeader = new JLabel("Chọn danh mục chi tiêu");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader, BorderLayout.WEST);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(248, 249, 250));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        try {
            List<Category> allCategories = categoryService.getAllCategories();

            for (Category parent : allCategories) {
                if ((parent.getDanhMucChaId() == null || parent.getDanhMucChaId() == 0)
                        && "Chi".equalsIgnoreCase(parent.getLoaiDanhMuc())) {

                    CategoryParentPanel parentPanel = new CategoryParentPanel(parent);
                    parentPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            chonDanhMucCha(parent);
                        }
                    });
                    contentPanel.add(parentPanel);
                    contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

                    JPanel childContainer = new JPanel(new BorderLayout());
                    childContainer.setBackground(Color.WHITE);
                    childContainer.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                            BorderFactory.createEmptyBorder(15, 15, 15, 15)
                    ));

                    JPanel childPanel = new JPanel(new GridLayout(0, 3, 15, 15));
                    childPanel.setBackground(Color.WHITE);

                    for (Category child : allCategories) {
                        if (child.getDanhMucChaId() != null && child.getDanhMucChaId().equals(parent.getId())) {
                            CategoryItemPanel childItem = new CategoryItemPanel(child);
                            childItem.addMouseListener(new java.awt.event.MouseAdapter() {
                                public void mouseClicked(java.awt.event.MouseEvent evt) {
                                    chonDanhMucCon(child);
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

        jPanel1.add(headerPanel, BorderLayout.NORTH);
        jPanel1.add(scrollPane, BorderLayout.CENTER);
        jPanel1.revalidate();
        jPanel1.repaint();
    }

    private void loadBudgets() {
        jPanel2.removeAll();
        jPanel2.setLayout(new BorderLayout());

        // Panel tổng thể chứa header + content
        JPanel fullPanel = new JPanel();
        fullPanel.setLayout(new BoxLayout(fullPanel, BoxLayout.Y_AXIS));
        fullPanel.setBackground(new Color(248, 249, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel lblHeader = new JLabel("Danh sách ngân sách");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader, BorderLayout.WEST);

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadBudgets());
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        fullPanel.add(headerPanel);
        fullPanel.add(Box.createRigidArea(new Dimension(0, 10))); // khoảng cách

        // Content danh sách ngân sách
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(248, 249, 250));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        try {
            List<Budget> budgets = budgetService.getAllBudgetsByUser(userId);
            if (budgets.isEmpty()) {
                JLabel lblEmpty = new JLabel("Chưa có ngân sách nào");
                lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);
                contentPanel.add(lblEmpty);
            } else {
                for (Budget budget : budgets) {
                    BudgetItemPanel itemPanel = new BudgetItemPanel(budget);
                    contentPanel.add(itemPanel);
                    contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        fullPanel.add(contentPanel);

        // Scroll toàn bộ fullPanel
        JScrollPane scrollPane = new JScrollPane(fullPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        jPanel2.add(scrollPane, BorderLayout.CENTER);
        jPanel2.revalidate();
        jPanel2.repaint();
    }

    private void chonDanhMucCha(Category parent) {
        selectedCategory = null;
        selectedParentCategory = parent;
        selectedBudget = null;

        txtDanhMucCha.setText(parent.getTenDanhMuc());
        txtTenDanhMuc.setText("");
        txtSoTien.setText("");
        txtSoTien.setEnabled(false);
        txtTongNganSach.setEnabled(true);

        BigDecimal tongHienTai = budgetService.getTongNganSachTheoCha(userId, parent.getId());
        txtTongNganSach.setText(tongHienTai.compareTo(BigDecimal.ZERO) == 0 ? "" : formatMoney(tongHienTai));

        List<Category> children = categoryService.getAllCategories().stream()
                .filter(c -> c.getDanhMucChaId() != null && c.getDanhMucChaId().equals(parent.getId()))
                .collect(Collectors.toList());

        long tongCon = children.size();
        long daCo = children.stream()
                .filter(c -> budgetService.getBudgetByUserAndCategory(userId, c.getId()) != null)
                .count();

        StringBuilder info = new StringBuilder();
        info.append("THÔNG TIN DANH MỤC CHA: ").append(parent.getTenDanhMuc()).append("\n");
        info.append("----------------------------------------\n");
        info.append("Tổng số danh mục con: ").append(tongCon).append("\n");
        info.append("Đã có ngân sách: ").append(daCo).append("\n");
        info.append("Chưa có hoặc tự động: ").append(tongCon - daCo).append("\n");
        info.append("Tổng ngân sách hiện tại: ").append(formatMoney(tongHienTai)).append("\n");
        info.append("----------------------------------------\n\n");
        info.append("HƯỚNG DẪN:\n");
        info.append("- Nhập tổng ngân sách mới vào ô 'Tổng ngân sách'\n");
        info.append("- Hệ thống sẽ chia đều cho tất cả các danh mục con\n");
        info.append("- Bạn có thể chọn thêm một danh mục con để đặt số tiền riêng");

        JOptionPane.showMessageDialog(this, info.toString(), "Đặt ngân sách cho danh mục cha", JOptionPane.INFORMATION_MESSAGE);
    }

    private void chonDanhMucCon(Category child) {
        selectedCategory = child;
        selectedBudget = null;
        txtSoTien.setEnabled(true);

        Category parent = categoryService.getCategoryById(child.getDanhMucChaId());
        txtDanhMucCha.setText(parent != null ? parent.getTenDanhMuc() : "");
        txtTenDanhMuc.setText(child.getTenDanhMuc());

        boolean isChildOfSelectedParent = selectedParentCategory != null &&
                child.getDanhMucChaId() != null &&
                child.getDanhMucChaId().equals(selectedParentCategory.getId());

        if (isChildOfSelectedParent) {
            txtTongNganSach.setEnabled(true);
        } else {
            selectedParentCategory = null;
            txtTongNganSach.setEnabled(false);
            txtTongNganSach.setText("");
        }

        selectedBudget = budgetService.getBudgetByUserAndCategory(userId, child.getId());
        
        if (selectedBudget != null) {
            txtSoTien.setText(formatMoney(selectedBudget.getTongNganSach()));
            JOptionPane.showMessageDialog(this,
                    "NGÂN SÁCH HIỆN TẠI\n" +
                    "----------------------------------------\n" +
                    "Danh mục: " + child.getTenDanhMuc() + "\n" +
                    "Ngân sách: " + formatMoney(selectedBudget.getTongNganSach()) + "\n" +
                    "Đã chi: " + formatMoney(selectedBudget.getDaDung()) + "\n" +
                    "Còn lại: " + formatMoney(selectedBudget.getConLai()) + "\n" +
                    budgetService.getBudgetWarning(selectedBudget) + "\n" +
                    "----------------------------------------\n\n" +
                    "Bạn có thể chỉnh sửa số tiền và nhấn 'Đặt ngân sách'",
                    "Ngân sách hiện tại", JOptionPane.INFORMATION_MESSAGE);
        } else {
            txtSoTien.setText("");
            JOptionPane.showMessageDialog(this,
                    "TẠO NGÂN SÁCH MỚI\n" +
                    "----------------------------------------\n" +
                    "Danh mục: " + child.getTenDanhMuc() + "\n" +
                    "Chưa có ngân sách\n" +
                    "----------------------------------------\n\n" +
                    "Nhập số tiền và nhấn 'Đặt ngân sách'",
                    "Tạo ngân sách mới", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void datNganSachChoDanhMucCha(Category parent, BigDecimal tongNganSachMoi, Category specificChild, BigDecimal soTienCon) {
    List<Category> children = categoryService.getAllCategories().stream()
            .filter(c -> c.getDanhMucChaId() != null && c.getDanhMucChaId().equals(parent.getId()))
            .collect(Collectors.toList());

    if (children.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Danh mục cha không có danh mục con!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Tính toán phần chia đều
    BigDecimal soTienChiaDeu;
    List<Category> childrenToDistribute;
    
    if (specificChild != null && soTienCon != null) {
        // Trường hợp có đặt riêng cho 1 con
        if (tongNganSachMoi.compareTo(soTienCon) < 0) {
            JOptionPane.showMessageDialog(this,
                    "Tổng ngân sách cha phải lớn hơn hoặc bằng số tiền đặt cho con!\n\n" +
                    "Tổng cha: " + formatMoney(tongNganSachMoi) + "\n" +
                    "Số tiền con: " + formatMoney(soTienCon),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        soTienChiaDeu = tongNganSachMoi.subtract(soTienCon);
        childrenToDistribute = children.stream()
                .filter(c -> c.getId() != specificChild.getId())
                .collect(Collectors.toList());
    } else {
        // Trường hợp chia đều cho tất cả
        soTienChiaDeu = tongNganSachMoi;
        childrenToDistribute = new ArrayList<>(children);
    }

    if (childrenToDistribute.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Chỉ có 1 danh mục con, không cần chia đều!",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        if (specificChild != null) {
            datNganSachChoDanhMucCon(specificChild, soTienCon, false);
        }
        return;
    }

    int soLuongChia = childrenToDistribute.size();
    BigDecimal moiConNhan = soTienChiaDeu.divide(BigDecimal.valueOf(soLuongChia), 0, RoundingMode.DOWN);
    BigDecimal phanDu = soTienChiaDeu.subtract(moiConNhan.multiply(BigDecimal.valueOf(soLuongChia)));

    if (phanDu.compareTo(BigDecimal.ZERO) > 0) {
        JOptionPane.showMessageDialog(this,
                "Không thể chia đều!\n\n" +
                "Phần cần chia: " + formatMoney(soTienChiaDeu) + "\n" +
                "Số danh mục: " + soLuongChia + "\n" +
                "Phần dư: " + formatMoney(phanDu) + "\n\n" +
                "Vui lòng điều chỉnh tổng ngân sách để chia hết.",
                "Lỗi chia đều", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Hiển thị xác nhận
    StringBuilder msg = new StringBuilder();
    msg.append("XÁC NHẬN ĐẶT NGÂN SÁCH CHO DANH MỤC CHA\n");
    msg.append("========================================\n\n");
    msg.append("Danh mục cha: ").append(parent.getTenDanhMuc()).append("\n");
    msg.append("Tổng ngân sách mới: ").append(formatMoney(tongNganSachMoi)).append("\n\n");

    if (specificChild != null) {
        msg.append("Đặt riêng:\n");
        msg.append("  - ").append(specificChild.getTenDanhMuc()).append(": ").append(formatMoney(soTienCon)).append("\n\n");
    }

    msg.append("Chia đều cho ").append(soLuongChia).append(" danh mục còn lại:\n");
    msg.append("  - Mỗi con nhận: ").append(formatMoney(moiConNhan)).append("\n\n");
    msg.append("LƯU Ý: Tất cả ngân sách cũ sẽ được LOAD LẠI theo số tiền mới!");

    int confirm = JOptionPane.showConfirmDialog(this, msg.toString(), 
            "Xác nhận đặt ngân sách", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    
    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    // Bắt đầu thực hiện
    int success = 0;
    int error = 0;
    StringBuilder errorDetail = new StringBuilder();

    // Xử lý danh mục đặt riêng 
    if (specificChild != null && soTienCon != null) {
        Budget existing = null;

        try {
            existing = budgetService.getBudgetByUserAndCategory(userId, specificChild.getId());
        } catch (Exception ex) {
            System.err.println("Lỗi khi load budget cho danh mục " + specificChild.getTenDanhMuc() + ": " + ex.getMessage());
            existing = null;
        }

        BudgetService.Response resp;

        if (existing != null) {
            // Kiểm tra số tiền mới không được nhỏ hơn đã chi
            BigDecimal daDung = existing.getDaDung() != null ? existing.getDaDung() : BigDecimal.ZERO;
            if (soTienCon.compareTo(daDung) < 0) {
                JOptionPane.showMessageDialog(this,
                        "Không thể đặt ngân sách!\n\n" +
                        "Danh mục: " + specificChild.getTenDanhMuc() + "\n" +
                        "Đã chi: " + formatMoney(daDung) + "\n" +
                        "Số tiền mới: " + formatMoney(soTienCon) + "\n\n" +
                        "Ngân sách mới phải >= số tiền đã chi!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            existing.setTongNganSach(soTienCon);
            existing.updateConLai();
            existing.setGhiChu("Đặt riêng từ danh mục cha: " + parent.getTenDanhMuc());
            resp = budgetService.updateBudget(existing);
        } else {
            resp = budgetService.createBudget(
                    userId,
                    specificChild.getId(),
                    specificChild.getTenDanhMuc(),
                    specificChild.getLoaiDanhMuc(),
                    soTienCon,
                    "Tháng",
                    "Đặt riêng từ danh mục cha: " + parent.getTenDanhMuc()
            );
        }

        if (resp.result == BudgetService.Result.SUCCESS) {
            success++;
        } else {
            error++;
            errorDetail.append("- ").append(specificChild.getTenDanhMuc()).append(": ").append(resp.message).append("\n");
        }
    }

    // Xử lý các danh mục chia đều
    for (Category child : childrenToDistribute) {
        Budget existing = null;

        try {
            existing = budgetService.getBudgetByUserAndCategory(userId, child.getId());
        } catch (Exception ex) {
            System.err.println("Lỗi khi load budget cho danh mục " + child.getTenDanhMuc() + ": " + ex.getMessage());
            existing = null;
        }

        BudgetService.Response resp;

        if (existing != null) {
            // Kiểm tra số tiền mới không được nhỏ hơn đã chi
            if (moiConNhan.compareTo(existing.getDaDung() != null ? existing.getDaDung() : BigDecimal.ZERO) < 0) {
                error++;
                errorDetail.append("- ").append(child.getTenDanhMuc())
                        .append(": Ngân sách mới (").append(formatMoney(moiConNhan))
                        .append(") < Đã chi (").append(formatMoney(existing.getDaDung())).append(")\n");
                continue;
            }

            existing.setTongNganSach(moiConNhan);
            existing.updateConLai();
            existing.setGhiChu("Tự động chia đều từ danh mục cha: " + parent.getTenDanhMuc());
            resp = budgetService.updateBudget(existing);
        } else {
            resp = budgetService.createBudget(
                    userId,
                    child.getId(),
                    child.getTenDanhMuc(),
                    child.getLoaiDanhMuc(),
                    moiConNhan,
                    "Tháng",
                    "Tự động chia đều từ danh mục cha: " + parent.getTenDanhMuc()
            );
        }

        if (resp.result == BudgetService.Result.SUCCESS) {
            success++;
        } else {
            error++;
            errorDetail.append("- ").append(child.getTenDanhMuc()).append(": ").append(resp.message).append("\n");
        }
    }

    // Hiển thị kết quả
    StringBuilder result = new StringBuilder();
        result.append("HOÀN THÀNH ĐẶT NGÂN SÁCH!\n\n");
        result.append("Thành công: ").append(success).append("\n");
        result.append("Lỗi: ").append(error).append("\n");

        if (error > 0) {
            result.append("\nChi tiết lỗi:\n").append(errorDetail.toString());
        }

        JOptionPane.showMessageDialog(this, result.toString(), 
                error == 0 ? "Thành công" : "Hoàn thành với lỗi", 
                error == 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

        loadBudgets();
        huyBo();
    }

    private void datNganSachChoDanhMucCon(Category child, BigDecimal soTien, boolean showConfirm) {
        Budget existing = budgetService.getBudgetByUserAndCategory(userId, child.getId());

        if (existing != null && soTien.compareTo(existing.getDaDung()) < 0) {
            JOptionPane.showMessageDialog(this,
                    "SỐ TIỀN KHÔNG HỢP LỆ!\n\n" +
                    "Đã chi: " + formatMoney(existing.getDaDung()) + "\n" +
                    "Bạn nhập: " + formatMoney(soTien),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder msg = new StringBuilder();
        msg.append("XÁC NHẬN ĐẶT NGÂN SÁCH\n");
        msg.append("----------------------------------------\n");
        msg.append("Danh mục: ").append(child.getTenDanhMuc()).append("\n");
        msg.append("Số tiền: ").append(formatMoney(soTien)).append("\n");

        int confirm = showConfirm ? JOptionPane.showConfirmDialog(this, msg.toString(), "Xác nhận", JOptionPane.YES_NO_OPTION) : JOptionPane.YES_OPTION;
        if (confirm != JOptionPane.YES_OPTION) return;

        BudgetService.Response response;
        if (existing != null) {
            existing.setTongNganSach(soTien);
            existing.updateConLai();
            response = budgetService.updateBudget(existing);
        } else {
            response = budgetService.createBudget(
                    userId,
                    child.getId(),
                    child.getTenDanhMuc(),
                    child.getLoaiDanhMuc(),
                    soTien,
                    "Tháng",
                    null
            );
        }

        JOptionPane.showMessageDialog(this,
                response.result == BudgetService.Result.SUCCESS ? "Thành công: " + response.message : "Lỗi: " + response.message,
                response.result == BudgetService.Result.SUCCESS ? "Thành công" : "Lỗi",
                response.result == BudgetService.Result.SUCCESS ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

        if (response.result == BudgetService.Result.SUCCESS) {
            loadBudgets();
            huyBo();
        }
    }

    private void datNganSachChoDanhMucCon(Category child, BigDecimal soTien) {
        datNganSachChoDanhMucCon(child, soTien, true);
    }

    private BigDecimal parseMoney(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        try {
            String cleaned = input.replaceAll("[^0-9]", "");
            if (cleaned.isEmpty()) {
                return null;
            }
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ: " + input, "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void datNganSach() {
        String tongStr = txtTongNganSach.getText().trim();
        String soTienStr = txtSoTien.getText().trim();

        BigDecimal tong = parseMoney(tongStr);
        BigDecimal soTien = parseMoney(soTienStr);

        // Kiểm tra input cơ bản
        if (tong == null && soTien == null) {
            JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập ít nhất một trong hai:\n- Tổng ngân sách (cho danh mục cha)\n- Số tiền (cho danh mục con)", 
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (tong != null && tong.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "Tổng ngân sách phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (soTien != null && soTien.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Trường hợp 1: Chọn cả cha và con, nhập cả tổng và số tiền
        if (selectedParentCategory != null && selectedCategory != null && tong != null && soTien != null) {
            if (tong.compareTo(soTien) < 0) {
                JOptionPane.showMessageDialog(this, 
                        "Tổng ngân sách cha phải >= số tiền con riêng!\n\n" +
                        "Tổng cha: " + formatMoney(tong) + "\n" +
                        "Số tiền con: " + formatMoney(soTien), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            datNganSachChoDanhMucCha(selectedParentCategory, tong, selectedCategory, soTien);
        }
        // Trường hợp 2: Chỉ chọn cha, chỉ nhập tổng (chia đều cho tất cả con)
        else if (selectedParentCategory != null && tong != null && soTien == null) {
            datNganSachChoDanhMucCha(selectedParentCategory, tong, null, null);
        }
        // Trường hợp 3: Chỉ chọn con, chỉ nhập số tiền (đặt riêng cho con)
        else if (selectedCategory != null && soTien != null && tong == null) {
            datNganSachChoDanhMucCon(selectedCategory, soTien);
        }
        // Trường hợp không hợp lệ
        else {
            JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn đúng danh mục và nhập số tiền phù hợp!\n\n" +
                    "Hướng dẫn:\n" +
                    "- Chọn danh mục cha + nhập tổng ngân sách: Chia đều cho con\n" +
                    "- Chọn danh mục cha + con + nhập cả hai: Đặt riêng cho con, chia đều phần còn lại\n" +
                    "- Chọn danh mục con + nhập số tiền: Đặt riêng cho con đó", 
                    "Lỗi", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private JPanel buildRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        // Tạo panel chứa tất cả: header + form
        JPanel fullRightPanel = new JPanel();
        fullRightPanel.setLayout(new BoxLayout(fullRightPanel, BoxLayout.Y_AXIS));
        fullRightPanel.setBackground(Color.WHITE);

        // Header
        fullRightPanel.add(jPanel2);

        // Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(jLabel1);
        formPanel.add(txtDanhMucCha);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(jLabel2);
        formPanel.add(txtTongNganSach);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(jLabel3);
        formPanel.add(txtTenDanhMuc);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(jLabel4);
        formPanel.add(txtSoTien);
        formPanel.add(Box.createVerticalStrut(15));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnDat);
        btnPanel.add(btnHuy);

        formPanel.add(btnPanel);

        fullRightPanel.add(formPanel);

        // Scroll toàn bộ fullRightPanel
        JScrollPane scrollForm = new JScrollPane(fullRightPanel);
        scrollForm.setBorder(null);
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);

        rightPanel.add(scrollForm, BorderLayout.CENTER);

        return rightPanel;
    }

    private void chinhSuaNganSach(Budget budget) {
        selectedBudget = budget;
        selectedCategory = null;
        selectedParentCategory = null;

        if (budget.getDanhMucId() != null) {
            Category cat = categoryService.getCategoryById(budget.getDanhMucId());
            if (cat != null) {
                selectedCategory = cat;
                Category parent = categoryService.getCategoryById(cat.getDanhMucChaId());
                txtDanhMucCha.setText(parent != null ? parent.getTenDanhMuc() : "");
            }
        }

        txtTongNganSach.setText("");
        txtTongNganSach.setEnabled(false);
        txtTenDanhMuc.setText(budget.getTenDanhMuc());
        txtSoTien.setText(formatMoney(budget.getTongNganSach()));
        txtSoTien.setEnabled(true);

        JOptionPane.showMessageDialog(this, "Chỉ thay đổi số tiền ngân sách của danh mục này.", "Chỉnh sửa", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void refreshBudgetList() {
        loadBudgets();
    }

    private void showBudgetDetails(Budget budget) {
        JOptionPane.showMessageDialog(this,
                "Danh mục: " + budget.getTenDanhMuc() + "\n" +
                "Ngân sách: " + formatMoney(budget.getTongNganSach()) + "\n" +
                "Đã chi: " + formatMoney(budget.getDaDung()) + "\n" +
                "Còn lại: " + formatMoney(budget.getConLai()) + "\n" +
                budgetService.getBudgetWarning(budget),
                "Chi tiết ngân sách", JOptionPane.INFORMATION_MESSAGE);
    }

    private void huyBo() {
        selectedCategory = null;
        selectedParentCategory = null;
        selectedBudget = null;
        txtDanhMucCha.setText("");
        txtTongNganSach.setText("");
        txtTenDanhMuc.setText("");
        txtSoTien.setText("");
        txtSoTien.setEnabled(false);
        txtTongNganSach.setEnabled(false);
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) return "";
        return String.format("%,d", amount.longValue());
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblNganSach = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnDat = new javax.swing.JButton();
        btnHuy = new javax.swing.JButton();
        txtDanhMucCha = new javax.swing.JTextField();
        txtTongNganSach = new javax.swing.JTextField();
        txtTenDanhMuc = new javax.swing.JTextField();
        txtSoTien = new javax.swing.JTextField();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 418, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 464, Short.MAX_VALUE)
        );

        lblNganSach.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblNganSach.setText("Thông tin ngân sách của danh mục");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblNganSach)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(lblNganSach, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Danh mục cha:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Tổng ngân sách:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Tên danh mục:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Số tiền: ");

        btnDat.setBackground(new java.awt.Color(0, 153, 51));
        btnDat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDat.setForeground(new java.awt.Color(255, 255, 255));
        btnDat.setText("Đặt ngân sách");
        btnDat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDatActionPerformed(evt);
            }
        });

        btnHuy.setBackground(new java.awt.Color(255, 0, 0));
        btnHuy.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnHuy.setForeground(new java.awt.Color(255, 255, 255));
        btnHuy.setText("Hủy");

        txtDanhMucCha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDanhMucChaActionPerformed(evt);
            }
        });

        txtTongNganSach.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTongNganSachActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnDat)
                                .addGap(18, 18, 18)
                                .addComponent(btnHuy, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(17, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(txtDanhMucCha))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTongNganSach))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTenDanhMuc)
                                    .addComponent(txtSoTien)))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtDanhMucCha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40)
                        .addComponent(jLabel2))
                    .addComponent(txtTongNganSach, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtTenDanhMuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(txtSoTien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(59, 59, 59)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDat, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHuy, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnDatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDatActionPerformed
        datNganSach();
    }//GEN-LAST:event_btnDatActionPerformed

    private void txtDanhMucChaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDanhMucChaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDanhMucChaActionPerformed

    private void txtTongNganSachActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTongNganSachActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTongNganSachActionPerformed
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDat;
    private javax.swing.JButton btnHuy;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblNganSach;
    private javax.swing.JTextField txtDanhMucCha;
    private javax.swing.JTextField txtSoTien;
    private javax.swing.JTextField txtTenDanhMuc;
    private javax.swing.JTextField txtTongNganSach;
    // End of variables declaration//GEN-END:variables
}
