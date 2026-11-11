
import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ChatClient extends JFrame {

    private JTextArea chatArea;
    private JTextField messageField;
    private PrintWriter out;

    public ChatClient() {

        setTitle("Real-Time Chat Application");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);

        add(new JScrollPane(chatArea),
                BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());

        messageField = new JTextField();
        JButton sendButton = new JButton("Send");

        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        add(panel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());

        messageField.addActionListener(e -> sendMessage());

        String username = JOptionPane.showInputDialog(
                this, "Enter Your Name:");

        if (username == null || username.trim().isEmpty()) {
            username = "Guest";
        }

        connectToServer(username);

        setVisible(true);
    }

    private void connectToServer(String username) {

        try {

            Socket socket = new Socket("localhost", 5000);

            out = new PrintWriter(
                    socket.getOutputStream(), true);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            out.println(username + " joined the chat!");

            Thread thread = new Thread(() -> {

                try {

                    String message;

                    while ((message = in.readLine()) != null) {

                        final String msg = message;

                        SwingUtilities.invokeLater(() ->
                                chatArea.append(msg + "\n"));
                    }

                } catch (IOException e) {
                    SwingUtilities.invokeLater(() ->
                            chatArea.append("Disconnected from server\n"));
                }

            });

            thread.start();

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this, "Unable to connect to server!");
        }
    }

    private void sendMessage() {

        String message = messageField.getText().trim();

        if (!message.isEmpty() && out != null) {

            out.println(message);
            messageField.setText("");
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
                new ChatClient());
    }
}

