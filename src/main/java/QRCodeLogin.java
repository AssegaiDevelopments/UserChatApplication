import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import static constants.Colors.backgroundColor;

public class QRCodeLogin extends JFrame {

    private JLabel label;
    private JPanel panel;
    private WebcamPanel webcamPanel;
    private Login loginInstance;

    public QRCodeLogin(Login loginInstance) {
        super("QR Code Login");
        this.loginInstance = loginInstance;

        UIManager.put("defaultFont", new Font("Roboto", Font.PLAIN, 13));

        label = new JLabel();
        panel = new JPanel();
        panel.setSize(250, 250);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        label.setText("Place the QR Code in front of the camera");
        label.setForeground(Color.WHITE);
        panel.add(label);
        panel.setBackground(backgroundColor);
        add(panel, BorderLayout.NORTH);

        webcamPanel = new WebcamPanel();
        webcamPanel.setBackground(backgroundColor);
        add(webcamPanel, BorderLayout.CENTER);

        setBackground(backgroundColor);
        setLocation(450, 150);
        setResizable(false);
        setSize(400, 500);
        setVisible(true);

        new Thread(() -> {
            VideoCapture camera = new VideoCapture(0);
            if (camera.isOpened()) {
                Mat frame = new Mat();
                while (true) {
                    if (camera.read(frame)) {
                        String qrCodeText = scanQRCode(frame);
                        if (qrCodeText != null) {
                            setTitle("Scanning QR Code");
                            label.setText("Scanning QR Code");

                            String[] qrCredentials = qrCodeText.split(":");
                            if (qrCredentials.length == 2) {
                                SwingUtilities.invokeLater(() -> {
                                    try {
                                        loginInstance.lUser.setText(qrCredentials[0]);
                                        loginInstance.lPass.setText(qrCredentials[1]);
                                        Thread.sleep(500);
                                        loginInstance.lButton.doClick();
                                        dispose();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                });
                                break;
                            } else {
                                label.setText("Invalid QR Code format.");
                                SwingUtilities.invokeLater(() -> {
                                    try {

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            }

                        } else {
                            label.setText("Place the QR Code in front of the camera.");
                            setTitle("QR Code Login");
                        }
                        webcamPanel.updateImage(frame);
                    } else {
                        label.setText("Error: Unable to read frame from camera at index 0");
                    }
                }
            } else {
                label.setText("Error: Camera not found at index 0");
            }
        }).start();
    }

    static {
        System.loadLibrary("opencv_java4110");
    }

    private static String scanQRCode(Mat frame) {
        Mat gray = new Mat();
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);

        try {
            int width = gray.width();
            int height = gray.height();
            int channels = gray.channels();
            byte[] sourcePixels = new byte[width * height * channels];
            gray.get(0, 0, sourcePixels);

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (Exception e) {
            return null;
        }
    }

    class WebcamPanel extends JPanel {
        private BufferedImage image;

        public WebcamPanel() {
            setPreferredSize(new java.awt.Dimension(250, 250));
        }

        public void updateImage(Mat mat) {
            Core.flip(mat, mat, 1);

            int width = mat.width();
            int height = mat.height();
            int channels = mat.channels();
            byte[] sourcePixels = new byte[width * height * channels];
            mat.get(0, 0, sourcePixels);

            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int imageWidth = image.getWidth();
                int imageHeight = image.getHeight();

                int padding = 20;

                int maxImageWidth = panelWidth - 2 * padding;
                int maxImageHeight = panelHeight - 2 * padding;

                double widthScale = (double) maxImageWidth / imageWidth;
                double heightScale = (double) maxImageHeight / imageHeight;
                double scale = Math.min(widthScale, heightScale);

                int scaledWidth = (int) (imageWidth * scale);
                int scaledHeight = (int) (imageHeight * scale);

                int x = (panelWidth - scaledWidth) / 2;
                int y = (panelHeight - scaledHeight) / 2;

                y = Math.max(y, padding);

                g.drawImage(image, x, y, scaledWidth, scaledHeight, this);
            }
        }
    }
}