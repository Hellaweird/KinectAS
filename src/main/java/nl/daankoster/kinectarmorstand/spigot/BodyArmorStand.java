package nl.daankoster.kinectarmorstand.spigot;

import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityTeleport;
import nl.daankoster.kinectarmorstand.ArmorStandMovement;
import nl.daankoster.kinectarmorstand.KinectArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.EulerAngle;

public class BodyArmorStand {


    private KinectArmorStand main = KinectArmorStand.getInstance();
    private Location loc;
    private EntityArmorStand armorStand;
    private ArmorStand as;

    private double x,y,z,pitch,rotation;


    private ArmorStand look;
    private Player player;
    public BodyArmorStand(Location l) {
        Location location = l.clone();
        location.setPitch(0);

        this.loc = location.clone().add(location.getDirection().multiply(4));

        loc.setDirection(location.getDirection().multiply(-1));


    }

    private void sendPacket(Packet packet) {
        Bukkit.getOnlinePlayers().forEach(cur -> ((CraftPlayer)cur).getHandle().playerConnection.sendPacket(packet));
    }

    public void spawn(Player target) {
        this.player = target;

        as = loc.getWorld().spawn(loc,ArmorStand.class);
        as.setArms(true);
        as.setBasePlate(false);
        as.setGravity(false);
        as.setVisible(false);

        org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(Material.DIAMOND_SHOVEL);
        ItemMeta meta = item.getItemMeta();
        ((Damageable) meta).setDamage(537);
        item.setItemMeta(meta);

        as.getEquipment().setHelmet(item);
    }


    public void update(String packet) {

        String[] decoded = packet.split(":");

        ArmorStandMovement move = new ArmorStandMovement(decoded);

        if(KinectArmorStand.getInstance().isStartSaving()) {
            KinectArmorStand.getInstance().getCache().add(move);
        }


        as.setRightArmPose(new EulerAngle(Math.toRadians(-move.right_armX),Math.toRadians(move.right_armY),0));
        as.setLeftArmPose(new EulerAngle(Math.toRadians(-move.left_armX),Math.toRadians(move.left_armY),0));

        as.setRightLegPose( new EulerAngle(Math.toRadians(move.right_legX),Math.toRadians(move.right_legY),0));
        as.setLeftLegPose(new EulerAngle(Math.toRadians(move.left_legX),Math.toRadians(move.left_legY),0));




        Bukkit.getServer().getScheduler().runTask(main, new Runnable(){
            @Override
            public void run(){
                ((CraftEntity)as).getHandle().setLocation(as.getLocation().getX()- move.vecX, as.getLocation().getY() -move.vecY, as.getLocation().getZ() -move.vecZ,(float) (loc.getYaw() - (move.yaw * 0.8)), (float) move.pitch + 10);
                PacketPlayOutEntityTeleport tp = new PacketPlayOutEntityTeleport(((CraftEntity)as).getHandle());
                sendPacket(tp);
            }
        });



    }


}
