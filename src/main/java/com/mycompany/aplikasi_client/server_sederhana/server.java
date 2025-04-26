/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.aplikasi_client.server_sederhana;

/**
 *
 * @author ACER
 */
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class server {

    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    private static final Color HOVER_COLOR = BUTTON_COLOR.darker();

    private JTextArea textArea;
    private ServerSocket serverSocket;

    public static void main(String[] args) {
        new server().createServerGUI();
    }

    private void createServerGUI() {
        JFrame frame = new JFrame("Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 350);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(BACKGROUND_COLOR);
        textArea.setForeground(Color.WHITE);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);

        JButton startButton = createButton("Start Server");
        startButton.addActionListener(e -> startServer());

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(startButton, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BUTTON_COLOR.darker()),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
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

    private void startServer() {
        try {
            serverSocket = new ServerSocket(5000);
            textArea.append("Server started. Waiting for clients...\n");

            new Thread(() -> {
                try {
                    Socket clientSocket = serverSocket.accept();
                    textArea.append("Client connected: " + clientSocket.getInetAddress() + "\n");

                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());

                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.startsWith("TEXT:")) {
                            textArea.append("Client: " + line.substring(5) + "\n");
                        } else if (line.startsWith("FILE:")) {
                            String fileName = line.substring(5);
                            long fileSize = dataIn.readLong();

                            File receivedFile = new File("received_" + fileName);
                            FileOutputStream fileOut = new FileOutputStream(receivedFile);

                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            long totalRead = 0;
                            while (totalRead < fileSize && (bytesRead = dataIn.read(buffer, 0, (int)Math.min(buffer.length, fileSize - totalRead))) != -1) {
                                fileOut.write(buffer, 0, bytesRead);
                                totalRead += bytesRead;
                            }

                            fileOut.close();
                            textArea.append("Received file: " + receivedFile.getName() + "\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
