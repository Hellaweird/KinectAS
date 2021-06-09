package nl.daankoster.kinectarmorstand.keep;

import nl.daankoster.kinectarmorstand.KinectArmorStand;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityTeleport;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.EulerAngle;

public class MovingArmorStandSaved {


    private boolean selfView = true;

    private Location loc;
    private ArmorStand as;

    private double x,y,z,pitch,rotation;


    private ArmorStand look;
    private Player player;
    public MovingArmorStandSaved(Location l) {
        Location location = l.clone();
        location.setPitch(0);

        this.loc = location.clone().add(location.getDirection().multiply(4));

        loc.setDirection(location.getDirection().multiply(-1));


    }

    public void spawn(Player target) {
        this.player = target;
        as = loc.getWorld().spawn(loc,ArmorStand.class);
        as.setArms(true);
        as.setBasePlate(false);
        as.setGravity(false);

        if(selfView) {
            as.setHeadPose(new EulerAngle(Math.toRadians(180), 0,0));
            this.look = loc.getWorld().spawn(loc, ArmorStand.class);
            look.setArms(true);
            look.setBasePlate(false);
            look.setGravity(false);
            as.setItemInHand(new ItemStack(Material.DIAMOND_AXE));

            target.setGameMode(GameMode.SPECTATOR);
            target.setSpectatorTarget(look);

        } else {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta)item.getItemMeta();
            meta.setOwningPlayer(target);
            item.setItemMeta(meta);
            as.setHelmet(new ItemStack(item));
        }

    }


    public void update(String packet) {

        String[] decoded = packet.split(":");

        if(decoded[0].equalsIgnoreCase("BODY")) {

            int yrleg, prleg, ylleg, plleg, yrarm, prarm, ylarm, plarm;
            int phead, yhead;

            yhead = Integer.valueOf(decoded[1]);
            phead = Integer.valueOf(decoded[2]);
            //arms
            yrarm = Integer.valueOf(decoded[3]);
            prarm = Integer.valueOf(decoded[4]);
            ylarm = Integer.valueOf(decoded[5]);
            plarm = Integer.valueOf(decoded[6]);

            //legs
            yrleg = Integer.valueOf(decoded[7]);
            prleg = Integer.valueOf(decoded[8]);
            ylleg = Integer.valueOf(decoded[9]);
            plleg = Integer.valueOf(decoded[10]);

            as.setRightArmPose(new EulerAngle(Math.toRadians(-prarm), Math.toRadians(yrarm), 0));
            as.setLeftArmPose(new EulerAngle(Math.toRadians(-plarm), Math.toRadians(ylarm), 0));

            as.setRightLegPose(new EulerAngle(Math.toRadians(prleg), Math.toRadians(yrleg), 0));
            as.setLeftLegPose(new EulerAngle(Math.toRadians(plleg), Math.toRadians(ylleg), 0));

            //yaw is really bugged, I don't use it
            if(!selfView)
                as.setHeadPose(new EulerAngle(Math.toRadians(phead), 0,0));


        } else if(decoded[0].equalsIgnoreCase("MOV")) {
             rotation = Double.valueOf(decoded[1]);

             x = Double.valueOf(decoded[2]);
             y = Double.valueOf(decoded[3]);
             z = Double.valueOf(decoded[4]);
             pitch = Double.valueOf(decoded[5]);



            Location l = as.getLocation();
            ((CraftArmorStand)as).getHandle().setLocation(l.getX() - z, l.getY() - y , l.getZ() - x,(float) (loc.getYaw() - (rotation * 0.8)), (float) pitch + 10);
            PacketPlayOutEntityTeleport tp = new PacketPlayOutEntityTeleport(((CraftArmorStand)as).getHandle());
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(tp);

            if(selfView) {
                look.teleport(l);
            }

        }

        if(KinectArmorStand.getInstance().isStartSaving()) {
        }

    }

    public double normalize(double angle) {
        double newAngle = angle % 360;
        if(newAngle < 0)
            newAngle+=360;
        return newAngle;
    }

    public ArmorStand getAs() {
        return as;
    }

    public ArmorStand getLook() {
        return look;
    }
}
