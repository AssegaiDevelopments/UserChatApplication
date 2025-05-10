import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import java.util.Properties;

public class EmailSender {

    public static void sendEmail(String recipient, String subject, String messageText) {
        final String senderEmail = "hyper5211@gmail.com";
        final String senderPassword = "oktchtizdbyufxgg";

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.debug", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(messageText);

            Transport.send(message);
        } catch (MessagingException e) {
            JOptionPane.showMessageDialog(null, "Failed to send email: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}