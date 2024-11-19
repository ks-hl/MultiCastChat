package dev.skeens.multicastchat;

import javax.swing.*;
import java.awt.*;

public class ChatMessagePanel extends JPanel {
    private final JLabel senderLabel;
    private final JLabel messageLabel;

    public ChatMessagePanel(String sender, String time, String message, boolean isHost) {
        setLayout(new BorderLayout());
        setBackground(isHost ? new Color(0xD9EAD3) : new Color(0xF4F4F4));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setPreferredSize(new Dimension(580, 60));

        senderLabel = new JLabel(sender + " " + time);
        senderLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        senderLabel.setForeground(Color.GRAY);

        messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setOpaque(true);
        messageLabel.setBackground(getBackground());

        add(senderLabel, BorderLayout.NORTH);
        add(messageLabel, BorderLayout.CENTER);

        if (isHost) {
            setAlignmentX(Component.RIGHT_ALIGNMENT);
        } else {
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }
    }
}