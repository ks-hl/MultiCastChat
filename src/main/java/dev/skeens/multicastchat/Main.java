package dev.skeens.multicastchat;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    private static final int PORT = 3335;
    public static final UUID MY_UUID = UUID.randomUUID();

    public static void main(String[] args) {
        AtomicBoolean running = new AtomicBoolean(true);

        SwingUtilities.invokeLater(() -> {
            ChatWindow chatWindow = new ChatWindow(message -> {
                try {
                    BroadcastSender.broadcast(message, PORT);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            });

            BroadcastReceiver broadcastReceiver = new BroadcastReceiver(PORT, running, (sender, msg) -> chatWindow.insertChatMessage(sender, LocalDateTime.now(), msg, false, true));
            new Thread(broadcastReceiver).start();

            chatWindow.setVisible(true);
        });
    }
}

     

