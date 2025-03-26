package server;
import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Scanner;


public class Server {
    private static final int PORT = 12345;
    private static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);


            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    if (scanner.hasNextLine()) {
                        String message = scanner.nextLine();
                        broadcast("Server: " + message, null);
                    }
                }
            }).start();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        }
    }

    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) { 
            if (client != sender) {
                client.out.println(message);
            }
                
        }
    }
    
    public static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;
        public ClientHandler (Socket socket) {
            this.clientSocket = socket;
            try {
                out = new PrintWriter (clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            try {
                out.println("Enter username: ");
                username = in.readLine();
                System.out.println("User " + username + " has connected");
                out.println("Connected to server");

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(username + ": " + inputLine);
                    broadcast("[" + username + "]: " + inputLine, this);

                }
                clients.remove(this);
                System.out.println(username + " has disconnected");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
        }
        public void sendMessage(String message) {
            out.println(message);
        }

    }
}

