/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import Service.LoginAttemptService;
import DAO.UserDAO;
import Model.User;
import org.mindrot.jbcrypt.BCrypt;
import java.util.regex.Pattern;

public class UserService {

    private final UserDAO userDAO;
    private final LoginAttemptService attemptService;

    public UserService() {
        this.userDAO = new UserDAO();
        this.attemptService = new LoginAttemptService();
    }

    public enum LoginResult {
        SUCCESS,
        INVALID_INPUT,
        INVALID_EMAIL_FORMAT,
        USER_NOT_FOUND,
        WRONG_PASSWORD,
        LOCKED
    }

    public static class LoginResponse {
        public final LoginResult result;
        public final String message;
        public LoginResponse(LoginResult result, String message) {
            this.result = result;
            this.message = message;
        }
    }

    // kiểm tra định dạng email: chỉ chấp nhận gmail.com
    private boolean isValidGmail(String email) {
        if (email == null) return false;
        String pattern = "^[A-Za-z0-9._%+-]+@gmail\\.com$";
        return Pattern.matches(pattern, email.toLowerCase());
    }

    // hàm login chính
    public LoginResponse login(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return new LoginResponse(LoginResult.INVALID_INPUT, "Email hoặc mật khẩu không được để trống.");
        }

        // kiểm tra khóa
        if (attemptService.isLocked(email)) {
            long remain = attemptService.getRemainingLockMillis(email);
            String msg = String.format("Tài khoản bị khóa. Vui lòng thử lại sau %d giây.", (remain/1000));
            return new LoginResponse(LoginResult.LOCKED, msg);
        }

        // kiểm tra định dạng gmail
        if (!isValidGmail(email)) {
            return new LoginResponse(LoginResult.INVALID_EMAIL_FORMAT, "Email phải là định dạng @gmail.com.");
        }

        // kiểm tra tồn tại user
        if (!userDAO.existsByEmail(email)) {
            return new LoginResponse(LoginResult.USER_NOT_FOUND, "Tài khoản không tồn tại.");
        }

        // lấy hash password từ DB và so sánh bằng BCrypt
        String storedHash = userDAO.getPasswordHashByEmail(email);
        if (storedHash == null) {
            return new LoginResponse(LoginResult.USER_NOT_FOUND, "Tài khoản không tồn tại.");
        }

        boolean ok = false;
        try {
            ok = BCrypt.checkpw(password, storedHash);
        } catch (Exception ex) {
            ex.printStackTrace();
            ok = false;
        }

        if (ok) {
            attemptService.resetAttempts(email);
            return new LoginResponse(LoginResult.SUCCESS, "Đăng nhập thành công.");
        } else {
            attemptService.recordFailedAttempt(email);
            int attempts = attemptService.getAttempts(email);
            int remaining = Math.max(0, 5 - attempts);
            if (attemptService.isLocked(email)) {
                long remain = attemptService.getRemainingLockMillis(email);
                String msg = String.format("Sai mật khẩu. Tài khoản bị khóa %d phút %d giây.",
                        (remain/1000)/60, (remain/1000)%60);
                return new LoginResponse(LoginResult.LOCKED, msg);
            }
            String msg = String.format("Sai mật khẩu. Còn %d lần thử.", remaining);
            return new LoginResponse(LoginResult.WRONG_PASSWORD, msg);
        }
    }
    
    public enum UpdateResult {
        SUCCESS,
        INVALID_INPUT,
        INVALID_EMAIL_FORMAT,
        USER_NOT_FOUND,
        FAILED
    }
    
    public static class UpdateResponse {
        public final UpdateResult result;
        public final String message;

        public UpdateResponse(UpdateResult result, String message) {
            this.result = result;
            this.message = message;
        }
    }
    
    // Hàm cập nhật thông tin user
    public UpdateResponse updateUserInfo(User user) {
        if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
            return new UpdateResponse(UpdateResult.INVALID_INPUT, "Dữ liệu không hợp lệ hoặc thiếu email.");
        }

        if (!isValidGmail(user.getEmail())) {
            return new UpdateResponse(UpdateResult.INVALID_EMAIL_FORMAT, "Email phải có định dạng @gmail.com.");
        }

        // Kiểm tra tồn tại user theo ID
        User existingUser = getUserById(user.getId());
        if (existingUser == null) {
            return new UpdateResponse(UpdateResult.USER_NOT_FOUND, "Không tìm thấy tài khoản cần cập nhật.");
        }

        boolean updated = userDAO.updateUser(user);
        if (updated) {
            return new UpdateResponse(UpdateResult.SUCCESS, "Cập nhật thông tin thành công.");
        } else {
            return new UpdateResponse(UpdateResult.FAILED, "Cập nhật thất bại. Vui lòng thử lại sau.");
        }
    }

    // Lấy thông tin user theo ID
    public User getUserById(int id) {
        if (id <= 0) return null;
        return userDAO.getUserById(id);
    }
    
    // Lấy thông tin user theo ID 
    public User getUserByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }
    
    public enum ChangePasswordResult {
        SUCCESS,
        USER_NOT_FOUND,
        WRONG_OLD_PASSWORD,
        MISMATCH,
        INVALID_INPUT,
        FAILED
    }

    public static class ChangePasswordResponse {
        public final ChangePasswordResult result;
        public final String message;

        public ChangePasswordResponse(ChangePasswordResult result, String message) {
            this.result = result;
            this.message = message;
        }
    }

    // Đổi mật khẩu
    public ChangePasswordResponse changePassword(int userId, String oldPass, String newPass, String confirmPass) {
        if (userId <= 0 || oldPass == null || newPass == null || confirmPass == null
                || oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            return new ChangePasswordResponse(ChangePasswordResult.INVALID_INPUT, "Vui lòng nhập đầy đủ thông tin.");
        }

        if (!newPass.equals(confirmPass)) {
            return new ChangePasswordResponse(ChangePasswordResult.MISMATCH, "Mật khẩu xác nhận không khớp.");
        }

        User user = userDAO.getUserById(userId);
        if (user == null) {
            return new ChangePasswordResponse(ChangePasswordResult.USER_NOT_FOUND, "Không tìm thấy người dùng.");
        }

        // Lấy mật khẩu hash cũ từ DB
        String storedHash = userDAO.getPasswordHashByEmail(user.getEmail());
        if (storedHash == null || !BCrypt.checkpw(oldPass, storedHash)) {
            return new ChangePasswordResponse(ChangePasswordResult.WRONG_OLD_PASSWORD, "Mật khẩu hiện tại không đúng.");
        }

        // Hash mật khẩu mới
        String newHash = BCrypt.hashpw(newPass, BCrypt.gensalt(12));
        boolean updated = userDAO.updatePassword(userId, newHash);

        if (updated) {
            return new ChangePasswordResponse(ChangePasswordResult.SUCCESS, "Đổi mật khẩu thành công.");
        } else {
            return new ChangePasswordResponse(ChangePasswordResult.FAILED, "Đổi mật khẩu thất bại. Vui lòng thử lại sau.");
        }
    }
}

