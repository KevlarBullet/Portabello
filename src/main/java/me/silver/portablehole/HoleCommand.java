package me.silver.portablehole;

import net.minecraft.server.v1_12_R1.NBTTagByte;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HoleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "Error: Command can only be sent by a player");
            return true;
        }

        ItemStack portableHole = new ItemStack(Material.EYE_OF_ENDER);
        ItemMeta itemMeta = portableHole.getItemMeta();

        itemMeta.setDisplayName("\u00a7c\u00a7lPortable Hole");
        itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        portableHole.setItemMeta(itemMeta);

        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(portableHole);
        nmsStack.a("isHole", new NBTTagByte((byte) 1));
//        NBTTagCompound tagCompound = nmsStack.getTag();
//        tagCompound.setByte("isHole", (byte) 1);
//        nmsStack.setTag(tagCompound);

        player.getInventory().addItem(CraftItemStack.asBukkitCopy(nmsStack));

        return true;
    }

}
