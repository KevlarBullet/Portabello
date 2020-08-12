package me.silver.portablehole;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// This whole class is kinda yikes, not gonna lie
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
                            this.digHole(player);
                            this.startCooldown(player, itemStack);
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

        ArrayList<PacketPlayOutMultiBlockChange> packetsToSend = new ArrayList<>();

        // 5 nested for loops is kinda yikes
        for (int cx = x1 >> 4; cx <= x2 >> 4; cx++) {
            for (int cz = z1 >> 4; cz <= z2 >> 4; cz++) {
                int xMin = Math.max(cx << 4, x1) % 16;
                int xMax = Math.min((cx << 4) + 15, x2) % 16;
                int zMin = Math.max(cz << 4, z1) % 16;
                int zMax = Math.min((cz << 4) + 15, z2) % 16;
                int width = (xMax - xMin);
                int depth = (zMax - zMin);
                int height = (y2 - y1);
                int count = width * depth * height;

                Chunk chunk = ((CraftWorld) player.getWorld()).getHandle().getChunkAt(cx, cz);
                short[] blockChangeInfo = new short[count];

                for (int y = y1; y <= y2; y++) {
                    for (int x = xMin; x <= xMax; x++) {
                        for (int z = zMin; z <= zMax; z++) {
                            // Create block change info
                            int index = (width * depth * y) + (depth * x) + z;
                            short blockPos = (short) ((y & 0xFF) | ((z & 0xF) << 8) | ((x & 0xF) << 12));
                            blockChangeInfo[index] = blockPos;

                            // Get and store block data for replacing blocks
                            Block block = player.getWorld().getBlockAt(cx + x, y, cz + z);
                            Location location = block.getLocation();
                            Material material = block.getType();
                            MaterialData materialData = block.getState().getData();
                            byte data = block.getData();

                            BlockHolder holder = new BlockHolder(location, material, materialData, data);

                            // Delete block
                            
                        }
                    }
                }

                PacketPlayOutMultiBlockChange multiBlockChange = new PacketPlayOutMultiBlockChange(count, blockChangeInfo, chunk);
                packetsToSend.add(multiBlockChange);
            }
        }

        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (Player other : Bukkit.getServer().getOnlinePlayers()) {
                if (other != player) {
                    EntityPlayer nmsPlayer = ((CraftPlayer) other).getHandle();

                    for (PacketPlayOutMultiBlockChange packet : packetsToSend) {
                        nmsPlayer.playerConnection.sendPacket(packet);
                    }
                }
            }
        }, 0L);
    }

}
