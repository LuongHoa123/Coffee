package service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * Email Service - Real Email Implementation via Gmail SMTP
 * Sends OTP emails using Gmail SMTP server
 */
public class EmailService {
    
    // Gmail SMTP Configuration
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_USERNAME = "academy.xdo@gmail.com";
    private static final String EMAIL_PASSWORD = "psbh azjx fqui yvqh"; // App password
    
    private Session session;
    
    public EmailService() {
        initializeEmailSession();
        System.out.println("EmailService initialized (Real Email via Gmail SMTP)");
    }
    
    /**
     * Initialize Gmail SMTP session
     */
    private void initializeEmailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        
        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });
    }
    
    /**
     * Send OTP email to user
     */
    public boolean sendOTPEmail(String recipientEmail, String otpCode, String userName) {
        try {
            System.out.println("📧 Sending OTP email to: " + recipientEmail);
            
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME, "CoffeeLux System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Mã xác thực đặt lại mật khẩu - CoffeeLux");
            
            // Create HTML content
            String htmlContent = createHTMLOTPEmailContent(otpCode, userName);
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            // Send message
            Transport.send(message);
            
            System.out.println("✅ Email sent successfully to: " + recipientEmail);
            System.out.println("🔐 OTP Code: " + otpCode + " (valid for 10 minutes)");
            
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
            
            // Show OTP in console as backup
            System.out.println("🔐 BACKUP - Your OTP Code: " + otpCode);
            return false;
        }
    }
    
    /**
     * Send password reset confirmation email
     */
    public boolean sendPasswordResetConfirmation(String recipientEmail, String userName) {
        try {
            System.out.println("📧 Sending password reset confirmation to: " + recipientEmail);
            
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME, "CoffeeLux System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Mật khẩu đã được cập nhật - CoffeeLux");
            
            // Create HTML content
            String htmlContent = createPasswordResetConfirmationContent(userName);
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            // Send message
            Transport.send(message);
            
            System.out.println("✅ Confirmation email sent to: " + recipientEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send confirmation email: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Create HTML content for OTP email
     */
    private String createHTMLOTPEmailContent(String otpCode, String userName) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<title>Mã xác thực CoffeeLux</title>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                
                "<!-- Header -->" +
                "<div style='text-align: center; background: linear-gradient(135deg, #8B4513, #A0522D); color: white; padding: 30px; border-radius: 10px; margin-bottom: 30px;'>" +
                "<h1 style='margin: 0; font-size: 2.5em;'>☕ CoffeeLux</h1>" +
                "<p style='margin: 10px 0 0 0; font-size: 1.1em; opacity: 0.9;'>Coffee Management System</p>" +
                "</div>" +
                
                "<!-- Main Content -->" +
                "<div style='background: #f9f9f9; padding: 30px; border-radius: 10px; border: 1px solid #ddd;'>" +
                "<h2 style='color: #8B4513; margin-top: 0;'>Xin chào " + userName + "!</h2>" +
                
                "<p style='font-size: 1.1em;'>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản CoffeeLux của mình.</p>" +
                
                "<p style='margin: 20px 0;'>Mã xác thực OTP của bạn là:</p>" +
                
                "<!-- OTP Code Box -->" +
                "<div style='text-align: center; margin: 30px 0;'>" +
                "<div style='display: inline-block; background: white; padding: 20px 40px; border-radius: 15px; border: 3px solid #8B4513; box-shadow: 0 4px 15px rgba(139, 69, 19, 0.2);'>" +
                "<div style='font-size: 2.5em; font-weight: bold; color: #8B4513; letter-spacing: 8px; font-family: monospace;'>" + otpCode + "</div>" +
                "</div>" +
                "</div>" +
                
                "<!-- Important Notice -->" +
                "<div style='background: #fff3cd; border: 2px solid #ffc107; border-radius: 8px; padding: 15px; margin: 25px 0;'>" +
                "<div style='display: flex; align-items: center;'>" +
                "<span style='font-size: 1.5em; margin-right: 10px;'>⚠️</span>" +
                "<div>" +
                "<strong style='color: #856404;'>Quan trọng:</strong>" +
                "<ul style='margin: 5px 0 0 0; color: #856404;'>" +
                "<li>Mã này có hiệu lực trong <strong>10 phút</strong></li>" +
                "<li>Không chia sẻ mã này với bất kỳ ai</li>" +
                "<li>Chỉ sử dụng mã này trên trang web CoffeeLux chính thức</li>" +
                "</ul>" +
                "</div>" +
                "</div>" +
                "</div>" +
                
                "<p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này hoặc liên hệ với chúng tôi ngay lập tức.</p>" +
                
                "</div>" +
                
                "<!-- Footer -->" +
                "<div style='text-align: center; margin-top: 30px; padding: 20px; color: #666; font-size: 0.9em;'>" +
                "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>" +
                "<p style='margin: 0;'>Trân trọng,<br><strong>Đội ngũ CoffeeLux Support</strong></p>" +
                "<p style='margin: 10px 0 0 0; font-size: 0.8em; opacity: 0.7;'>© 2025 CoffeeLux Coffee Management System</p>" +
                "</div>" +
                
                "</body>" +
                "</html>";
    }
    
    /**
     * Create HTML content for password reset confirmation
     */
    private String createPasswordResetConfirmationContent(String userName) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<title>Mật khẩu đã được cập nhật - CoffeeLux</title>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                
                "<!-- Header -->" +
                "<div style='text-align: center; background: linear-gradient(135deg, #28a745, #20c997); color: white; padding: 30px; border-radius: 10px; margin-bottom: 30px;'>" +
                "<h1 style='margin: 0; font-size: 2.5em;'>☕ CoffeeLux</h1>" +
                "<p style='margin: 10px 0 0 0; font-size: 1.1em; opacity: 0.9;'>Coffee Management System</p>" +
                "</div>" +
                
                "<!-- Main Content -->" +
                "<div style='background: #f9f9f9; padding: 30px; border-radius: 10px; border: 1px solid #ddd;'>" +
                "<h2 style='color: #28a745; margin-top: 0;'>Xin chào " + userName + "!</h2>" +
                
                "<!-- Success Message -->" +
                "<div style='text-align: center; margin: 30px 0;'>" +
                "<div style='display: inline-block; background: #d4edda; padding: 20px 40px; border-radius: 15px; border: 3px solid #28a745; box-shadow: 0 4px 15px rgba(40, 167, 69, 0.2);'>" +
                "<div style='font-size: 3em; margin-bottom: 10px;'>✅</div>" +
                "<div style='font-size: 1.3em; font-weight: bold; color: #155724;'>Mật khẩu đã được cập nhật thành công!</div>" +
                "</div>" +
                "</div>" +
                
                "<p style='font-size: 1.1em;'>Mật khẩu tài khoản CoffeeLux của bạn đã được thay đổi thành công.</p>" +
                
                "<p>Bạn có thể đăng nhập với mật khẩu mới ngay bây giờ.</p>" +
                
                "<!-- Security Notice -->" +
                "<div style='background: #e7f3ff; border: 2px solid #007bff; border-radius: 8px; padding: 15px; margin: 25px 0;'>" +
                "<div style='display: flex; align-items: center;'>" +
                "<span style='font-size: 1.5em; margin-right: 10px;'>🔒</span>" +
                "<div>" +
                "<strong style='color: #004085;'>Bảo mật tài khoản:</strong>" +
                "<p style='margin: 5px 0 0 0; color: #004085;'>Nếu bạn không thực hiện thay đổi này, vui lòng liên hệ với chúng tôi ngay lập tức để bảo vệ tài khoản của bạn.</p>" +
                "</div>" +
                "</div>" +
                "</div>" +
                
                "</div>" +
                
                "<!-- Footer -->" +
                "<div style='text-align: center; margin-top: 30px; padding: 20px; color: #666; font-size: 0.9em;'>" +
                "<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>" +
                "<p style='margin: 0;'>Trân trọng,<br><strong>Đội ngũ CoffeeLux Support</strong></p>" +
                "<p style='margin: 10px 0 0 0; font-size: 0.8em; opacity: 0.7;'>© 2025 CoffeeLux Coffee Management System</p>" +
                "</div>" +
                
                "</body>" +
                "</html>";
    }
}
