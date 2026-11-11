
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    private static final int PORT = 5000;

    private static Set<PrintWriter> clients =
            Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {

        System.out.println("Chat Server Started...");

        try (ServerSocket server =
                     new ServerSocket(PORT)) {

            while (true) {

                Socket socket = server.accept();

                System.out.println("New Client Connected!");

                new ClientHandler(socket).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {

        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {

            try {

                in = new BufferedReader(
                    new InputStreamReader(
                        socket.getInputStream()));

                out = new PrintWriter(
                    socket.getOutputStream(), true);

                clients.add(out);

                String message;

                while ((message = in.readLine()) != null) {

                    System.out.println(message);

                    synchronized (clients) {
                        for (PrintWriter client : clients) {
                            client.println(message);
                        }
                    }
                }

            } catch (IOException e) {
                System.out.println("Client Disconnected");
            } finally {

                if (out != null) {
                    clients.remove(out);
                }

                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

