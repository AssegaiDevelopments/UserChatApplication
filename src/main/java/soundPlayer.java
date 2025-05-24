import javax.swing.*;
import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class soundPlayer {

    public static void playSound(String soundFilePath) {
        try {
            URL soundUrl = soundPlayer.class.getResource("/sounds/" + soundFilePath);
            File soundFile;

            if (soundUrl != null) {
                soundFile = new File(soundUrl.toURI());
            } else {
                soundFile = new File(soundFilePath);
            }

            // Check if the file exists before attempting to play
            if (soundFile.exists()) {
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(soundFile));
                clip.start(); // Play the sound
            } else {
                 JOptionPane.showMessageDialog(null, "Sound file not found: " + soundFilePath, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
             JOptionPane.showMessageDialog(null, "An error occurred while playing sound.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
