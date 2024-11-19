package dev.skeens.multicastchat;
 
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;
 
public class BroadcastReceiver implements Runnable {
    private final int port;
    private final AtomicBoolean running;
 
    public BroadcastReceiver(int port, AtomicBoolean running) {
        this.port = port;
        this.running = running;
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
 
                System.out.println("[" + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) + "] " + packet.getAddress() + " > " + receivedMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
            running.set(false);
            System.exit(1);
        }
    }
}