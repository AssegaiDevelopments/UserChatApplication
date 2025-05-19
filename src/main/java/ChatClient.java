import javax.swing.*;
import java.io.*;
import java.net.*;

public class ChatClient extends JFrame {
    private static JPanel panel;

    public ChatClient() {
        setTitle("Chat Client");

        try (Socket socket = new Socket("localhost", 1337)) {
            System.out.println("Connected to the server");

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            Thread receiveThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = input.readLine()) != null) {
                        System.out.println("Server: " + serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();

            String clientMessage;
            while ((clientMessage = consoleInput.readLine()) != null) {
                output.println(clientMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);



    }

    public static void main(String[] args) {
        new ChatClient();
    }
}