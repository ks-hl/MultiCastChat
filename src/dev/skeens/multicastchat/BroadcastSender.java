package dev.skeens.multicastchat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class BroadcastSender implements Runnable {
    private final int port;
    private final AtomicBoolean running;

    public BroadcastSender(int port, AtomicBoolean running) {
        this.port = port;
        this.running = running;
    }

    private void broadcast(String message) throws IOException {
        byte[] buffer = message.getBytes();

        int interfacesSentTo = 0;
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                continue;
            }

            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                if (interfaceAddress.getAddress().getAddress().length != 4) continue;

                try (DatagramSocket socket = new DatagramSocket()) {
                    socket.setBroadcast(true);

                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("239.255.255.255"), port);
                    socket.send(packet);
                } catch (IOException e) {
                    continue;
                }
                interfacesSentTo++;
            }
        }
        if (interfacesSentTo == 0) {
            System.err.println("No interfaces to broadcast to.");
        } else {
            System.out.println("Broadcast sent to " + interfacesSentTo + " addresses/interfaces.");
        }
    }

    @Override
    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (running.get()) {
                String line = scanner.nextLine();
                if (line.equals("exit")) {
                    running.set(false);
                    System.exit(1);
                }

                try {
                    broadcast(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}