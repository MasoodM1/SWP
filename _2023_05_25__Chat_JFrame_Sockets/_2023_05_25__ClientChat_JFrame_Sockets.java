package _2023_05_25__Chat_JFrame_Sockets;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class _2023_05_25__ClientChat_JFrame_Sockets extends JFrame implements ActionListener {

    private JTextArea chatTextArea;
    private JList<String> userList;
    private JTextField messageTextField;
    private JButton privateMessageButton;  // New button for private messaging
    private BufferedReader reader;
    private PrintWriter writer;
    private Socket socket;
    private String username;
    private DefaultListModel<String> connectedUsersModel;

    public _2023_05_25__ClientChat_JFrame_Sockets(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(600, 500);
        setLocationRelativeTo(null);

        // Chat Panel
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatTextArea = new JTextArea(15, 30);
        chatTextArea.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font size
        chatTextArea.setLineWrap(true);
        chatTextArea.setWrapStyleWord(true);
        chatTextArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatTextArea);
        chatScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);

        // User List Panel
        JPanel userListPanel = new JPanel();
        userListPanel.setLayout(new BorderLayout());
        connectedUsersModel = new DefaultListModel<>();
        userList = new JList<>(connectedUsersModel);

        // User List Title
        JLabel userListTitle = new JLabel("User");
        userListTitle.setHorizontalAlignment(SwingConstants.CENTER);
        userListPanel.add(userListTitle, BorderLayout.NORTH);

        JScrollPane userListScrollPane = new JScrollPane(userList);
        userListScrollPane.setPreferredSize(new Dimension(150, 0));
        userListPanel.add(userListScrollPane, BorderLayout.CENTER);

        // Message Panel
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        messageTextField = new JTextField(30);
        messageTextField.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font size
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        privateMessageButton = new JButton("Private Message");  // Initialize the private message button
        privateMessageButton.addActionListener(this);  // Add ActionListener for the private message button
        messagePanel.add(messageTextField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        messagePanel.add(privateMessageButton, BorderLayout.WEST);  // Add the private message button to the message panel

        // Add panels to the main frame
        add(chatPanel, BorderLayout.CENTER);
        add(userListPanel, BorderLayout.EAST);
        add(messagePanel, BorderLayout.SOUTH);

        // Prompt for username
        username = promptForUsername();
        connectedUsersModel.addElement(username);

        // Set up network connection
        setUpNetworking();

        // Start reading messages from the server
        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();

        // Send message when Enter key is pressed
        messageTextField.addActionListener(this);

        setVisible(true);
    }

    private String promptForUsername() {
        return JOptionPane.showInputDialog(this, "Enter your username:");
    }

    private void setUpNetworking() {
        try {
            String host = "localhost"; // Change to server IP address or hostname
            int port = 5000; // Change to server port number

            socket = new Socket(host, port);
            InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(socket.getOutputStream());
            System.out.println("Network connection established...");

            // Send the username to the server
            writer.println(username);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageTextField.getText();
        writer.println(message);
        writer.flush();
        messageTextField.setText("");
        messageTextField.requestFocus();
    }

    private void sendPrivateMessage(String recipient) {
        String message = messageTextField.getText();
        writer.println("[PRIVATE]" + recipient + ": " + message);
        writer.flush();
        messageTextField.setText("");
        messageTextField.requestFocus();
    }

    private void processMessage(String message) {
        chatTextArea.append(message + "\n");
        chatTextArea.setCaretPosition(chatTextArea.getDocument().getLength());
    }

    private void processUserList(String userListString) {
        String[] users = userListString.split(",");
        connectedUsersModel.clear();
        for (String user : users) {
            connectedUsersModel.addElement(user);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            JButton sourceButton = (JButton) e.getSource();
            if (sourceButton == privateMessageButton) {
                String recipient = userList.getSelectedValue();
                if (recipient != null && !recipient.equals(username)) {
                    sendPrivateMessage(recipient);
                }
            } else {
                sendMessage();
            }
        } else if (e.getSource() instanceof JTextField) {
            sendMessage();
        }
    }

    private class IncomingReader implements Runnable {
        @Override
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    if (message.startsWith("[USERLIST]")) {
                        String userListString = message.substring(10);
                        processUserList(userListString);
                    } else if (message.startsWith("[PRIVATE]")) {
                        processMessage(message.substring(9) + " (Private)");
                    } else {
                        processMessage(message);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new _2023_05_25__ClientChat_JFrame_Sockets("Chat Client"));
    }
}







//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.Socket;
//
//public class _2023_05_25__ClientChat_JFrame_Sockets extends JFrame implements ActionListener {
//    private JTextArea incomingTextArea;
//    private JTextField outgoingTextField;
//    private PrintWriter writer;
//    private Socket socket;
//    private String username;
//
//    public _2023_05_25__ClientChat_JFrame_Sockets(String title) {
//        super(title);
//
//        incomingTextArea = new JTextArea(15, 20);
//        incomingTextArea.setLineWrap(true);
//        incomingTextArea.setWrapStyleWord(true);
//        incomingTextArea.setEditable(false);
//        JScrollPane scrollPane = new JScrollPane(incomingTextArea);
//        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//
//        outgoingTextField = new JTextField(20);
//        JButton sendButton = new JButton("Send");
//        sendButton.addActionListener(this);
//
//        JPanel panel = new JPanel();
//        panel.add(scrollPane);
//        panel.add(outgoingTextField);
//        panel.add(sendButton);
//
//        add(panel);
//
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(400, 400);
//        setVisible(true);
//
//        username = JOptionPane.showInputDialog(this, "Enter your name:");
//
//        connectToServer();
//        startMessageReaderThread();
//    }
//
//    private void connectToServer() {
//        try {
//            socket = new Socket("localhost", 5000);
//            writer = new PrintWriter(socket.getOutputStream());
//            System.out.println("Connected to server");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void startMessageReaderThread() {
//        Thread readerThread = new Thread(new IncomingReader());
//        readerThread.start();
//    }
//
//    public void actionPerformed(ActionEvent e) {
//        String message = outgoingTextField.getText();
//        sendMessage(message);
//        outgoingTextField.setText("");
//    }
//
//    public void sendMessage(String message) {
//        writer.println(username + ": " + message);
//        writer.flush();
//    }
//
//    public class IncomingReader implements Runnable {
//        private BufferedReader reader;
//
//        public IncomingReader() {
//            try {
//                InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
//                reader = new BufferedReader(streamReader);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void run() {
//            String message;
//            try {
//                while ((message = reader.readLine()) != null) {
//                    System.out.println("Received message: " + message);
//                    incomingTextArea.append(message + "\n");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new _2023_05_25__ClientChat_JFrame_Sockets("Chat Client"));
//    }
//}
//
