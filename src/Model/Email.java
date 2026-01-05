package Model;

import java.sql.Timestamp;

public class Email {
    private int id;
    private int nguoiDungId;
    private String tieuDe;
    private String noiDung;
    private String emailNguoiNhan;
    private LoaiEmail loaiEmail;
    private TrangThai trangThai;
    private boolean daGui;
    private boolean laGhim;
    private Timestamp ngayTao;
    private Timestamp ngayDoc;
    
    // Enum cho loại email
    public enum LoaiEmail {
        THONG_BAO_GIAO_DICH,
        CANH_BAO,
        BAO_CAO
    }
    
    // Enum cho trạng thái
    public enum TrangThai {
        CHUA_DOC,
        DA_DOC
    }
    
    // Constructor
    public Email() {
        this.trangThai = TrangThai.CHUA_DOC;
        this.daGui = false;
        this.laGhim = false;
    }
    
    public Email(int nguoiDungId, String tieuDe, String noiDung, String emailNguoiNhan, LoaiEmail loaiEmail) {
        this();
        this.nguoiDungId = nguoiDungId;
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.emailNguoiNhan = emailNguoiNhan;
        this.loaiEmail = loaiEmail;
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

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getEmailNguoiNhan() {
        return emailNguoiNhan;
    }

    public void setEmailNguoiNhan(String emailNguoiNhan) {
        this.emailNguoiNhan = emailNguoiNhan;
    }

    public LoaiEmail getLoaiEmail() {
        return loaiEmail;
    }

    public void setLoaiEmail(LoaiEmail loaiEmail) {
        this.loaiEmail = loaiEmail;
    }

    public TrangThai getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThai trangThai) {
        this.trangThai = trangThai;
    }

    public boolean isDaGui() {
        return daGui;
    }

    public void setDaGui(boolean daGui) {
        this.daGui = daGui;
    }

    public boolean isLaGhim() {
        return laGhim;
    }

    public void setLaGhim(boolean laGhim) {
        this.laGhim = laGhim;
    }

    public Timestamp getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Timestamp ngayTao) {
        this.ngayTao = ngayTao;
    }

    public Timestamp getNgayDoc() {
        return ngayDoc;
    }

    public void setNgayDoc(Timestamp ngayDoc) {
        this.ngayDoc = ngayDoc;
    }
}