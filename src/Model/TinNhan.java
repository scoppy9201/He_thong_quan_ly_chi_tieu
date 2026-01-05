package Model;

import java.sql.Timestamp;

public class TinNhan {
    private int id;
    private int doanChatId;
    private VaiTro vaiTro;
    private String noiDung;
    private String metadata;
    private Timestamp ngayTao;
    
    public enum VaiTro {
        USER, ASSISTANT
    }
    
    public TinNhan() {
    }
    
    public TinNhan(int doanChatId, VaiTro vaiTro, String noiDung) {
        this.doanChatId = doanChatId;
        this.vaiTro = vaiTro;
        this.noiDung = noiDung;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDoanChatId() {
        return doanChatId;
    }

    public void setDoanChatId(int doanChatId) {
        this.doanChatId = doanChatId;
    }

    public VaiTro getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(VaiTro vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Timestamp getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Timestamp ngayTao) {
        this.ngayTao = ngayTao;
    }
}