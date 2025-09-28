package service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * OTP Service for password reset functionality
 * Manages OTP generation, validation, and expiration
 */
public class OTPService {
    
    // OTP Configuration
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final String DIGITS = "0123456789";
    
    // In-memory storage for OTP codes (in production, use database or Redis)
    private static final Map<String, OTPInfo> otpStorage = new ConcurrentHashMap<>();
    
    // Secure random generator
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Inner class to store OTP information
     */
    private static class OTPInfo {
        private final String otpCode;
        private final LocalDateTime createdAt;
        private final LocalDateTime expiresAt;
        private boolean used;
        
        public OTPInfo(String otpCode) {
            this.otpCode = otpCode;
            this.createdAt = LocalDateTime.now();
            this.expiresAt = createdAt.plusMinutes(OTP_EXPIRY_MINUTES);
            this.used = false;
        }
        
        public String getOtpCode() { return otpCode; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public boolean isUsed() { return used; }
        public void setUsed(boolean used) { this.used = used; }
        
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
        
        public long getTimeRemainingMinutes() {
            if (isExpired()) return 0;
            return ChronoUnit.MINUTES.between(LocalDateTime.now(), expiresAt);
        }
    }
    
    /**
     * Generate a new 6-digit OTP code
     * @param email User email address (used as key)
     * @return Generated OTP code
     */
    public String generateOTP(String email) {
        // Remove any existing OTP for this email
        invalidateOTP(email);
        
        // Generate new 6-digit OTP
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            int index = random.nextInt(DIGITS.length());
            otp.append(DIGITS.charAt(index));
        }
        
        String otpCode = otp.toString();
        
        // Store OTP with email as key
        otpStorage.put(email.toLowerCase(), new OTPInfo(otpCode));
        
        System.out.println("Generated OTP for " + email + ": " + otpCode + " (expires in " + OTP_EXPIRY_MINUTES + " minutes)");
        
        return otpCode;
    }
    
    /**
     * Validate OTP code for given email
     * @param email User email address
     * @param otpCode OTP code to validate
     * @return true if OTP is valid and not expired, false otherwise
     */
    public boolean validateOTP(String email, String otpCode) {
        if (email == null || otpCode == null) {
            return false;
        }
        
        OTPInfo otpInfo = otpStorage.get(email.toLowerCase());
        
        if (otpInfo == null) {
            System.out.println("No OTP found for email: " + email);
            return false;
        }
        
        if (otpInfo.isUsed()) {
            System.out.println("OTP already used for email: " + email);
            return false;
        }
        
        if (otpInfo.isExpired()) {
            System.out.println("OTP expired for email: " + email);
            invalidateOTP(email); // Clean up expired OTP
            return false;
        }
        
        if (!otpInfo.getOtpCode().equals(otpCode)) {
            System.out.println("Invalid OTP code for email: " + email);
            return false;
        }
        
        // Mark OTP as used
        otpInfo.setUsed(true);
        System.out.println("OTP validated successfully for email: " + email);
        
        return true;
    }
    
    /**
     * Check if OTP exists and is valid for email
     * @param email User email address
     * @return true if valid OTP exists, false otherwise
     */
    public boolean hasValidOTP(String email) {
        if (email == null) return false;
        
        OTPInfo otpInfo = otpStorage.get(email.toLowerCase());
        return otpInfo != null && !otpInfo.isUsed() && !otpInfo.isExpired();
    }
    
    /**
     * Get remaining time for OTP in minutes
     * @param email User email address
     * @return remaining minutes, 0 if expired or not found
     */
    public long getOTPRemainingTime(String email) {
        if (email == null) return 0;
        
        OTPInfo otpInfo = otpStorage.get(email.toLowerCase());
        return otpInfo != null ? otpInfo.getTimeRemainingMinutes() : 0;
    }
    
    /**
     * Invalidate/remove OTP for given email
     * @param email User email address
     */
    public void invalidateOTP(String email) {
        if (email != null) {
            otpStorage.remove(email.toLowerCase());
            System.out.println("OTP invalidated for email: " + email);
        }
    }
    
    /**
     * Clean up expired OTPs (should be called periodically)
     */
    public void cleanupExpiredOTPs() {
        otpStorage.entrySet().removeIf(entry -> {
            boolean expired = entry.getValue().isExpired();
            if (expired) {
                System.out.println("Cleaned up expired OTP for email: " + entry.getKey());
            }
            return expired;
        });
    }
    
    /**
     * Get OTP information for debugging purposes
     * @param email User email address
     * @return OTP info string
     */
    public String getOTPInfo(String email) {
        if (email == null) return "Email is null";
        
        OTPInfo otpInfo = otpStorage.get(email.toLowerCase());
        if (otpInfo == null) {
            return "No OTP found for email: " + email;
        }
        
        return String.format(
            "Email: %s, OTP: %s, Created: %s, Expires: %s, Used: %s, Expired: %s, Remaining: %d minutes",
            email, 
            otpInfo.getOtpCode(),
            otpInfo.getCreatedAt(),
            otpInfo.getExpiresAt(),
            otpInfo.isUsed(),
            otpInfo.isExpired(),
            otpInfo.getTimeRemainingMinutes()
        );
    }
    
    /**
     * Get total number of stored OTPs
     * @return number of OTPs in storage
     */
    public int getStoredOTPCount() {
        return otpStorage.size();
    }
}