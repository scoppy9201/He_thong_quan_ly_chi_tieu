⬤ HỆ THỐNG QUẢN LÝ CHI TIÊU CÁ NHÂN 
- Hệ Thống Quản Lý Lý Chi Tiêu Cá Nhân là một ứng dụng đơn giản trên máy tính để bàn giúp người dùng theo dõi, quản lý và phân tích chi tiêu hàng ngày. Dự án này được xây dựng bằng Java (sử dụng NetBeans IDE) và kết nối với cơ sở dữ liệu MySQL để lưu trữ dữ liệu. 
- Ứng dụng nhắm mục tiêu giúp người dùng kiểm soát tài khoản chính cá nhân một cách hiệu quả, với các tính năng như bổ sung giao dịch, xem báo cáo và phân loại chi tiêu.

⬤ Tính năng chính
- Thêm và chỉnh sửa giao dịch : Cho phép nhập chi tiêu/thu nhập với ngày, số tiền, danh mục và mô tả.

- Xem báo cáo : Hiển thị tổng thể chi tiêu theo ngày, tháng hoặc danh mục.
- Phân loại chi tiêu : Hỗ trợ các danh mục như ăn uống, giải trí, hóa đơn, vv
- Xuất dữ liệu : Xuất tệp báo cáo CSV hoặc PDF.
- Người dùng đăng nhập : Hỗ trợ nhiều tài khoản người dùng để quản lý riêng biệt.

⬤ Yêu cầu hệ thống 
- Trước khi cài đặt, hệ thống bảo mật của bạn sẽ đáp ứng các yêu cầu sau:
  + Java Development Kit (JDK) : Phiên bản 8 trở lên (khuyến nghị JDK 11 hoặc 17).
  + NetBeans IDE : Phiên bản 12.0 trở lên (tải từ netbeans.apache.org ).
  + MySQL Server : Phiên bản 5.7 trở lên (tải từ mysql.com ).
  + Hệ điều hành : Windows, macOS hoặc Linux.
  + Kết nối internet : Để tải các thư viện phụ thuộc (nếu cần).
 
⬤ Cài đặt
Bước 1: Sao chép kho lưu trữ
- Sao chép mã
  + git clone https://github.com/scoppy9201/He_thong_quan_ly_chi_tieu.git
  + cd he-thong-quan-ly-chi-tieu
Bước 2: Cài đặt và cấu hình MySQL
Cài đặt MySQL Server và khởi động dịch vụ.
- Tạo cơ sở dữ liệu mới:
- Sao chép mã
  + CREATE DATABASE expense_manager;
- Tạo tài khoản người dùng cho ứng dụng (thay thế 'tên người dùng' và 'mật khẩu' bằng giá trị thực):
- Sao chép mã
  + CREATE USER 'expense_user'@'localhost' IDENTIFIED BY 'password';
  + GRANT ALL PRIVILEGES ON expense_manager.* TO 'expense_user'@'localhost';
  + FLUSH PRIVILEGES;
- Chạy tập lệnh tạo bảng (tìm tệp database_setup.sqltrong dự án thư mục và thực thi trong MySQL Workbench hoặc dòng lệnh):
- Sao chép mã
  + mysql -u expense_user -p expense_manager < database_setup.sql
Bước 3: Import dự án vào NetBeans
- Mở NetBeans IDE.
- Chọn Tệp > Mở dự án và chọn bản sao dự án thư mục.
- Nếu có lỗi phụ thuộc, NetBeans sẽ cài đặt Maven hoặc Gradle (dự án này sử dụng Maven để quản lý phụ thuộc).
- Cập nhật tệp cấu hình cơ sở dữ liệu: Mở tệp src/main/resources/db.properties và chỉnh sửa kết nối thông tin MySQL (máy chủ, cổng, tên người dùng, mật khẩu).
Bước 4: Cài đặt thư viện phụ thuộc
- Nếu sử dụng Maven, NetBeans sẽ tự động tải các thư viện (như MySQL Connector/J). Nếu không, hãy thêm thủ công:
- Tải MySQL Connector/J từ mysql.com .
- Thêm JAR vào classpath của dự án trong NetBeans.

⬤ Liên hệ
- Tác giả: [Bùi Mạnh Hưng]
- Email: [Buimanhhung3105@gmail.com]
- Báo cáo vấn đề: [Liên kết đến các vấn đề trên GitHub]
- Lịch sử phiên bản
  + v1.0.0 (25/11/2025): Phát hành phiên bản đầu tiên với các tính năng bổ sung giao dịch và xem cơ sở báo cáo.
- [Thêm các phiên bản khác].

⬤ Cảm ơn
- Cảm ơn cộng đồng Java và MySQL đã cung cấp các công cụ tuyệt vời. Đặc biệt cảm ơn [danh sách người đóng góp] đã hỗ trợ phát triển dự án này.
