package dev.skeens.multicastchat;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.UUID;

public class BroadcastSender {

    public static void broadcast(String messageText, int port) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sender", Main.MY_UUID);
        jsonObject.put("message_id", UUID.randomUUID());
        jsonObject.put("message", messageText);

        byte[] buffer = jsonObject.toString().getBytes();

        int interfacesSentTo = 0;
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                continue;
            }

            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                if (interfaceAddress.getAddress().getAddress().length != 4) continue;

                try (MulticastSocket socket = new MulticastSocket()) {
                    socket.setNetworkInterface(networkInterface);

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
}