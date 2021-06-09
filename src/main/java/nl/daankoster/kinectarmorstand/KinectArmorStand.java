package nl.daankoster.kinectarmorstand;

import nl.daankoster.kinectarmorstand.socket.SocketReceivedPacket;
import nl.daankoster.kinectarmorstand.socket.UDPServer;
import nl.daankoster.kinectarmorstand.spigot.BodyArmorStand;
import nl.daankoster.kinectarmorstand.spigot.ConvertedArmorStand;
import nl.daankoster.kinectarmorstand.spigot.MovingArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import java.util.LinkedList;


public class KinectArmorStand extends JavaPlugin implements Listener {

    private static KinectArmorStand instance;
    private MovingArmorStand armorstand;
    private BodyArmorStand armorstand2;
    private UDPServer server;
    private BukkitTask task;
    private boolean startSaving = false;
    private ConvertedArmorStand convertedArmorStand;

    private LinkedList<ArmorStandMovement> cache = new LinkedList<>();


    @Override
    public void onEnable() {
        instance = this;

        try {
            server = new UDPServer();
            server.runTaskAsynchronously(this);


        } catch (Exception e) {
            e.printStackTrace();
        }
        Bukkit.getPluginManager().registerEvents(this, this);


    }



    @EventHandler
    public void onSocket(SocketReceivedPacket e) {
        if (armorstand != null) {
            armorstand.update(e.getPacket());
            armorstand2.update(e.getPacket());
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (command.getName().equalsIgnoreCase("kinect")) {
                if (args.length == 0) {
                    armorstand = new MovingArmorStand(p.getLocation());
                    armorstand.spawn(p);
                    armorstand2 = new BodyArmorStand(p.getLocation());
                    armorstand2.spawn(p);

                }
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        if(task != null)
            task.cancel();
        server.stop();
    }


    public static KinectArmorStand getInstance() {
        return instance;
    }


    public LinkedList<ArmorStandMovement> getCache() {
        return cache;
    }


    public boolean isStartSaving() {
        return startSaving;
    }
}
