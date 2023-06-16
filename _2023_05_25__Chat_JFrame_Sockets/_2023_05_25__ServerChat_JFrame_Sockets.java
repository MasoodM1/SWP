package _2023_05_25__Chat_JFrame_Sockets;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class _2023_05_25__ServerChat_JFrame_Sockets {

    private Map<String, PrintWriter> clientWriters;
    private Set<String> connectedUsers;

    public _2023_05_25__ServerChat_JFrame_Sockets() {
        clientWriters = new HashMap<>();
        connectedUsers = new HashSet<>();
    }

    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void broadcastMessage(String message) {
        for (PrintWriter writer : clientWriters.values()) {
            writer.println(message);
            writer.flush();
        }
    }

    private void sendPrivateMessage(String recipient, String message) {
        PrintWriter writer = clientWriters.get(recipient);
        if (writer != null) {
            writer.println("[PRIVATE]" + message);
            writer.flush();
        }
    }

    private void updateUserList() {
        StringBuilder userListBuilder = new StringBuilder("[USERLIST]");
        for (String user : connectedUsers) {
            userListBuilder.append(user).append(",");
        }
        String userList = userListBuilder.toString();
        broadcastMessage(userList);
    }

    private class ClientHandler implements Runnable {

        private Socket clientSocket;
        private BufferedReader reader;
        private PrintWriter writer;
        private String username;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                InputStreamReader streamReader = new InputStreamReader(clientSocket.getInputStream());
                reader = new BufferedReader(streamReader);
                writer = new PrintWriter(clientSocket.getOutputStream());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                username = reader.readLine();
                connectedUsers.add(username);
                clientWriters.put(username, writer);
                System.out.println("Username '" + username + "' connected");

                updateUserList();

                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Received message from '" + username + "': " + message);
                    if (message.startsWith("[PRIVATE]")) {
                        String[] parts = message.substring(9).split(":", 2);
                        String recipient = parts[0].trim();
                        String privateMessage = parts[1].trim();
                        sendPrivateMessage(recipient, username + ": " + privateMessage);
                    } else {
                        broadcastMessage(username + ": " + message);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (username != null) {
                    connectedUsers.remove(username);
                    updateUserList();
                }
                if (writer != null) {
                    clientWriters.remove(username);
                }
                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        int port = 5000; // Change to desired port number
        _2023_05_25__ServerChat_JFrame_Sockets server = new _2023_05_25__ServerChat_JFrame_Sockets();
        server.start(port);
    }
}








//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.Iterator;
//
//public class _2023_05_25__ServerChat_JFrame_Sockets {
//    private ArrayList<PrintWriter> clientOutputStreams;
//
//    public class ClientHandler implements Runnable {
//        private BufferedReader reader;
//        private Socket socket;
//
//        public ClientHandler(Socket clientSocket) {
//            try {
//                socket = clientSocket;
//                InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
//                reader = new BufferedReader(isReader);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void run() {
//            PrintWriter writer;
//            try {
//                writer = new PrintWriter(socket.getOutputStream());
//                clientOutputStreams.add(writer);
//
//                String message;
//                while ((message = reader.readLine()) != null) { // Liest Nachrichten vom Client
//                    System.out.println("Received message: " + message);
//                    broadcastMessage(message); // Sendet die Nachricht an alle verbundenen Clients
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void start(int port) {
//        clientOutputStreams = new ArrayList<>();
//        try {
//            ServerSocket serverSocket = new ServerSocket(port);
//            System.out.println("Chat server started on port " + port);
//
//            while (true) {
//                Socket clientSocket = serverSocket.accept(); // Akzeptiert eine eingehende Verbindung von einem Client
//                Thread clientThread = new Thread(new ClientHandler(clientSocket)); // Erstellt einen Thread, um den Client zu behandeln
//                clientThread.start();
//                System.out.println("New client connected");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void broadcastMessage(String message) {
//        Iterator<PrintWriter> it = clientOutputStreams.iterator();
//        while (it.hasNext()) {
//            PrintWriter writer = it.next();
//            writer.println(message); // Sendet die Nachricht an den Client
//            writer.flush();
//        }
//    }
//
//    public static void main(String[] args) {
//        _2023_05_25__ServerChat_JFrame_Sockets server = new _2023_05_25__ServerChat_JFrame_Sockets();
//        server.start(5000); // Startet den Chat-Server auf Port 5000
//    }
//}
