/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ACER
 */
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class Client {

    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final Color BUTTON_COLOR = new Color(255, 99, 71);
    private static final Color HOVER_COLOR = BUTTON_COLOR.darker();

    private Socket socket;
    private PrintWriter out;
    private DataOutputStream dataOut;
    private JTextField inputField;
    private JTextArea chatArea;

    public static void main(String[] args) {
        new Client().createClientGUI();
    }

    private void createClientGUI() {
        JFrame frame = new JFrame("Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 350);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(BACKGROUND_COLOR);
        chatArea.setForeground(Color.WHITE);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton sendButton = createButton("Send Text");
        sendButton.addActionListener(e -> sendMessage());

        JButton sendFileButton = createButton("Send File");
        sendFileButton.addActionListener(e -> sendFile());

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(sendButton);
        buttonPanel.add(sendFileButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);

        connectToServer();
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BUTTON_COLOR.darker()),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setOpaque(true);
        button.setContentAreaFilled(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(HOVER_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_COLOR);
            }
        });

        return button;
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 5000);
            out = new PrintWriter(socket.getOutputStream(), true);
            dataOut = new DataOutputStream(socket.getOutputStream());
            chatArea.append("Connected to Server\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            try {
                out.println("TEXT:" + message); // Kirim pesan dengan tag
                chatArea.append("Me: " + message + "\n");
                inputField.setText("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(null);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileInputStream fileIn = new FileInputStream(file);

                // Kirim informasi bahwa ini file
                out.println("FILE:" + file.getName());

                // Kirim ukuran file
                dataOut.writeLong(file.length());

                // Kirim isi file
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileIn.read(buffer)) != -1) {
                    dataOut.write(buffer, 0, bytesRead);
                }

                fileIn.close();
                chatArea.append("File sent: " + file.getName() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
