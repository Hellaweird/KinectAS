package nl.daankoster.kinectarmorstand.socket;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer extends BukkitRunnable {
    private DatagramSocket udpSocket;

    private boolean started = true;

    public UDPServer() throws IOException {
        this.udpSocket = new DatagramSocket(7076);
        System.out.println("-- Kinect server aan op adres: " + InetAddress.getLocalHost().getHostAddress() + "--");
    }

    @Override
    public void run() {
        try {

            String msg;
            while (started) {


                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);


                udpSocket.receive(packet);
                msg = new String(packet.getData()).trim();

                SocketReceivedPacket event = new SocketReceivedPacket(msg);
                Bukkit.getPluginManager().callEvent(event);

            }


        } catch(IOException e) {
            e.printStackTrace();
        }
    }



    public void stop() {
        started = false;
        udpSocket.close();

    }

}