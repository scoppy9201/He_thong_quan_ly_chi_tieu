/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.math.BigDecimal;
import java.sql.Timestamp; 

/**
 *
 * @author Admin
 */
public class Budget {
    private int id;
    private int nguoiDungId;
    private Integer danhMucId;
    private String tenDanhMuc;
    private String loaiDanhMuc;
    private BigDecimal tongNganSach;
    private BigDecimal daDung;
    private BigDecimal conLai;
    private String kyHan; // Tháng/Quý/Năm
    private Timestamp ngayBatDau;
    private Timestamp ngayKetThuc;
    private String trangThai;
    private String ghiChu;
    private Timestamp ngayTao;
    private Timestamp ngayCapNhat;
    
    // Thông tin bổ sung từ JOIN
    private String bieuTuongDanhMuc;
    private Integer danhMucChaId;
    private String tenDanhMucCha;
    
    public Budget() {}
    
    public Budget(int nguoiDungId, Integer danhMucId, String tenDanhMuc, 
                  String loaiDanhMuc, BigDecimal tongNganSach) {
        this.nguoiDungId = nguoiDungId;
        this.danhMucId = danhMucId;
        this.tenDanhMuc = tenDanhMuc;
        this.loaiDanhMuc = loaiDanhMuc;
        this.tongNganSach = tongNganSach;
        this.daDung = BigDecimal.ZERO;
        this.conLai = tongNganSach;
        this.kyHan = "Tháng";
        this.trangThai = "ACTIVE";
    }
    
    // Tính phần trăm đã dùng
    public double getPhanTramDaDung() {
        if (tongNganSach == null || tongNganSach.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return daDung.divide(tongNganSach, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(100)).doubleValue();
    }
    
    // Kiểm tra vượt ngân sách
    public boolean isVuotNganSach() {
        return daDung.compareTo(tongNganSach) > 0;
    }
    
    // Cập nhật số dư
   // Cập nhật số dư
    public void updateConLai() {
    if (this.tongNganSach == null) {
        this.tongNganSach = BigDecimal.ZERO;
    }
    if (this.daDung == null) {
        this.daDung = BigDecimal.ZERO;
    }
    this.conLai = this.tongNganSach.subtract(this.daDung);
}
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getNguoiDungId() { return nguoiDungId; }
    public void setNguoiDungId(int nguoiDungId) { this.nguoiDungId = nguoiDungId; }
    
    public Integer getDanhMucId() { return danhMucId; }
    public void setDanhMucId(Integer danhMucId) { this.danhMucId = danhMucId; }
    
    public String getTenDanhMuc() { return tenDanhMuc; }
    public void setTenDanhMuc(String tenDanhMuc) { this.tenDanhMuc = tenDanhMuc; }
    
    public String getLoaiDanhMuc() { return loaiDanhMuc; }
    public void setLoaiDanhMuc(String loaiDanhMuc) { this.loaiDanhMuc = loaiDanhMuc; }
    
    public BigDecimal getTongNganSach() { return tongNganSach; }
    public void setTongNganSach(BigDecimal tongNganSach) { 
        this.tongNganSach = tongNganSach;
        updateConLai();
    }
    
    public BigDecimal getDaDung() { return daDung; }
    public void setDaDung(BigDecimal daDung) { 
        this.daDung = daDung;
        updateConLai();
    }
    
    public BigDecimal getConLai() { return conLai; }
    public void setConLai(BigDecimal conLai) { this.conLai = conLai; }
    
    public String getKyHan() { return kyHan; }
    public void setKyHan(String kyHan) { this.kyHan = kyHan; }
    
    public Timestamp getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(Timestamp ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    
    public Timestamp getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(Timestamp ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }
    
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    
    public Timestamp getNgayTao() { return ngayTao; }
    public void setNgayTao(Timestamp ngayTao) { this.ngayTao = ngayTao; }
    
    public Timestamp getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(Timestamp ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }
    
    public String getBieuTuongDanhMuc() { return bieuTuongDanhMuc; }
    public void setBieuTuongDanhMuc(String bieuTuongDanhMuc) { this.bieuTuongDanhMuc = bieuTuongDanhMuc; }
    
    public Integer getDanhMucChaId() { return danhMucChaId; }
    public void setDanhMucChaId(Integer danhMucChaId) { this.danhMucChaId = danhMucChaId; }
    
    public String getTenDanhMucCha() { return tenDanhMucCha; }
    public void setTenDanhMucCha(String tenDanhMucCha) { this.tenDanhMucCha = tenDanhMucCha; }
}
