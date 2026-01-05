/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

public class Category {

    private int id;
    private String tenDanhMuc;
    private String loaiDanhMuc;     
    private Integer danhMucChaId;   
    private String bieuTuong;       
    private int thuTuHienThi;
    private int capDo;              
    private String trangThai;      
    private String moTa;
    private String ngayTao;
    private String ngayCapNhat;

    // Constructor rỗng
    public Category() {}

    // Constructor đủ
    public Category(int id, String tenDanhMuc, String loaiDanhMuc, Integer danhMucChaId,
                    String bieuTuong, int thuTuHienThi, int capDo, String trangThai,
                    String moTa, String ngayTao, String ngayCapNhat) {
        this.id = id;
        this.tenDanhMuc = tenDanhMuc;
        this.loaiDanhMuc = loaiDanhMuc;
        this.danhMucChaId = danhMucChaId;
        this.bieuTuong = bieuTuong;
        this.thuTuHienThi = thuTuHienThi;
        this.capDo = capDo;
        this.trangThai = trangThai;
        this.moTa = moTa;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    
    public enum LoaiDanhMuc {
        THU, CHI
    }
    
    public enum TrangThai {
        ACTIVE, INACTIVE
    }

    // Getter – Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTenDanhMuc() { return tenDanhMuc; }
    public void setTenDanhMuc(String tenDanhMuc) { this.tenDanhMuc = tenDanhMuc; }

    public String getLoaiDanhMuc() { return loaiDanhMuc; }
    public void setLoaiDanhMuc(String loaiDanhMuc) { this.loaiDanhMuc = loaiDanhMuc; }

    public Integer getDanhMucChaId() { return danhMucChaId; }
    public void setDanhMucChaId(Integer danhMucChaId) { this.danhMucChaId = danhMucChaId; }

    public String getBieuTuong() { return bieuTuong; }
    public void setBieuTuong(String bieuTuong) { this.bieuTuong = bieuTuong; }

    public int getThuTuHienThi() { return thuTuHienThi; }
    public void setThuTuHienThi(int thuTuHienThi) { this.thuTuHienThi = thuTuHienThi; }

    public int getCapDo() { return capDo; }
    public void setCapDo(int capDo) { this.capDo = capDo; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getNgayTao() { return ngayTao; }
    public void setNgayTao(String ngayTao) { this.ngayTao = ngayTao; }

    public String getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(String ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }
}
