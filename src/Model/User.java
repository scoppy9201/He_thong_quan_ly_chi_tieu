package Model;

import java.util.Date;

public class User {
    private int id;           
    private String hoTen;
    private String email;
    private String gioiTinh;
    private Date ngaySinh;

    // Constructor trống
    public User() {}

    // Constructor có ID
    public User(int id, String hoTen, String email, String gioiTinh, Date ngaySinh) {
        this.id = id;
        this.hoTen = hoTen;
        this.email = email;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
    }

    // getter và setter cho ID
    public int getId() { 
        return id; 
    }
    public void setId(int id) { 
        this.id = id; 
    }

    // getter và setter cho họ tên
    public String getHoTen() { 
        return hoTen; 
    }
    public void setHoTen(String hoTen) { 
        this.hoTen = hoTen; 
    }

    // getter và setter cho email
    public String getEmail() { 
        return email; 
    }
    public void setEmail(String email) { 
        this.email = email; 
    }

    // getter và setter cho giới tính
    public String getGioiTinh() { 
        return gioiTinh; 
    }
    public void setGioiTinh(String gioiTinh) { 
        this.gioiTinh = gioiTinh; 
    }

    // getter và setter cho ngày sinh
    public Date getNgaySinh() { 
        return ngaySinh; 
    }
    public void setNgaySinh(Date ngaySinh) { 
        this.ngaySinh = ngaySinh; 
    }
}
