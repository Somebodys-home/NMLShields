package io.github.NoOne.nMLShields;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLShields extends JavaPlugin {
    private ProfileManager profileManager;
    private GuardingSystem guardingSystem;

    @Override
    public void onEnable() {
        profileManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getProfileManager();

        guardingSystem = new GuardingSystem(this);
        guardingSystem.start();

        getServer().getPluginManager().registerEvents(new ShieldListener(this), this);
    }

    @Override
    public void onDisable() {
        guardingSystem.stop();
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public GuardingSystem getBlockBar() {
        return guardingSystem;
    }

    public GuardingSystem getGuardingSystem() {
        return guardingSystem;
    }
}
