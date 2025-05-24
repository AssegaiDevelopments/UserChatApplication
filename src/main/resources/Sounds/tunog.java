import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL; // Import URL for loading resources from classpath

public class tunog {

    // Helper method to play a sound from a given file path
    private static void playSound(String soundFilePath) {
        try {
            // Attempt to load the sound file from the classpath first, then from the file system
            URL soundUrl = tunog.class.getResource(soundFilePath);
            File soundFile;

            if (soundUrl != null) {
                // If found in classpath, use the URL
                soundFile = new File(soundUrl.toURI());
            } else {
                // Otherwise, assume it's a direct file path
                soundFile = new File(soundFilePath);
            }

            // Check if the file exists before attempting to play
            if (soundFile.exists()) {
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(soundFile));
                clip.start(); // Play the sound
            } else {
                System.err.println("Sound file not found: " + soundFilePath);
                // In a real application, you might show a message box to the user
                // JOptionPane.showMessageDialog(null, "Sound file not found: " + soundFilePath, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            System.err.println("Error playing sound: " + ex.getMessage());
            ex.printStackTrace();
            // In a real application, you might show a message box to the user
            // JOptionPane.showMessageDialog(null, "An error occurred while playing sound.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Ensure GUI updates are done on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create a new JFrame (window)
                JFrame frame = new JFrame("Sound Board");
                frame.setSize(400, 450); // Increased size to accommodate more buttons
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(null); // Using null layout for absolute positioning

                // --- Original "Click Me" button removed as per request ---

                // Create and add the first sound button (original "Error Sound")
                JButton soundButton1 = new JButton("Error Sound");
                soundButton1.setBounds(150, 50, 150, 30);
                soundButton1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Replace "errorSound.wav" with the actual path to your sound file
                        // Make sure these files are in the same directory as your compiled .class file
                        // or within your project's resources if loading via classpath.
                        playSound("errorSound.wav");
                    }
                });
                frame.add(soundButton1);

                // Create and add 5 more sound buttons
                JButton soundButton2 = new JButton("Log in Sound");
                soundButton2.setBounds(150, 90, 150, 30);
                soundButton2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        playSound("loginSound.wav"); // Replace with your sound file path
                    }
                });
                frame.add(soundButton2);

                JButton soundButton3 = new JButton("Message Receive Sound");
                soundButton3.setBounds(150, 130, 150, 30);
                soundButton3.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        playSound("messageReceive.wav"); // Replace with your sound file path
                    }
                });
                frame.add(soundButton3);

                JButton soundButton4 = new JButton("Offline Sound");
                soundButton4.setBounds(150, 170, 150, 30);
                soundButton4.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        playSound("offlineSound.wav"); // Replace with your sound file path
                    }
                });
                frame.add(soundButton4);

                JButton soundButton5 = new JButton("Online Sound");
                soundButton5.setBounds(150, 210, 150, 30);
                soundButton5.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        playSound("onlineSound.wav"); // Replace with your sound file path
                    }
                });
                frame.add(soundButton5);

                JButton soundButton6 = new JButton("Send Sound");
                soundButton6.setBounds(150, 250, 150, 30);
                soundButton6.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        playSound("sendSound.wav"); // Replace with your sound file path
                    }
                });
                frame.add(soundButton6);

                // Make the frame visible
                frame.setVisible(true);
            }
        });
    }
}
