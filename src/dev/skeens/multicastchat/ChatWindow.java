package dev.skeens.multicastchat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.function.Function;

public class ChatWindow extends JFrame {

    private final JTextPane chatTextPane;
    private final JTextField inputField;
    private final JButton sendButton;
    private final Function<String, Boolean> messageHandler;

    public ChatWindow(Function<String, Boolean> messageHandler) {
        this.messageHandler = messageHandler;

        // Set up the main window
        setTitle("Chat Window");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set up the chat text pane
        chatTextPane = new JTextPane();
        chatTextPane.setEditable(false);
        chatTextPane.setContentType("text/html");
        chatTextPane.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(chatTextPane);
        scrollPane.setPreferredSize(new Dimension(600, 350));

        // Set up the input field and send button
        inputField = new JTextField();
        inputField.addActionListener(this::sendMessage);
        sendButton = new JButton("Send");
        sendButton.addActionListener(this::sendMessage);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Add components to the frame
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    private void sendMessage(ActionEvent e) {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            boolean success = messageHandler.apply(message);
            insertChatMessage("You", LocalDateTime.now(), message, true, success);
            inputField.setText("");
        }
    }

    public void insertChatMessage(String sender, LocalDateTime time, String message, boolean isHost, boolean success) {
        StringBuilder html = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        if (isHost) {
            // Right-aligned message for the host
            html.append("<div style='text-align: right; margin-bottom: 10px;'>");
            html.append("<span style='color: #777777;'>").append(sender).append(" ").append(time.format(formatter)).append("</span><br>");
            html.append("<span style='background-color: #DCF8C6; padding: 5px 10px; border-radius: 20px;'>").append(message).append("</span>");
            html.append("</div>");
        } else {
            // Left-aligned message for external
            html.append("<div style='text-align: left; margin-bottom: 10px;'>");
            html.append("<span style='color: #777777;'>").append(sender).append(" ").append(time.format(formatter)).append("</span><br>");
            html.append("<span style='background-color: #E5E5EA; padding: 5px 10px; border-radius: 20px;'>").append(message).append("</span>");
            html.append("</div>");
        }

        chatTextPane.setText(chatTextPane.getText() + html);
        chatTextPane.setCaretPosition(chatTextPane.getDocument().getLength());
    }
}