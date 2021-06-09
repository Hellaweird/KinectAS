package nl.daankoster.kinectarmorstand.socket;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SocketReceivedPacket extends Event {



    private String packet;
    public SocketReceivedPacket(String packet) {
        super(true);
        this.packet = packet;

    }


    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getPacket() {
        return packet;
    }
}
