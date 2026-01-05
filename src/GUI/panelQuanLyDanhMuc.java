/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI;

import Model.CategoryParentPanel;
import Model.Category;
import Model.CategoryItemPanel;
import Service.CategoryService;
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
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Admin
 */
public class panelQuanLyDanhMuc extends javax.swing.JPanel {

    private Category selectedCategory = null;
    private String selectedIconPath = null;
    private boolean isEditMode = false;
    private boolean isUpdatingParentCategory = false;
    /**
     * Creates new form panelQuanLyDanhMuc
     */
    public panelQuanLyDanhMuc(int userId) {
        initComponents();
        SwingUtilities.invokeLater(() -> setupLabelIcons());
        loadCategory();
        
        btnThemMoi.addActionListener(e -> addCategory());
        btnCapNhat.addActionListener(e -> updateCategory());
        btnXoa.addActionListener(e -> deleteCategory());
        btnHuyBo.addActionListener(e -> cancelCategory());

        setupSearchListener();
        
        //Khóa tất cả text field ban đầu 
        lockAllFields();
        
        lblAvatar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblAvatar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (isEditMode) {
                    chonIconDanhMuc();
                }
            }
        });
        // Set hints 
        HintUtils.setHint(txtTenDM, "Nhập tên danh mục...");
        HintUtils.setHint(txtLoaiDM, "Nhập loại danh mục...");
        HintUtils.setHint(txtDMCha, "Nhập danh mục cha...");
        HintUtils.setHint(txtViTri, "Nhập vị trí hiển thị danh mục...");
        HintUtils.setHint(txtCapDo, "Nhập cấp danh mục...");
        HintUtils.setHint(txtTrangThai, "Hệ thống tự động thiết lập...");
        HintUtils.setHint(txtMoTa, "Nhập mô tả...");
    }

    private void loadCategory() {
        panelDanhMuc.removeAll();
        panelDanhMuc.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        ImageIcon iconchi = resizeIcon("/resources/spend.png", 20, 20);
        ImageIcon iconthu = resizeIcon("/resources/revenue.png", 20, 20);

        
        JPanel panelChi = createCategoryPanel("Chi");
        tabbedPane.addTab("Chi tiền ", iconchi ,panelChi);
        
        JPanel panelThu = createCategoryPanel("Thu");
        tabbedPane.addTab("Thu tiền", iconthu,panelThu);
        
        panelDanhMuc.add(tabbedPane, BorderLayout.CENTER);
        panelDanhMuc.revalidate();
        panelDanhMuc.repaint();
    }
    
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

                    // Thêm panel cha
                    CategoryParentPanel parentPanel = new CategoryParentPanel(parent);
                    parentPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            showCategoryDetails(parent);
                        }
                    });
                    contentPanel.add(parentPanel);

                    // Thêm panel con
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
                                    showCategoryDetails(child);
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

    private void showCategoryDetails(Category dm) {
       // Nếu đang ở chế độ chỉnh sửa (thêm mới hoặc cập nhật)
       if (isEditMode) {
           // Nếu đang cập nhật danh mục cha -> KHÔNG cho phép chọn danh mục cha khác
           if (isUpdatingParentCategory) {
               javax.swing.JOptionPane.showMessageDialog(this, 
                   "Không thể chọn danh mục cha!\n" +
                   "Bạn đang cập nhật một DANH MỤC CHA (cấp 1).\n" +
                   "Nếu chọn danh mục cha khác, danh mục này sẽ chuyển thành danh mục con.",
                   "Cảnh báo", 
                   javax.swing.JOptionPane.WARNING_MESSAGE);
               return;
           }

           // Nếu đang thêm mới hoặc cập nhật danh mục con -> cho phép chọn danh mục cha
           // Kiểm tra xem có phải danh mục cha không (cấp độ 1)
           if (dm.getDanhMucChaId() == null || dm.getDanhMucChaId() == 0) {
               // Đây là danh mục cha -> cho phép chọn
               txtDMCha.setText(String.valueOf(dm.getId()));
               javax.swing.JOptionPane.showMessageDialog(this, 
                   "Đã chọn danh mục cha: " + dm.getTenDanhMuc(),
                   "Thông báo", 
                   javax.swing.JOptionPane.INFORMATION_MESSAGE);
           } else {
               // Đây là danh mục con -> không cho chọn
               javax.swing.JOptionPane.showMessageDialog(this, 
                   "Vui lòng chọn danh mục cha (cấp độ 1)!\nDanh mục này là danh mục con.",
                   "Cảnh báo", 
                   javax.swing.JOptionPane.WARNING_MESSAGE);
           }
           return;
       }

       // Chế độ xem bình thường (không chỉnh sửa)
       this.selectedCategory = dm;
       txtTenDM.setText(dm.getTenDanhMuc());
       txtLoaiDM.setText(dm.getLoaiDanhMuc());
       txtDMCha.setText(dm.getDanhMucChaId() != null ? dm.getDanhMucChaId().toString() : "");
       txtViTri.setText(String.valueOf(dm.getThuTuHienThi()));
       txtCapDo.setText(String.valueOf(dm.getCapDo()));
       txtMoTa.setText(dm.getMoTa() != null ? dm.getMoTa() : "");
       txtTrangThai.setText(dm.getTrangThai() != null ? dm.getTrangThai() : "");

       // Hiển thị icon nếu có
       if (dm.getBieuTuong() != null && !dm.getBieuTuong().isEmpty()) {
           try {
               ImageIcon icon = new ImageIcon(dm.getBieuTuong());
               Image scaledImg = icon.getImage().getScaledInstance(56, 56, Image.SCALE_SMOOTH);
               lblAvatar.setIcon(new ImageIcon(scaledImg));
               lblAvatar.setText("");
           } catch (Exception e) {
               lblAvatar.setIcon(null);
               lblAvatar.setText("Ảnh");
           }
       } else {
           lblAvatar.setIcon(null);
           lblAvatar.setText("Ảnh");
       }
   }
    
    private void addCategory() {
        if (!isEditMode) {
            // Bật chế độ thêm mới
            cancelCategory();
            unlockFieldsForEdit();
            selectedCategory = null; // Đảm bảo không có danh mục nào được chọn
            isUpdatingParentCategory = false;
            btnThemMoi.setText("Xác nhận");
            btnCapNhat.setEnabled(false);
            btnXoa.setEnabled(false);

            // Hiển thị hướng dẫn
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Chế độ thêm mới đã được kích hoạt!\n" +
                "- Nhập thông tin danh mục\n" +
                "- Click vào DANH MỤC CHA ở danh sách bên trái để chọn (nếu cần)\n" +
                "- Click vào ảnh để chọn icon\n" +
                "- Nhấn 'Xác nhận' để lưu hoặc 'Hủy bỏ' để thoát",
                "Hướng dẫn", 
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Xác nhận thêm mới
        try {
            // Validate dữ liệu
            if (txtTenDM.getText().trim().isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng nhập tên danh mục!");
                return;
            }
            if (txtLoaiDM.getText().trim().isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng nhập loại danh mục!");
                return;
            }

            Category c = new Category();
            c.setTenDanhMuc(txtTenDM.getText().trim());
            c.setLoaiDanhMuc(txtLoaiDM.getText().trim());

            String dmChaText = txtDMCha.getText().trim();
            c.setDanhMucChaId(dmChaText.isEmpty() ? null : Integer.parseInt(dmChaText));

            c.setThuTuHienThi(Integer.parseInt(txtViTri.getText().trim()));
            c.setCapDo(Integer.parseInt(txtCapDo.getText().trim()));
            c.setMoTa(txtMoTa.getText().trim());
            c.setTrangThai("Hoạt động");
            c.setBieuTuong(selectedIconPath);

            CategoryService.Response res = new CategoryService().addCategory(c);
            javax.swing.JOptionPane.showMessageDialog(this, res.message);

            if (res.result == CategoryService.Result.SUCCESS) {
                loadCategory();
                resetToViewMode();
            }
        } catch (NumberFormatException ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho vị trí và cấp độ!");
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
    
    private void updateCategory() {
        if (!isEditMode) {
            // Kiểm tra đã chọn danh mục chưa
            if (selectedCategory == null) {
                javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục cần cập nhật!");
                return;
            }

            // Kiểm tra xem có phải đang cập nhật danh mục cha không
            isUpdatingParentCategory = (selectedCategory.getDanhMucChaId() == null || selectedCategory.getDanhMucChaId() == 0);

            // Bật chế độ cập nhật
            unlockFieldsForEdit();
            btnCapNhat.setText("Xác nhận");
            btnThemMoi.setEnabled(false);
            btnXoa.setEnabled(false);

            // Hiển thị hướng dẫn khác nhau tùy loại danh mục
            if (isUpdatingParentCategory) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Chế độ cập nhật DANH MỤC CHA!\n" +
                    "- Chỉnh sửa thông tin danh mục\n" +
                    "- KHÔNG thể chọn danh mục cha khác (danh mục này đã là cấp 1)\n" +
                    "- Click vào ảnh để đổi icon\n" +
                    "- Nhấn 'Xác nhận' để lưu hoặc 'Hủy bỏ' để thoát",
                    "Hướng dẫn", 
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Chế độ cập nhật DANH MỤC CON!\n" +
                    "- Chỉnh sửa thông tin danh mục\n" +
                    "- Click vào DANH MỤC CHA khác ở danh sách để thay đổi (nếu cần)\n" +
                    "- Click vào ảnh để đổi icon\n" +
                    "- Nhấn 'Xác nhận' để lưu hoặc 'Hủy bỏ' để thoát",
                    "Hướng dẫn", 
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        }

        // Xác nhận cập nhật
        try {
            // Validate dữ liệu
            if (txtTenDM.getText().trim().isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng nhập tên danh mục!");
                return;
            }

            selectedCategory.setTenDanhMuc(txtTenDM.getText().trim());
            selectedCategory.setLoaiDanhMuc(txtLoaiDM.getText().trim());

            String dmChaText = txtDMCha.getText().trim();
            selectedCategory.setDanhMucChaId(dmChaText.isEmpty() ? null : Integer.parseInt(dmChaText));

            selectedCategory.setThuTuHienThi(Integer.parseInt(txtViTri.getText().trim()));
            selectedCategory.setCapDo(Integer.parseInt(txtCapDo.getText().trim()));
            selectedCategory.setMoTa(txtMoTa.getText().trim());
            selectedCategory.setBieuTuong(selectedIconPath);

            CategoryService.Response res = new CategoryService().updateCategory(selectedCategory);
            javax.swing.JOptionPane.showMessageDialog(this, res.message);

            if (res.result == CategoryService.Result.SUCCESS) {
                loadCategory();
                resetToViewMode();
            }
        } catch (NumberFormatException ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho vị trí và cấp độ!");
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
    
    private void deleteCategory() {
        if (selectedCategory == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục cần xóa!");
            return;
        }

        int confirm = javax.swing.JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa danh mục này?", "Xác nhận", 
            javax.swing.JOptionPane.YES_NO_OPTION);

        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            CategoryService.Response res = new CategoryService().deleteCategory(selectedCategory.getId());
            javax.swing.JOptionPane.showMessageDialog(this, res.message);

            if (res.result == CategoryService.Result.SUCCESS) {
                loadCategory();
                resetToViewMode();
            }
        }
    }

    private void cancelCategory() {
        selectedCategory = null;
        selectedIconPath = null;
        txtTenDM.setText("");
        txtLoaiDM.setText("");
        txtDMCha.setText("");
        txtViTri.setText("");
        txtCapDo.setText("");
        txtMoTa.setText("");
        txtTrangThai.setText("");
        lblAvatar.setIcon(null);
        lblAvatar.setText("Ảnh");

        resetToViewMode();
    }

    private void resetToViewMode() {
        lockAllFields();
        isUpdatingParentCategory = false;
        btnThemMoi.setText("Thêm mới");
        btnCapNhat.setText("Cập nhật");
        btnThemMoi.setEnabled(true);
        btnCapNhat.setEnabled(true);
        btnXoa.setEnabled(true);
    }
    
    // Interface quản lý thao tác người dùng 
    private void setupSearchListener(){
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                performSearch();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                performSearch();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                performSearch();
            }
        });
    }
    
    private void performSearch() {
        String keyword = txtSearch.getText().trim();

        // Nếu keyword rỗng, load lại toàn bộ danh mục
        if (keyword.isEmpty()) {
            loadCategory();
            return;
        }

        // Tìm kiếm và hiển thị kết quả
        loadSearchResults(keyword);
    }
    
    private void loadSearchResults(String keyword){
        panelDanhMuc.removeAll();
        panelDanhMuc.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        ImageIcon iconchi = resizeIcon("/resources/spend.png", 20, 20);
        ImageIcon iconthu = resizeIcon("/resources/revenue.png", 20, 20);

        JPanel panelChi = createSearchPanel("Chi", keyword);
        tabbedPane.addTab("Chi tiền ", iconchi, panelChi);

        JPanel panelThu = createSearchPanel("Thu", keyword);
        tabbedPane.addTab("Thu tiền", iconthu, panelThu);

        panelDanhMuc.add(tabbedPane, BorderLayout.CENTER);
        panelDanhMuc.revalidate();
        panelDanhMuc.repaint();
    }
    
    private JPanel createSearchPanel(String loaiDanhMuc, String keyword) {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(248, 249, 250));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        try {
            List<Category> allCategories = new CategoryService().getAllCategories();
            List<Category> searchResults = new CategoryService().searchCategoriesByName(keyword);

            // Lọc theo loại danh mục
            List<Category> filteredResults = new ArrayList<>();
            for (Category cat : searchResults) {
                if (loaiDanhMuc.equalsIgnoreCase(cat.getLoaiDanhMuc())) {
                    filteredResults.add(cat);
                }
            }

            if (filteredResults.isEmpty()) {
                JLabel lblNoResult = new JLabel("Không tìm thấy danh mục phù hợp với từ khóa: \"" + keyword + "\"");
                lblNoResult.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                lblNoResult.setForeground(Color.GRAY);
                lblNoResult.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                contentPanel.add(Box.createRigidArea(new Dimension(0, 50)));
                contentPanel.add(lblNoResult);
            } else {
                // Dùng Set để tránh hiển thị trùng danh mục cha
                java.util.Set<Integer> displayedParents = new java.util.HashSet<>();

                for (Category result : filteredResults) {
                    Category parent = null;
                    List<Category> childrenToShow = new ArrayList<>();

                    if (result.getDanhMucChaId() == null || result.getDanhMucChaId() == 0) {
                        // Kết quả là danh mục cha -> hiển thị tất cả con
                        parent = result;
                        for (Category child : allCategories) {
                            if (child.getDanhMucChaId() != null && child.getDanhMucChaId().equals(parent.getId())) {
                                childrenToShow.add(child);
                            }
                        }
                    } else {
                        // Kết quả là danh mục con -> chỉ hiển thị con đó
                        parent = allCategories.stream()
                                .filter(cat -> Objects.equals(cat.getId(), result.getDanhMucChaId()))
                                .findFirst().orElse(null);
                        childrenToShow.add(result);
                    }

                    if (parent != null && !displayedParents.contains(parent.getId())) {
                        displayedParents.add(parent.getId());

                        Category finalParent = parent;
                        // Hiển thị panel cha
                        CategoryParentPanel parentPanel = new CategoryParentPanel(parent);
                        parentPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                            public void mouseClicked(java.awt.event.MouseEvent evt) {
                                showCategoryDetails(finalParent);
                            }
                        });
                        contentPanel.add(parentPanel);

                        // Hiển thị panel con
                        if (!childrenToShow.isEmpty()) {
                            JPanel childContainer = new JPanel(new BorderLayout());
                            childContainer.setBackground(Color.WHITE);
                            childContainer.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                                BorderFactory.createEmptyBorder(15, 15, 15, 15)
                            ));

                            JPanel childPanel = new JPanel(new GridLayout(0, 4, 15, 15));
                            childPanel.setBackground(Color.WHITE);

                            for (Category child : childrenToShow) {
                                CategoryItemPanel childItem = new CategoryItemPanel(child);
                                childItem.addMouseListener(new java.awt.event.MouseAdapter() {
                                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                                        showCategoryDetails(child);
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

                            childContainer.add(childPanel, BorderLayout.CENTER);
                            contentPanel.add(childContainer);
                            contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JLabel lblError = new JLabel("Lỗi khi tìm kiếm: " + e.getMessage());
            lblError.setForeground(Color.RED);
            contentPanel.add(lblError);
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scrollPane);
        return wrapper;
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
        txtLoaiDM = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        lblLoaiDM = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        txtDMCha = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        lblDMCha = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        txtViTri = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        lblViTri = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        txtCapDo = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        lblCapDo = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        txtMoTa = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        lblMoTa = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        txtTrangThai = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        lblTrangThai = new javax.swing.JLabel();
        lblSearch = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        panelDanhMuc = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));

        panelThongTin.setBackground(new java.awt.Color(255, 255, 255));

        txtSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnThemMoi.setBackground(new java.awt.Color(40, 167, 69));
        btnThemMoi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnThemMoi.setForeground(new java.awt.Color(255, 255, 255));
        btnThemMoi.setText("Thêm mới");
        btnThemMoi.setPreferredSize(new java.awt.Dimension(88, 33));

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
        jLabel2.setText("Thông tin danh mục");

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTenDM, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
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

        txtLoaiDM.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtLoaiDM.setBorder(null);

        jLabel5.setBackground(new java.awt.Color(245, 245, 245));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Loại danh mục");

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLoaiDM, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLoaiDM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtLoaiDM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)))
                .addGap(15, 15, 15))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setPreferredSize(new java.awt.Dimension(310, 45));

        txtDMCha.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtDMCha.setBorder(null);

        jLabel6.setBackground(new java.awt.Color(245, 245, 245));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Danh mục cha");

        lblDMCha.setBackground(new java.awt.Color(245, 245, 245));
        lblDMCha.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDMCha.setPreferredSize(new java.awt.Dimension(20, 20));

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
                .addComponent(txtDMCha, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDMCha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtDMCha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)))
                .addGap(15, 15, 15))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setPreferredSize(new java.awt.Dimension(310, 45));

        txtViTri.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtViTri.setBorder(null);

        jLabel7.setBackground(new java.awt.Color(245, 245, 245));
        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Vị trí hiển thị");

        lblViTri.setBackground(new java.awt.Color(245, 245, 245));
        lblViTri.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblViTri.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblViTri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtViTri, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblViTri, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtViTri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)))
                .addGap(15, 15, 15))
        );

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setPreferredSize(new java.awt.Dimension(310, 45));

        txtCapDo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtCapDo.setBorder(null);

        jLabel8.setBackground(new java.awt.Color(245, 245, 245));
        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Cấp dộ");

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCapDo, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCapDo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCapDo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)))
                .addGap(15, 15, 15))
        );

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setPreferredSize(new java.awt.Dimension(310, 45));

        txtMoTa.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtMoTa.setBorder(null);

        jLabel9.setBackground(new java.awt.Color(245, 245, 245));
        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Mô tả");

        lblMoTa.setBackground(new java.awt.Color(245, 245, 245));
        lblMoTa.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblMoTa.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblMoTa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMoTa, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblMoTa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtMoTa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)))
                .addGap(15, 15, 15))
        );

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setPreferredSize(new java.awt.Dimension(310, 45));

        txtTrangThai.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtTrangThai.setBorder(null);

        jLabel11.setBackground(new java.awt.Color(245, 245, 245));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Trạng thái");

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTrangThai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11)))
                .addGap(15, 15, 15))
        );

        lblSearch.setBackground(new java.awt.Color(245, 245, 245));
        lblSearch.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSearch.setPreferredSize(new java.awt.Dimension(20, 20));

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
                        .addGroup(panelThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblAvatar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                            .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE))
                        .addGap(0, 28, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(panelThongTinLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnThemMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnHuyBo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThemMoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHuyBo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelDanhMucLayout = new javax.swing.GroupLayout(panelDanhMuc);
        panelDanhMuc.setLayout(panelDanhMucLayout);
        panelDanhMucLayout.setHorizontalGroup(
            panelDanhMucLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );
        panelDanhMucLayout.setVerticalGroup(
            panelDanhMucLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 588, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(panelDanhMuc);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelThongTin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelThongTin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void setupLabelIcons() {
        resizeLabelIcon(lblTenDM, "/resources/namecategory.png");
        resizeLabelIcon(lblSearch, "/resources/search.png");
        resizeLabelIcon(lblLoaiDM, "/resources/classify.png");
        resizeLabelIcon(lblDMCha, "/resources/originalcatalog.png");
        resizeLabelIcon(lblViTri, "/resources/priorit.png");
        resizeLabelIcon(lblCapDo, "/resources/adjusting.png");
        resizeLabelIcon(lblTrangThai, "/resources/status.png");
        resizeLabelIcon(lblMoTa, "/resources/presentation.png");
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
    
    private ImageIcon resizeIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
    
    private void lockAllFields() {
        txtTenDM.setEnabled(false);
        txtLoaiDM.setEnabled(false);
        txtDMCha.setEnabled(false);
        txtViTri.setEnabled(false);
        txtCapDo.setEnabled(false);
        txtMoTa.setEnabled(false);
        txtTrangThai.setEnabled(false);
        isEditMode = false;
    }

    private void unlockFieldsForEdit() {
        txtTenDM.setEnabled(true);
        txtLoaiDM.setEnabled(true);
        txtViTri.setEnabled(true);
        txtCapDo.setEnabled(true);
        txtMoTa.setEnabled(true);
        txtTrangThai.setEnabled(false);
        isEditMode = true;
    }
    
    private void chonIconDanhMuc() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn icon cho danh mục");

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Hình ảnh (*.png, *.jpg, *.jpeg, *.gif)", "png", "jpg", "jpeg", "gif");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedIconPath = selectedFile.getAbsolutePath();

            // Hiển thị preview icon
            try {
                ImageIcon icon = new ImageIcon(selectedIconPath);
                Image scaledImg = icon.getImage().getScaledInstance(56, 56, Image.SCALE_SMOOTH);
                lblAvatar.setIcon(new ImageIcon(scaledImg));
                lblAvatar.setText("");
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Không thể tải ảnh: " + e.getMessage());
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCapNhat;
    private javax.swing.JButton btnHuyBo;
    private javax.swing.JButton btnThemMoi;
    private javax.swing.JButton btnXoa;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAvatar;
    private javax.swing.JLabel lblCapDo;
    private javax.swing.JLabel lblDMCha;
    private javax.swing.JLabel lblLoaiDM;
    private javax.swing.JLabel lblMoTa;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblTenDM;
    private javax.swing.JLabel lblTrangThai;
    private javax.swing.JLabel lblViTri;
    private javax.swing.JPanel panelDanhMuc;
    private javax.swing.JPanel panelThongTin;
    private javax.swing.JTextField txtCapDo;
    private javax.swing.JTextField txtDMCha;
    private javax.swing.JTextField txtLoaiDM;
    private javax.swing.JTextField txtMoTa;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtTenDM;
    private javax.swing.JTextField txtTrangThai;
    private javax.swing.JTextField txtViTri;
    // End of variables declaration//GEN-END:variables
}
