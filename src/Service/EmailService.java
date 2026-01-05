    package Service;

    import DAO.EmailDAO;
    import Model.Email;
    import Model.Transaction;
    import Model.User;
    import jakarta.mail.*;
    import jakarta.mail.internet.*;
    import jakarta.activation.DataHandler;
    import java.util.List;
    import java.util.Properties;

    public class EmailService {
        private final EmailDAO emailDAO;

        // Cấu hình Gmail SMTP
        private static final String SMTP_HOST = "smtp.gmail.com";
        private static final String SMTP_PORT = "587";
        private static final String EMAIL_FROM = "Dnam16092005@gmail.com";
        private static final String EMAIL_PASSWORD = "prnl pgfv mrdp jxlf"; 

        public EmailService() {
            this.emailDAO = new EmailDAO();
        }

        // Gửi email thông báo giao dịch
        public boolean sendTransactionNotification(User user, Transaction transaction) {
            try {
                // Tạo nội dung email
                String subject = "Thông báo: Giao dịch mới - " + transaction.getLoaiGiaoDich();
                String content = buildTransactionEmailContent(user, transaction);

                // Tạo object Email để lưu vào DB
                Email email = new Email(
                    user.getId(),
                    subject,
                    content,
                    user.getEmail(),
                    Email.LoaiEmail.THONG_BAO_GIAO_DICH
                );

                // Gửi email qua SMTP
                boolean emailSent = sendEmail(user.getEmail(), subject, content);
                email.setDaGui(emailSent);

                // Lưu vào database
                boolean saved = emailDAO.insert(email);

                return saved;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        // Gửi email khi sửa giao dịch
    public boolean sendTransactionUpdateNotification(User user, Transaction transaction) {
        try {
            String subject = "Thông báo: Giao dịch đã được cập nhật";
            String content = buildTransactionUpdateEmailContent(user, transaction);

            Email email = new Email(
                user.getId(),
                subject,
                content,
                user.getEmail(),
                Email.LoaiEmail.THONG_BAO_GIAO_DICH
            );

            boolean emailSent = sendEmail(user.getEmail(), subject, content);
            email.setDaGui(emailSent);

            return emailDAO.insert(email);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Gửi email khi xóa giao dịch
    public boolean sendTransactionDeleteNotification(User user, Transaction transaction) {
        try {
            String subject = "Thông báo: Giao dịch đã bị xóa";
            String content = buildTransactionDeleteEmailContent(user, transaction);

            Email email = new Email(
                user.getId(),
                subject,
                content,
                user.getEmail(),
                Email.LoaiEmail.THONG_BAO_GIAO_DICH
            );

            boolean emailSent = sendEmail(user.getEmail(), subject, content);
            email.setDaGui(emailSent);

            return emailDAO.insert(email);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

        // Xây dựng nội dung email giao dịch
        private String buildTransactionEmailContent(User user, Transaction transaction) {
            StringBuilder sb = new StringBuilder();
            sb.append("Xin chào ").append(user.getHoTen()).append(",\n\n");
            sb.append("Bạn vừa thêm một giao dịch mới:\n\n");
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append("Loại: ").append(transaction.getLoaiGiaoDich()).append("\n");
            sb.append("Số tiền: ").append(String.format("%,.0f đ", transaction.getSoTien())).append("\n");
            sb.append("Danh mục: ").append(transaction.getTenDanhMuc()).append("\n");
            sb.append("Ngày: ").append(transaction.getNgayGiaoDich()).append("\n");
            sb.append("Phương thức: ").append(transaction.getPhuongThuc()).append("\n");

            if (transaction.getGhiChu() != null && !transaction.getGhiChu().isEmpty()) {
                sb.append("Ghi chú: ").append(transaction.getGhiChu()).append("\n");
            }

            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            sb.append("Cảm ơn bạn đã sử dụng Hệ thống Quản lý Chi tiêu!\n\n");
            sb.append("Trân trọng,\n");
            sb.append("Đội ngũ phát triển");

            return sb.toString();
        }

        private String buildTransactionUpdateEmailContent(User user, Transaction transaction) {
        StringBuilder sb = new StringBuilder();
        sb.append("Xin chào ").append(user.getHoTen()).append(",\n\n");
        sb.append("Một giao dịch của bạn đã được CẬP NHẬT:\n\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("Loại: ").append(transaction.getLoaiGiaoDich()).append("\n");
        sb.append("Số tiền mới: ").append(String.format("%,.0f đ", transaction.getSoTien())).append("\n");
        sb.append("Danh mục: ").append(transaction.getTenDanhMuc()).append("\n");
        sb.append("Ngày: ").append(transaction.getNgayGiaoDich()).append("\n");
        sb.append("Phương thức: ").append(transaction.getPhuongThuc()).append("\n");

        if (transaction.getGhiChu() != null && !transaction.getGhiChu().isEmpty()) {
            sb.append("Ghi chú: ").append(transaction.getGhiChu()).append("\n");
        }

        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        sb.append("Vui lòng kiểm tra lại thông tin nếu cần.\n\n");
        sb.append("Trân trọng,\n");
        sb.append("Đội ngũ phát triển");

        return sb.toString();
    }

    // Nội dung email khi xóa giao dịch
    private String buildTransactionDeleteEmailContent(User user, Transaction transaction) {
        StringBuilder sb = new StringBuilder();
        sb.append("Xin chào ").append(user.getHoTen()).append(",\n\n");
        sb.append("Một giao dịch của bạn đã bị XÓA khỏi hệ thống:\n\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("Loại: ").append(transaction.getLoaiGiaoDich()).append("\n");
        sb.append("Số tiền: ").append(String.format("%,.0f đ", transaction.getSoTien())).append("\n");
        sb.append("Danh mục: ").append(transaction.getTenDanhMuc()).append("\n");
        sb.append("Ngày giao dịch: ").append(transaction.getNgayGiaoDich()).append("\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        sb.append("Nếu đây không phải là hành động của bạn, vui lòng liên hệ hỗ trợ.\n\n");
        sb.append("Trân trọng,\n");
        sb.append("Đội ngũ phát triển");

        return sb.toString();
    }
        // Gửi email qua SMTP
        private boolean sendEmail(String toEmail, String subject, String content) {
            try {
                // Cấu hình properties
                Properties props = new Properties();
                props.put("mail.smtp.host", SMTP_HOST);
                props.put("mail.smtp.port", SMTP_PORT);
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");

                // Tạo session với authentication
                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
                    }
                });

                // Tạo message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(EMAIL_FROM));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(subject);
                message.setText(content);

                // Gửi email
                Transport.send(message);

                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        // Lấy danh sách email của user
        public List<Email> getEmailsByUserId(int userId) {
            return emailDAO.getByUserId(userId);
        }

        // Đánh dấu đã đọc
        public boolean markAsRead(int emailId) {
            return emailDAO.markAsRead(emailId);
        }

        // Toggle ghim
        public boolean togglePin(int emailId) {
            return emailDAO.toggleGhim(emailId);
        }

        // Xóa email
        public boolean deleteEmail(int emailId) {
            return emailDAO.delete(emailId);
        }

        // Đếm email chưa đọc
        public int getUnreadCount(int userId) {
            return emailDAO.countUnread(userId);
        }
    }