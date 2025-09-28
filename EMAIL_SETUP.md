# CoffeeLux Email Service Configuration

## Current Status: MOCK MODE
The system is currently running in mock mode. OTP codes are displayed in the server console instead of being sent to actual email addresses.

## How to Get Your OTP Code

### For Testing/Development:
1. Start the server
2. Go to forgot password page
3. Enter your email address
4. Check the **server console/terminal** for the OTP code
5. Look for this format:
```
============================================================
🚨 MOCK EMAIL SERVICE - EMAIL NOT SENT TO INBOX
============================================================
📧 TO: your-email@example.com
👤 USER: Your Name

🔐 YOUR OTP CODE IS: 123456
⏰ Expires in: 10 minutes
============================================================
```

## How to Enable Real Email Sending

### Step 1: Gmail App Password Setup
1. Go to your Gmail account settings
2. Enable 2-Factor Authentication
3. Generate an App Password for "Mail"
4. Copy the 16-character app password

### Step 2: Configure EmailService.java
```java
// In EmailService.java, update these constants:
private static final boolean ENABLE_REAL_EMAIL = true;
private static final String EMAIL_USERNAME = "your-gmail@gmail.com";
private static final String EMAIL_PASSWORD = "your-app-password"; // 16-character app password
```

### Step 3: Add JavaMail Implementation
The system needs to implement real JavaMail SMTP sending. Currently using mock service due to library conflicts.

### Step 4: Test Configuration
1. Restart the server
2. Try forgot password flow
3. Check if email arrives in inbox

## Troubleshooting

### Common Issues:
1. **Gmail blocks login** - Make sure to use App Password, not regular password
2. **SMTP connection fails** - Check firewall/network settings
3. **Email goes to spam** - Add sender to safe list

### Debug Steps:
1. Check server console for error messages
2. Verify SMTP credentials
3. Test with different email providers
4. Check email server logs

## Production Considerations
- Use environment variables for email credentials
- Implement email templates
- Add retry logic for failed sends
- Monitor email delivery rates
- Consider using email services like SendGrid or AWS SES

## Current Mock Email Template
The system generates this email content:
```
======================================
           COFFEELUX SYSTEM           
======================================

Xin chào [User Name]!

Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản CoffeeLux.
Mã xác thực OTP của bạn là:

        [OTP_CODE]

Mã này sẽ hết hạn sau 10 phút.
Vui lòng không chia sẻ mã này với bất kỳ ai khác.

Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.

Trân trọng,
Đội ngũ CoffeeLux Support

======================================
© 2025 CoffeeLux Coffee Management System
======================================
```