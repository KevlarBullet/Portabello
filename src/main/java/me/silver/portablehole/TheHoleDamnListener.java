package me.silver.portablehole;

import net.minecraft.server.v1_12_R1.NBTTagByte;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class TheHoleDamnListener implements Listener {

    private static final String ITEM_NAME = "\u00a7c\u00a7lPortable Hole";

    private final PortableHole plugin = PortableHole.getInstance();
    private final HashMap<String, Integer> holeCooldowns = new HashMap<>();
    private final HashMap<String, Integer> cooldownTimes = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR) {
                Player player = event.getPlayer();
                ItemStack itemStack = player.getInventory().getItemInMainHand();

                if (itemStack.getType() == Material.EYE_OF_ENDER) {
                    net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
                    NBTTagCompound tagCompound = nmsStack.getTag();

                    if (tagCompound != null && tagCompound.getByte("isHole") == 1) {
                        event.setCancelled(true);
//                        player.sendMessage(tagCompound.toString());

                        if (holeCooldowns.get(player.getName()) == null) {
                            this.startCooldown(player, itemStack);
                            this.digHole(player);
                        }
//                        if (tagCompound.getByte("hasCooldown") != 1) {
//                        }
                    } else {
                        player.sendMessage("This is the problem");
                    }
                }
            }
        }
    }

    private void startCooldown(Player player, ItemStack itemStack) {
        String name = player.getName();

        cooldownTimes.put(player.getName(), 10);
        ItemMeta meta = itemStack.getItemMeta();
//        meta.setDisplayName(ITEM_NAME + " \u00a7610");
//        itemStack.setItemMeta(meta);
//        NBTTagCompound displayCompound = new NBTTagCompound();
//
//        nmsStack.a("display", displayCompound);
//        nmsStack.a("hasCooldown", new NBTTagByte((byte) 1));

        int taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            int time = cooldownTimes.get(name);

            if (time > 0) {
//                displayCompound.setString("Name", ITEM_NAME + " \u00a76" + time);
                meta.setDisplayName(ITEM_NAME + " \u00a76\u00a7l" + time);
                itemStack.setItemMeta(meta);
                cooldownTimes.put(name, time - 1);
            } else {
//                displayCompound.setString("Name", ITEM_NAME);
                meta.setDisplayName(ITEM_NAME);
                itemStack.setItemMeta(meta);
                plugin.getServer().getScheduler().cancelTask(holeCooldowns.get(name));
                holeCooldowns.remove(name);
                cooldownTimes.remove(name);
//                nmsStack.a("hasCooldown", new NBTTagByte((byte) 0));
            }

            if (player.getOpenInventory() != null) {
                player.updateInventory();
            }
        }, 0, 20);

        holeCooldowns.put(name, taskid);
        player.sendMessage("" + taskid);
    }

    private void digHole(Player player) {
        // There's gotta be a better way to do this
        Location pl = player.getLocation();
        float pitch = player.getLocation().getPitch();
        float yaw = player.getLocation().getYaw();

        int x1 = -1;
        int y1 = -1;
        int z1 = -1;
        int x2 = 1;
        int y2 = 1;
        int z2 = 1;

        if (pitch <= -45) {
            y1 = 0;
            y2 = 10;
        } else if (pitch >= 45) {
            y1 = -10;
            y2 = 0;
        } else {
            if (yaw > -45 && yaw <= 45) {
                z1 = 0;
                z2 = 10;
            } else if (yaw > 45 && yaw <= 135) {
                x1 = -10;
                x2 = 0;
            } else if (yaw > 135 || yaw <= -135) {
                z1 = -10;
                z2 = 0;
            } else if (yaw > -135 && yaw <= -45) {
                x1 = 0;
                x2 = 10;
            }
        }

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    Location newLocation = new Location(pl.getWorld(), pl.getBlockX() + x, pl.getBlockY() + y, pl.getBlockZ() + z);

                }
            }
        }
    }

}
