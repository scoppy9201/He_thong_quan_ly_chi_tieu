/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

public class Transaction {
    private int id;
    private int nguoiDungId;
    private int danhMucId;
    private BigDecimal soTien;
    private LoaiGiaoDich loaiGiaoDich;
    private LocalDate ngayGiaoDich;
    private String phuongThuc;
    private String ghiChu;
    private Timestamp ngayTao;
    private Timestamp ngayCapNhat;
    private String tenDanhMuc;
    private String bieuTuongDanhMuc;
    private String anhHoaDon;

    public enum LoaiGiaoDich {
        THU, CHI
    }

    public Transaction() {}

    public Transaction(int id, int nguoiDungId, int danhMucId, BigDecimal soTien, 
                    LoaiGiaoDich loaiGiaoDich, LocalDate ngayGiaoDich,
                    String phuongThuc, String ghiChu, Timestamp ngayTao, Timestamp ngayCapNhat) {
        this.id = id;
        this.nguoiDungId = nguoiDungId;
        this.danhMucId = danhMucId;
        this.soTien = soTien;
        this.loaiGiaoDich = loaiGiaoDich;
        this.ngayGiaoDich = ngayGiaoDich;
        this.phuongThuc = phuongThuc;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getNguoiDungId() {
        return nguoiDungId;
    }
    public void setNguoiDungId(int nguoiDungId) {
        this.nguoiDungId = nguoiDungId;
    }

    public int getDanhMucId() {
        return danhMucId;
    }
    public void setDanhMucId(int danhMucId) {
        this.danhMucId = danhMucId;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }
    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public LoaiGiaoDich getLoaiGiaoDich() {
        return loaiGiaoDich;
    }
    public void setLoaiGiaoDich(LoaiGiaoDich loaiGiaoDich) {
        this.loaiGiaoDich = loaiGiaoDich;
    }

    public LocalDate getNgayGiaoDich() {
        return ngayGiaoDich;
    }
    public void setNgayGiaoDich(LocalDate ngayGiaoDich) {
        this.ngayGiaoDich = ngayGiaoDich;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }
    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public String getGhiChu() {
        return ghiChu;
    }
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public Timestamp getNgayTao() {
        return ngayTao;
    }
    public void setNgayTao(Timestamp ngayTao) {
        this.ngayTao = ngayTao;
    }

    public Timestamp getNgayCapNhat() {
        return ngayCapNhat;
    }
    public void setNgayCapNhat(Timestamp ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }
    public String getTenDanhMuc() {
        return tenDanhMuc;
    }
    
    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }
    
    public String getBieuTuongDanhMuc() {
        return bieuTuongDanhMuc;
    }
    
    public void setBieuTuongDanhMuc(String bieuTuongDanhMuc) {
        this.bieuTuongDanhMuc = bieuTuongDanhMuc;
    }
    
    public String getAnhHoaDon(){
        return anhHoaDon;
    }
    
    public void setAnhHoaDon(String anhHoaDon){
        this.anhHoaDon = anhHoaDon;
    }
}
