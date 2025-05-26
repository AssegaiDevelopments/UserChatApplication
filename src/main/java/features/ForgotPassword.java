package features;

import org.mindrot.jbcrypt.BCrypt;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import static constants.RegexConstants.*;
import static features.SoundPlayer.*;

public class ForgotPassword {
    private static String url;
    private static String user;
    private static String password;

    public ForgotPassword(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static void forgotFunc() {
        String email = JOptionPane.showInputDialog(null, "Enter your Gmail:", "Forgot Password", JOptionPane.QUESTION_MESSAGE);
        if (email == null || email.isEmpty()) {
            errorSound();
            JOptionPane.showMessageDialog(null, "Email cannot be empty", "Email Null Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int[] generatedCode = new int[4];
        for (int i = 0; i < 4; ++i) {
            generatedCode[i] = new Random().nextInt(10);
        }

        StringBuilder code = new StringBuilder();
        for (int n : generatedCode) {
            code.append(n);
        }

        String subject = "Code Verification - Connect";
        String message = "Your verification code is: " + code;

        EmailSender.sendEmail(email, subject, message);
        sendSound();
        JOptionPane.showMessageDialog(null, "Verification code sent to your email.", "Email Sent", JOptionPane.INFORMATION_MESSAGE);

        String verifyCode = JOptionPane.showInputDialog(null, "Enter the code:", "Verify Code", JOptionPane.INFORMATION_MESSAGE);
        if (!code.toString().equals(verifyCode)) {
            errorSound();
            JOptionPane.showMessageDialog(null, "Invalid Code", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newPassword = JOptionPane.showInputDialog(null, "Enter your new password:", "Reset Password", JOptionPane.INFORMATION_MESSAGE);
        String retypePassword = JOptionPane.showInputDialog(null, "Retype your new password:", "Reset Password", JOptionPane.INFORMATION_MESSAGE);

        if (!newPassword.equals(retypePassword)) {
            errorSound();
            JOptionPane.showMessageDialog(null, "Passwords do not match.", "Password Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!isValidPassword(newPassword)) {
            errorSound();
            JOptionPane.showMessageDialog(null, "Password must contain at least one uppercase letter, one lowercase letter, one numeric digit, and one symbol.", "Password Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String updateQuery = "UPDATE Credentials SET Passwords = ? WHERE BINARY Emails = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt(14)));
                statement.setString(2, email);

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    subject = "Reset Password Successful - Connect";
                    message = "Your password has been reset successfully.";
                    EmailSender.sendEmail(email, subject, message);
                    JOptionPane.showMessageDialog(null, "Password reset successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    errorSound();
                    JOptionPane.showMessageDialog(null, "Failed to reset password. Please try again.", "Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            errorSound();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static boolean isValidPassword(String password) {
        return password.matches(COMPLEX_PASSWORD_PATTERN) &&
               password.length() >= 8 &&
               password.length() <= 20;
    }
}