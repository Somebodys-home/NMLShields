package io.github.NoOne.nMLShields;

import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.NMLItems;
import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLShields extends JavaPlugin {
    private NMLShields instance;
    private static NMLPlayerStats nmlPlayerStats;
    private ProfileManager profileManager;
    private NMLItems nmlItems;
    private ShieldManager shieldManager;

    @Override
    public void onEnable() {
        instance = this;
        nmlItems = JavaPlugin.getPlugin(NMLItems.class);

        Plugin plugin = Bukkit.getPluginManager().getPlugin("NMLPlayerStats");
        if (plugin instanceof NMLPlayerStats statsPlugin) {
            nmlPlayerStats = statsPlugin;
            profileManager = nmlPlayerStats.getProfileManager();
        }

        shieldManager = new ShieldManager(this);

        getServer().getPluginManager().registerEvents(new ShieldListener(this), this);
        getCommand("generateShield").setExecutor(new GenerateShieldCommand(this));
    }

    public NMLShields getInstance() {
        return instance;
    }

    public NMLItems getNmlItems() {
        return nmlItems;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public static NMLPlayerStats getNmlPlayerStats() {
        return nmlPlayerStats;
    }

    public ShieldManager getShieldManager() {
        return shieldManager;
    }
}
