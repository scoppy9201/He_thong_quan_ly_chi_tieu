/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI;
import Service.TransactionService;
import Service.UserService;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.openpdf.text.*;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.PdfWriter;

public class panelBaoCaoThongKe extends javax.swing.JPanel {

    private TransactionService transactionService;
    private int userId;
    private PieChartPanel pieChartPanel;
    private BarChartPanel barChartPanel;
    private MonthlyStatsPanel monthlyStatsPanel;
    private YearlyStatsPanel yearlyStatsPanel;

    public panelBaoCaoThongKe(int userId) {
        this.transactionService = new TransactionService();
        this.userId = userId;
        initComponents();
        SwingUtilities.invokeLater(() -> {
            setupLabelIcons();
            setupScrollPane();
            setupFilterComboBoxes();
            setupCustomComponents();
            setupEventListeners();
            loadDefaultData();
        });
    }
    
    private void setupScrollPane() {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(mainContainerPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    // Setup các combo box filter (tháng, năm, chế độ)
    private void setupFilterComboBoxes() {
        // ComboBox tháng: 1-12
        jcbThang.removeAllItems();
        for (int i = 1; i <= 12; i++) {
            jcbThang.addItem("Tháng " + i);
        }
        // ComboBox năm: từ 2020 đến hiện tại
        jcbNam.removeAllItems();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear; i >= 2020; i--) {
            jcbNam.addItem("Năm " + i);
        }
        jcbNam.setSelectedIndex(0);
        
        // ComboBox chế độ
        jcbCheDo.removeAllItems();
        jcbCheDo.addItem("Theo Tháng");
        jcbCheDo.addItem("Theo Năm");
        jcbCheDo.addItem("Toàn Năm");
        jcbCheDo.setSelectedItem("Toàn Năm");
        // Set cursor cho labels
        lblLoc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblXuatFile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Enable/disable tháng theo chế độ
        jcbCheDo.addActionListener(e -> {
            String mode = (String) jcbCheDo.getSelectedItem();
            jcbThang.setEnabled("Theo Tháng".equals(mode));
        });
    }

    // Setup biểu đồ và thống kê
    private void setupCustomComponents() {
        Piechar.removeAll();
        Barchar.removeAll();
        panelThongKeThang.removeAll();
        panelThongKeNam.removeAll();
        Piechar.setLayout(new BorderLayout());
        Barchar.setLayout(new BorderLayout());
        panelThongKeThang.setLayout(new BorderLayout());
        panelThongKeNam.setLayout(new BorderLayout());
        pieChartPanel = new PieChartPanel();
        Piechar.add(pieChartPanel, BorderLayout.CENTER);
        barChartPanel = new BarChartPanel();
        Barchar.add(barChartPanel, BorderLayout.CENTER);
        monthlyStatsPanel = new MonthlyStatsPanel();
        panelThongKeThang.add(monthlyStatsPanel, BorderLayout.CENTER);
        yearlyStatsPanel = new YearlyStatsPanel();
        panelThongKeNam.add(yearlyStatsPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    // Setup event listeners cho labels (Lọc, Reset, Xuất PDF)
    private void setupEventListeners() {
        // Event cho Lọc
        lblLoc.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                applyFilter();
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblLoc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 123, 255), 2));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblLoc.setBorder(null);
            }
        });
        
        // Event cho Reset
        lblReset.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resetFilter();
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblReset.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 53, 69), 2));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblReset.setBorder(null);
            }
        });
        
        // Event cho Xuất PDF 
        lblXuatFile.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exportToPdf();
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblXuatFile.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 128, 0), 2));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblXuatFile.setBorder(null);
            }
        });
    }
    
    // Load dữ liệu mặc định khi khởi tạo
    private void loadDefaultData() {
        applyFilter();
    }
    
    // Lấy tháng được chọn (1-12)
    private int getSelectedMonth() {
        return jcbThang.getSelectedIndex() + 1;
    }
    
    // Lấy năm được chọn
    private int getSelectedYear() {
        String yearStr = (String) jcbNam.getSelectedItem();
        return Integer.parseInt(yearStr.replace("Năm ", ""));
    }
    
    // Lấy chế độ được chọn
    private String getSelectedMode() {
        return (String) jcbCheDo.getSelectedItem();
    }
    
    // Hàm áp dụng filter 
    private void applyFilter() {
        int month = getSelectedMonth();
        int year = getSelectedYear();
        String mode = getSelectedMode();
        loadDataByMode(month, year, mode);
    }
    
    // Reset filter về mặc định
    private void resetFilter() {
        jcbThang.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        jcbNam.setSelectedIndex(0);
        jcbCheDo.setSelectedItem("Toàn Năm");
        applyFilter();
    }
    
    private void loadDataByMode(int month, int year, String mode) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                switch (mode) {
                    case "Theo Tháng" -> loadMonthlyData(month, year);
                    case "Theo Năm" -> loadYearlyData(year);
                    case "Toàn Năm" -> loadFullYearData(year);
                }
                return null;
            }
            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                revalidate();
                repaint();
            }
        };
        worker.execute();
    }
    
    // Load dữ liệu theo tháng 
    private void loadMonthlyData(int month, int year) {
        Map<String, Double> expenseByCategory = transactionService.getExpenseByCategory(userId, month, year);
        SwingUtilities.invokeLater(() -> {
            pieChartPanel.updateData(expenseByCategory);
            pieChartPanel.setTitle("Cơ cấu chi tiêu - Tháng " + month + "/" + year);
        });
        Map<Integer, Double> expenseTrend = transactionService.getExpenseTrendByMonth(userId, year);
        SwingUtilities.invokeLater(() -> {
            barChartPanel.updateData(expenseTrend);
            barChartPanel.setTitle("Xu hướng chi tiêu năm " + year);
        });
        Map<String, Object> monthlyStats = transactionService.getMonthlyStats(userId, month, year);
        SwingUtilities.invokeLater(() -> {
            monthlyStatsPanel.updateData(monthlyStats);
            panelThongKeThang.setVisible(true);
            panelThongKeNam.setVisible(false);
        });
    }
    
    // Load dữ liệu theo năm 
    private void loadYearlyData(int year) {
        Map<String, Double> yearlyExpense = getYearlyExpenseByCategory(year);
        SwingUtilities.invokeLater(() -> {
            pieChartPanel.updateData(yearlyExpense);
            pieChartPanel.setTitle("Cơ cấu chi tiêu năm " + year);
        });
        Map<Integer, Double> expenseTrend = transactionService.getExpenseTrendByMonth(userId, year);
        SwingUtilities.invokeLater(() -> {
            barChartPanel.updateData(expenseTrend);
            barChartPanel.setTitle("Xu hướng chi tiêu năm " + year);
        });
        Map<String, Object> yearlyStats = transactionService.getYearlyStats(userId, year);
        SwingUtilities.invokeLater(() -> {
            yearlyStatsPanel.updateData(yearlyStats);
            panelThongKeNam.setVisible(true);
            panelThongKeThang.setVisible(false);
        });
    }
    
    // Load dữ liệu toàn năm 
    private void loadFullYearData(int year) {
        loadYearlyData(year);
        int currentMonth = getSelectedMonth();
        Map<String, Object> monthlyStats = transactionService.getMonthlyStats(userId, currentMonth, year);
        SwingUtilities.invokeLater(() -> {
            monthlyStatsPanel.updateData(monthlyStats);
            panelThongKeThang.setVisible(true);
            panelThongKeNam.setVisible(true);
        });
    }
    
    // tinh tổng chi tiêu theo danh mục cho cả năm 
    private Map<String, Double> getYearlyExpenseByCategory(int year) {
        Map<String, Double> result = new java.util.HashMap<>();
        for (int month = 1; month <= 12; month++) {
            Map<String, Double> monthData = transactionService.getExpenseByCategory(userId, month, year);
            monthData.forEach((key, value) -> result.merge(key, value, Double::sum));
        }
        return result;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainContainerPanel = new javax.swing.JPanel();
        panelThongKeThang = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        panelThongKeNam = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        Barchar = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        Piechar = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        panelFilter = new javax.swing.JPanel();
        jcbThang = new javax.swing.JComboBox<>();
        lblLGD = new javax.swing.JLabel();
        lblLGD1 = new javax.swing.JLabel();
        jcbNam = new javax.swing.JComboBox<>();
        jcbCheDo = new javax.swing.JComboBox<>();
        lblLGD2 = new javax.swing.JLabel();
        lblLoc = new javax.swing.JLabel();
        lblReset = new javax.swing.JLabel();
        lblXuatFile = new javax.swing.JLabel();

        panelThongKeThang.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Thống kê chi tiêu tháng ");

        javax.swing.GroupLayout panelThongKeThangLayout = new javax.swing.GroupLayout(panelThongKeThang);
        panelThongKeThang.setLayout(panelThongKeThangLayout);
        panelThongKeThangLayout.setHorizontalGroup(
            panelThongKeThangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThongKeThangLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelThongKeThangLayout.setVerticalGroup(
            panelThongKeThangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThongKeThangLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(340, Short.MAX_VALUE))
        );

        panelThongKeNam.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Thống kê chi tiêu năm");

        javax.swing.GroupLayout panelThongKeNamLayout = new javax.swing.GroupLayout(panelThongKeNam);
        panelThongKeNam.setLayout(panelThongKeNamLayout);
        panelThongKeNamLayout.setHorizontalGroup(
            panelThongKeNamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThongKeNamLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(457, Short.MAX_VALUE))
        );
        panelThongKeNamLayout.setVerticalGroup(
            panelThongKeNamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThongKeNamLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Barchar.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Xu hướng chi tiêu");

        javax.swing.GroupLayout BarcharLayout = new javax.swing.GroupLayout(Barchar);
        Barchar.setLayout(BarcharLayout);
        BarcharLayout.setHorizontalGroup(
            BarcharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BarcharLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        BarcharLayout.setVerticalGroup(
            BarcharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BarcharLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        Piechar.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Cơ cấu chi tiêu");

        javax.swing.GroupLayout PiecharLayout = new javax.swing.GroupLayout(Piechar);
        Piechar.setLayout(PiecharLayout);
        PiecharLayout.setHorizontalGroup(
            PiecharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PiecharLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PiecharLayout.setVerticalGroup(
            PiecharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PiecharLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(398, Short.MAX_VALUE))
        );

        panelFilter.setBackground(new java.awt.Color(255, 255, 255));

        jcbThang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbThang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbThangActionPerformed(evt);
            }
        });

        lblLGD.setBackground(new java.awt.Color(245, 245, 245));
        lblLGD.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblLGD.setPreferredSize(new java.awt.Dimension(20, 20));

        lblLGD1.setBackground(new java.awt.Color(245, 245, 245));
        lblLGD1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblLGD1.setPreferredSize(new java.awt.Dimension(20, 20));

        jcbNam.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jcbCheDo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblLGD2.setBackground(new java.awt.Color(245, 245, 245));
        lblLGD2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblLGD2.setPreferredSize(new java.awt.Dimension(20, 20));

        lblLoc.setBackground(new java.awt.Color(245, 245, 245));
        lblLoc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblLoc.setPreferredSize(new java.awt.Dimension(20, 20));

        lblReset.setBackground(new java.awt.Color(245, 245, 245));
        lblReset.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblReset.setPreferredSize(new java.awt.Dimension(20, 20));

        lblXuatFile.setBackground(new java.awt.Color(255, 255, 255));
        lblXuatFile.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblXuatFile.setText("Xuất file PDF");

        javax.swing.GroupLayout panelFilterLayout = new javax.swing.GroupLayout(panelFilter);
        panelFilter.setLayout(panelFilterLayout);
        panelFilterLayout.setHorizontalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblLGD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbThang, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblLGD1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbNam, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblLGD2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbCheDo, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(lblReset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblXuatFile, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(56, Short.MAX_VALUE))
        );
        panelFilterLayout.setVerticalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addContainerGap(7, Short.MAX_VALUE)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(lblLGD1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblLGD2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jcbCheDo, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jcbNam, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblReset, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblXuatFile, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblLGD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcbThang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainContainerPanelLayout = new javax.swing.GroupLayout(mainContainerPanel);
        mainContainerPanel.setLayout(mainContainerPanelLayout);
        mainContainerPanelLayout.setHorizontalGroup(
            mainContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(mainContainerPanelLayout.createSequentialGroup()
                .addGroup(mainContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Piechar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelThongKeThang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Barchar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelThongKeNam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainContainerPanelLayout.setVerticalGroup(
            mainContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainContainerPanelLayout.createSequentialGroup()
                .addComponent(panelFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Piechar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Barchar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelThongKeThang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelThongKeNam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainContainerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainContainerPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jcbThangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbThangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jcbThangActionPerformed

    private void setupLabelIcons() {
        resizeLabelIcon(lblReset, "/resources/rotate.png");
        resizeLabelIcon(lblLoc, "/resources/setting.png");
        resizeLabelIcon(lblLGD, "/resources/month.png");
        resizeLabelIcon(lblLGD1, "/resources/year.png");
        resizeLabelIcon(lblLGD2, "/resources/databse.png");
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
    
    private void exportToPdf() {
    try {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu báo cáo PDF");
        chooser.setSelectedFile(new File("BaoCao_" + getSelectedYear() + "_" + getSelectedMonth() + ".pdf"));
        
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;
        
        String filePath = chooser.getSelectedFile().getAbsolutePath();
        if (!filePath.toLowerCase().endsWith(".pdf")) {
            filePath += ".pdf";
        }
        
        // Lấy nội dung và xuất PDF
        String content = getBaoCaoText();
        boolean success = exportPdf(filePath, content);
        
        JOptionPane.showMessageDialog(this,
            success ? "Xuất PDF thành công!\n" + filePath : "Xuất PDF thất bại!",
            success ? "Thành công" : "Lỗi",
            success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Lỗi xuất PDF: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

    // Hàm kéo dữ liệu thống kê vào báo cáo 
    public String getBaoCaoText() {
        int month = getSelectedMonth();
        int year = getSelectedYear();
        String mode = getSelectedMode();

        StringBuilder content = new StringBuilder();
        content.append("== BÁO CÁO THỐNG KÊ GIAO DỊCH ==\n\n");
        content.append("Chế độ: ").append(mode).append("\n");
        content.append("Tháng: ").append(month).append(" | Năm: ").append(year).append("\n");
        content.append("Ngày xuất: ").append(java.time.LocalDate.now()).append("\n");
        content.append("==========================================\n\n");

        // thống kê theo tháng 
        if (panelThongKeThang.isVisible() && monthlyStatsPanel != null) {
            content.append("[ THỐNG KÊ THÁNG ").append(month).append("/").append(year).append(" ]\n");
            Map<String, Object> stats = monthlyStatsPanel.getData();
            if (stats != null) {
                content.append("Tổng thu: ").append(formatMoney(stats.get("TongThu"))).append(" VNĐ\n");
                content.append("Tổng chi: ").append(formatMoney(stats.get("TongChi"))).append(" VNĐ\n");
                content.append("Số dư: ").append(formatMoney(stats.get("SoDu"))).append(" VNĐ\n");
                content.append("Số giao dịch: ").append(stats.get("SoGiaoDich")).append("\n\n");
            }
        }

        // thống kê theo năm 
        if (panelThongKeNam.isVisible() && yearlyStatsPanel != null) {
            content.append("[ THỐNG KÊ NĂM ").append(year).append(" ]\n");
            Map<String, Object> stats = yearlyStatsPanel.getData();
            if (stats != null) {
                content.append("Tổng thu năm: ").append(formatMoney(stats.get("TongThuNam"))).append(" VNĐ\n");
                content.append("Tổng chi năm: ").append(formatMoney(stats.get("TongChiNam"))).append(" VNĐ\n");
                content.append("Số dư năm: ").append(formatMoney(stats.get("SoDu"))).append(" VNĐ\n");
                content.append("TB chi/tháng: ").append(formatMoney(stats.get("TrungBinhChiMoiThang"))).append(" VNĐ\n");
                content.append("Tháng chi nhiều nhất: Tháng ").append(stats.get("ThangChiLonNhat")).append("\n\n");
            }
        }

        // chi tiêu theo danh mục (từ Pie Chart)
        if (pieChartPanel != null) {
            content.append("[ CƠ CẤU CHI TIÊU THEO DANH MỤC ]\n");
            Map<String, Double> categoryData = pieChartPanel.getData();
            if (categoryData != null && !categoryData.isEmpty()) {
                double total = categoryData.values().stream().mapToDouble(Double::doubleValue).sum();
                categoryData.entrySet().stream()
                    .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                    .forEach(entry -> {
                        double percent = (entry.getValue() / total) * 100;
                        content.append(String.format("- %s: %s VNĐ (%.1f%%)\n", 
                            entry.getKey(), formatMoney(entry.getValue()), percent));
                    });
                content.append("\n");
            }
        }

        // xu hướng chi tiêu (từ Bar Chart)
        if (barChartPanel != null) {
            content.append("[ XU HƯỚNG CHI TIÊU THEO THÁNG ]\n");
            Map<Integer, Double> trendData = barChartPanel.getData();
            if (trendData != null && !trendData.isEmpty()) {
                trendData.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        content.append(String.format("Tháng %d: %s VNĐ\n", 
                            entry.getKey(), formatMoney(entry.getValue())));
                    });
                content.append("\n");
            }
        }

        // top 5 giao dịch chi tiêu lớn nhất 
        content.append("[ TOP 5 CHI TIÊU LỚN NHẤT ]\n");
        var topExpenses = transactionService.getTopExpenses(userId, 5);
        if (topExpenses != null && !topExpenses.isEmpty()) {
            int rank = 1;
            for (var trans : topExpenses) {
                content.append(String.format("%d. %s - %s VNĐ (%s)\n", 
                    rank++, 
                    trans.getTenDanhMuc(), 
                    formatMoney(trans.getSoTien()), 
                    trans.getNgayGiaoDich()));
            }
        } else {
            content.append("Chưa có dữ liệu\n");
        }

        content.append("\n=========================================\n");
        content.append("Báo cáo được tạo tự động bởi hệ thống\n");

        return content.toString();
    }

    // Hàm xuất file PDF (hỗ trợ tiếng việt)
    public boolean exportPdf(String filePath, String content) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Font hỗ trợ tiếng Việt
            BaseFont bf = BaseFont.createFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font normalFont = new Font(bf, 11);
            Font titleFont = new Font(bf, 16, Font.BOLD);
            Font headerFont = new Font(bf, 12, Font.BOLD);

            // Tiêu đề
            Paragraph title = new Paragraph("BÁO CÁO THỐNG KÊ GIAO DỊCH", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ", normalFont));

            // Nội dung
            String[] lines = content.split("\n");
            for (String line : lines) {
                if (line.startsWith("[")) {
                    document.add(new Paragraph(line, headerFont));
                } else {
                    document.add(new Paragraph(line, normalFont));
                }
            }

            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hàm format tiền 
    private String formatMoney(Object value) {
        if (value == null) return "0";
        try {
            double amount = value instanceof Double ? (Double) value : 
                           value instanceof Integer ? ((Integer) value).doubleValue() :
                           Double.parseDouble(value.toString());
            return String.format("%,.0f", amount);
        } catch (Exception e) {
            return value.toString();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Barchar;
    private javax.swing.JPanel Piechar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JComboBox<String> jcbCheDo;
    private javax.swing.JComboBox<String> jcbNam;
    private javax.swing.JComboBox<String> jcbThang;
    private javax.swing.JLabel lblLGD;
    private javax.swing.JLabel lblLGD1;
    private javax.swing.JLabel lblLGD2;
    private javax.swing.JLabel lblLoc;
    private javax.swing.JLabel lblReset;
    private javax.swing.JLabel lblXuatFile;
    private javax.swing.JPanel mainContainerPanel;
    private javax.swing.JPanel panelFilter;
    private javax.swing.JPanel panelThongKeNam;
    private javax.swing.JPanel panelThongKeThang;
    // End of variables declaration//GEN-END:variables
}
