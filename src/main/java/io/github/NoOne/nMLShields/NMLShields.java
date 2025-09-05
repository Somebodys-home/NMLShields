package io.github.NoOne.nMLShields;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLShields extends JavaPlugin {
    private NMLShields instance;
    private static NMLPlayerStats nmlPlayerStats;
    private ProfileManager profileManager;
    private ShieldGenerator shieldGenerator;
    private GuardingSystem guardingSystem;

    @Override
    public void onEnable() {
        instance = this;
        guardingSystem = new GuardingSystem(this);
        guardingSystem.start();

        Plugin plugin = Bukkit.getPluginManager().getPlugin("NMLPlayerStats");
        if (plugin instanceof NMLPlayerStats statsPlugin) {
            nmlPlayerStats = statsPlugin;
            profileManager = nmlPlayerStats.getProfileManager();
        }

        shieldGenerator = new ShieldGenerator();

        getServer().getPluginManager().registerEvents(new ShieldListener(this), this);
        getCommand("generateShield").setExecutor(new GenerateShieldCommand(this));
    }

    @Override
    public void onDisable() {
        guardingSystem.stop();
    }

    public NMLShields getInstance() {
        return instance;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public static NMLPlayerStats getNmlPlayerStats() {
        return nmlPlayerStats;
    }

    public ShieldGenerator getShieldManager() {
        return shieldGenerator;
    }

    public GuardingSystem getBlockBar() {
        return guardingSystem;
    }

    public GuardingSystem getGuardingSystem() {
        return guardingSystem;
    }
}
