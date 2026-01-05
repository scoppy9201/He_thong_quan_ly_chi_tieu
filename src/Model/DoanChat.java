package Model;

import java.sql.Timestamp;

public class DoanChat {
    private int id;
    private int nguoiDungId;
    private String tieuDe;
    private boolean laGhim;
    private Integer thuTuGhim;
    private Timestamp ngayTao;
    private Timestamp ngayCapNhat;
    
    public DoanChat() {
        this.tieuDe = "Đoạn chat mới";
        this.laGhim = false;
    }
    
    public DoanChat(int nguoiDungId, String tieuDe) {
        this.nguoiDungId = nguoiDungId;
        this.tieuDe = tieuDe;
        this.laGhim = false;
    }

    // Getters and Setters
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

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public boolean isLaGhim() {
        return laGhim;
    }

    public void setLaGhim(boolean laGhim) {
        this.laGhim = laGhim;
    }

    public Integer getThuTuGhim() {
        return thuTuGhim;
    }

    public void setThuTuGhim(Integer thuTuGhim) {
        this.thuTuGhim = thuTuGhim;
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

    @Override
    public String toString() {
        return tieuDe;
    }
}