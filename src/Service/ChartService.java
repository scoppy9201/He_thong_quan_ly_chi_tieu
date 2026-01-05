package Service;

import Model.Transaction;
import DAO.TransactionDAO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

public class ChartService {
    private final TransactionDAO transactionDAO;
    
    public ChartService() {
        this.transactionDAO = new TransactionDAO();
    }
    
    /**
     * Tạo biểu đồ tròn chi tiêu theo danh mục
     */
    public JPanel taoBieuDoTronChiTieu(int nguoiDungId, LocalDate tuNgay, LocalDate denNgay) {
        Map<String, Double> data = transactionDAO.thongKeTheoDateMuc(
            nguoiDungId, tuNgay, denNgay, Transaction.LoaiGiaoDich.CHI);
        
        DefaultPieDataset dataset = new DefaultPieDataset();
        data.forEach(dataset::setValue);
        
        JFreeChart chart = ChartFactory.createPieChart(
            "Chi tiêu theo danh mục",
            dataset,
            true,
            true,
            false
        );
        
        return new ChartPanel(chart);
    }
    
    /**
     * Tạo biểu đồ cột so sánh thu/chi
     */
    public JPanel taoBieuDoCotThuChi(int nguoiDungId, LocalDate tuNgay, LocalDate denNgay) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        double tongThu = transactionDAO.tinhTongSoTien(nguoiDungId, tuNgay, denNgay, Transaction.LoaiGiaoDich.THU);
        double tongChi = transactionDAO.tinhTongSoTien(nguoiDungId, tuNgay, denNgay, Transaction.LoaiGiaoDich.CHI);
        
        dataset.addValue(tongThu, "Thu nhập", "Tháng này");
        dataset.addValue(tongChi, "Chi tiêu", "Tháng này");
        
        JFreeChart chart = ChartFactory.createBarChart(
            "So sánh Thu - Chi",
            "Loại",
            "Số tiền (VNĐ)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        return new ChartPanel(chart);
    }
    
    /**
     * Hiển thị dialog biểu đồ
     */
    public void hienThiBieuDo(Component parent, JPanel chartPanel, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), title, true);
        dialog.setContentPane(chartPanel);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}