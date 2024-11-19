package dev.skeens.multicastchat;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class ChatWindow extends JFrame {
    private final JPanel chatPanel;
    private final JTextField inputField;
    private final JButton sendButton;
    private final Function<String, Boolean> messageHandler;
    private final JScrollPane scrollPane;

    public ChatWindow(Function<String, Boolean> messageHandler) {
        this.messageHandler = messageHandler;
        // Set up the main window
        setTitle("Chat Window");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set up the chat panel and scroll pane
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setOpaque(true);
        chatPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(chatPanel);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        ChatMessagePanel chatMessagePanel = new ChatMessagePanel(sender, time.format(formatter), message, isHost);
        JPanel line = new JPanel();
        if (isHost) line.add(new JPanel());
        line.add(chatMessagePanel, isHost ? BorderLayout.EAST : BorderLayout.WEST);
        if (!isHost) line.add(new JPanel());
        chatPanel.add(line);
        revalidate();
        repaint();
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    }
}