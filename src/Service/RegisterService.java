/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import DAO.UserDAO;
import org.mindrot.jbcrypt.BCrypt;
import java.util.regex.Pattern;

public class RegisterService {

    private final UserDAO userDAO;

    public RegisterService() {
        this.userDAO = new UserDAO();
    }

    public enum RegisterResult {
        SUCCESS,
        INVALID_INPUT,
        INVALID_EMAIL_FORMAT,
        EMAIL_EXISTS,
        WEAK_PASSWORD,
        DB_ERROR
    }

    public static class RegisterResponse {
        public final RegisterResult result;
        public final String message;
        public RegisterResponse(RegisterResult r, String m) {
            this.result = r; this.message = m;
        }
    }

    // Chỉ chấp nhận email có đuôi @gmail.com
    private boolean isValidEmail(String email) {
        if (email == null) return false;
        String pattern = "^[A-Za-z0-9._%+-]+@gmail\\.com$";
        return Pattern.matches(pattern, email);
    }

    // Kiểm tra độ mạnh password
    private boolean isStrongPassword(String pw) {
        return pw != null && pw.length() >= 6;
    }

    // Hash password bằng BCrypt
    private String hashPassword(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(12));
    }

    // Hàm register chính
    public RegisterResponse register(String fullName, String email, String plainPassword) {
        // validate
        if (fullName == null || fullName.trim().isEmpty()
            || email == null || email.trim().isEmpty()
            || plainPassword == null || plainPassword.isEmpty()) {
            return new RegisterResponse(RegisterResult.INVALID_INPUT, "Vui lòng điền đầy đủ họ tên, email và mật khẩu.");
        }

        if (!isValidEmail(email)) {
            return new RegisterResponse(RegisterResult.INVALID_EMAIL_FORMAT, "Email không hợp lệ.");
        }

        if (!isStrongPassword(plainPassword)) {
            return new RegisterResponse(RegisterResult.WEAK_PASSWORD, "Mật khẩu quá yếu (tối thiểu 6 ký tự).");
        }

        // kiểm tra tồn tại
        if (userDAO.existsByEmail(email)) {
            return new RegisterResponse(RegisterResult.EMAIL_EXISTS, "Email đã được đăng ký.");
        }

        // hash password và lưu
        String hash = hashPassword(plainPassword);
        boolean ok = userDAO.createUser(fullName.trim(), email.trim().toLowerCase(), hash);
        if (ok) {
            return new RegisterResponse(RegisterResult.SUCCESS, "Đăng ký thành công. Vui lòng đăng nhập.");
        } else {
            return new RegisterResponse(RegisterResult.DB_ERROR, "Lỗi khi lưu thông tin. Thử lại.");
        }
    }
}
