package dev.skeens.multicastchat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class BroadcastReceiver implements Runnable {
    private final int port;
    private final AtomicBoolean running;
    private final BiConsumer<String, String> receivedMessageConsumer;
    private final Set<UUID> receivedMessages = new HashSet<>();

    public BroadcastReceiver(int port, AtomicBoolean running, BiConsumer<String, String> receivedMessageConsumer) {
        this.port = port;
        this.running = running;
        this.receivedMessageConsumer = receivedMessageConsumer;
    }

    public void run() {
        try (MulticastSocket socket = new MulticastSocket(port)) {
            socket.setBroadcast(true);
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback()) continue;
                if (networkInterface.isVirtual()) continue;
                if (!networkInterface.isUp()) continue;
                if (!networkInterface.supportsMulticast()) continue;

                try {
                    socket.joinGroup(new InetSocketAddress("239.255.255.255", port), networkInterface);
                    System.out.println("Registered to " + networkInterface.getDisplayName() + " @ " + networkInterface.getInterfaceAddresses().stream().filter(inter -> inter.getAddress().getAddress().length == 4).map(inter -> inter.getAddress().getHostAddress()).collect(Collectors.joining(",")));
                } catch (IOException e) {
                    if (!e.getMessage().equals("Network interface not configured for IPv4")) {
                        System.err.println("Failed to register multicast on " + networkInterface);
                        e.printStackTrace();
                    }
                }
            }

            byte[] buffer = new byte[1024];

            while (running.get()) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(packet);
                } catch (IOException e) {
                    System.err.println("Failed to receive packet");
                    e.printStackTrace();
                    continue;
                }
                String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                JSONObject jsonObject;

                try {
                    jsonObject = new JSONObject(receivedMessage);
                } catch (JSONException e) {
                    System.err.println("Invalid JSON received from " + packet.getAddress().getHostAddress() + ": " + receivedMessage);
                    continue;
                }

                UUID sender = UUID.fromString(jsonObject.getString("sender"));
                if (sender.equals(Main.MY_UUID)) continue;

                UUID messageID = UUID.fromString(jsonObject.getString("message_id"));
                if (!receivedMessages.add(messageID)) continue;

                String message = jsonObject.getString("message");

                this.receivedMessageConsumer.accept(packet.getAddress().getHostAddress(), message);
                System.out.println("[" + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) + "] " + packet.getAddress() + " > " + receivedMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
            running.set(false);
            System.exit(1);
        }
    }
}