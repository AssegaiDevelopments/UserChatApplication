import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QRCodeGenerator {

    public static void main(String[] args) {
        String username = JOptionPane.showInputDialog(null, "Enter your username:", "Username Input", JOptionPane.QUESTION_MESSAGE);
        String password = JOptionPane.showInputDialog(null, "Enter your password:", "Password Input", JOptionPane.QUESTION_MESSAGE);

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username or password cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Format Name:Password
        String toQR = username + ":" + password;

        String downloadsPath = System.getProperty("user.home") + "/Downloads/qrcode.png";
        int width = 300;
        int height = 300;

        try {
            generateQRCode(toQR, downloadsPath, width, height);
            JOptionPane.showMessageDialog(null, "QR Code generated successfully: " + downloadsPath, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (WriterException | IOException e) {
            JOptionPane.showMessageDialog(null, "Error generating QR Code: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void generateQRCode(String data, String filePath, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }
}