package dev.skeens.multicastchat;
 
import java.util.concurrent.atomic.AtomicBoolean;
 
public class Main {
    private static final int PORT = 3335;
    public static void main(String[] args) {
        AtomicBoolean running = new AtomicBoolean(true);
        new Thread(new BroadcastReceiver(PORT, running)).start();
        new Thread(new BroadcastSender(PORT, running)).start();
    }
}

     

