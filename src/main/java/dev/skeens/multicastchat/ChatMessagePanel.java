package dev.skeens.multicastchat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

public class ChatMessagePanel extends JPanel {

    public ChatMessagePanel(String sender, String time, String message, boolean isHost, boolean success) {
        setLayout(new BorderLayout());
        if (success) {
            setBackground(isHost ? new Color(0xD9EAD3) : new Color(0xF4F4F4));
        } else {
            setBackground(new Color(0xffcccc));
        }
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        setPreferredSize(new Dimension(350, -1));
        setMaximumSize(new Dimension(350, -1));

        JLabel senderLabel = new JLabel(sender);
        senderLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        senderLabel.setForeground(Color.GRAY);

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        timeLabel.setForeground(Color.GRAY);

        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        header.setBackground(getBackground());
        header.add(senderLabel, BorderLayout.WEST);
        header.add(timeLabel, BorderLayout.EAST);

        JTextArea messageLabel = new JTextArea(message);
        messageLabel.setEditable(false);
        messageLabel.setLineWrap(true);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setOpaque(true);
        messageLabel.setBackground(getBackground());

        add(header, BorderLayout.PAGE_START);
        add(messageLabel, BorderLayout.CENTER);

        if (isHost) {
            setAlignmentX(Component.RIGHT_ALIGNMENT);
        } else {
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }
    }
}