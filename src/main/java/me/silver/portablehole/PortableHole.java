package me.silver.portablehole;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PortableHole extends JavaPlugin {

    private static PortableHole instance;

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getServer().getPluginManager().registerEvents(new TheHoleDamnListener(), this);
        this.getCommand("hole").setExecutor(new HoleCommand());
    }

    public static PortableHole getInstance() {
        return instance;
    }

}
