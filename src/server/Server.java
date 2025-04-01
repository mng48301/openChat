package server;

import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int PORT = 12345;
    private static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

   
    private static class UserCredentials {
        private static final String CREDENTIALS_FILE = "user_credentials.txt";
        private static Map<String, String> credentials = new HashMap<>();

        static {
            loadCredentials();
        }

        private static void loadCredentials() {
            try (BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        credentials.put(parts[0], parts[1]);
                    }
                }
            } catch (IOException e) {
                
            }
        }

        public static synchronized boolean registerUser(String username, String password) {
            if (credentials.containsKey(username)) {
                return false; // Username exists
            }
            credentials.put(username, password);
            saveCredentials();
            return true;
        }

        public static synchronized boolean authenticate(String username, String password) {
            return credentials.containsKey(username) && 
                   credentials.get(username).equals(password);
        }

        private static void saveCredentials() {
            try (PrintWriter writer = new PrintWriter(new FileWriter(CREDENTIALS_FILE))) {
                for (Map.Entry<String, String> entry : credentials.entrySet()) {
                    writer.println(entry.getKey() + ":" + entry.getValue());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
                System.out.println("New connection from: " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        }
    }

    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender && client.isAuthenticated()) {
                client.sendMessage(message);
            }
        }
    }

    public static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;
        private boolean authenticated = false;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean isAuthenticated() {
            return authenticated;
        }

        @Override
        public void run() {
            try {
                // Authentication phase
                out.println("Welcome! Choose an option:\n1. Register\n2. Login");
                String choice = in.readLine();

                if ("1".equals(choice)) {
                    handleRegistration();
                } else if ("2".equals(choice)) {
                    handleLogin();
                } else {
                    out.println("Invalid choice. Disconnecting.");
                    return;
                }

                // Only proceed to chat if authenticated
                if (authenticated) {
                    out.println("Authentication successful! Welcome, " + username + "!");
                    System.out.println("User " + username + " has connected");

                    // Chat loop
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println(username + ": " + inputLine);
                        broadcast("[" + username + "]: " + inputLine, this);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                cleanup();
            }
        }

        private void handleRegistration() throws IOException {
            out.println("Enter a new username:");
            String newUsername = in.readLine();
            out.println("Enter a password:");
            String password = in.readLine();

            if (UserCredentials.registerUser(newUsername, password)) {
                this.username = newUsername;
                this.authenticated = true;
                out.println("Registration successful! You are now logged in.");
            } else {
                out.println("Username already taken. Please try again.");
                handleRegistration(); // Recursive retry
            }
        }

        private void handleLogin() throws IOException {
            out.println("Enter your username:");
            String attemptUsername = in.readLine();
            out.println("Enter your password:");
            String password = in.readLine();

            if (UserCredentials.authenticate(attemptUsername, password)) {
                this.username = attemptUsername;
                this.authenticated = true;
                out.println("Login successful!");
            } else {
                out.println("Invalid username or password. Please try again.");
                handleLogin(); // Recursive retry
            }
        }

        private void cleanup() {
            try {
                if (authenticated) {
                    clients.remove(this);
                    System.out.println(username + " has disconnected");
                }
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
