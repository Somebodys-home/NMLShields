package io.github.NoOne.nMLShields;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.StatConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLShields extends JavaPlugin {
    private StatConfigManager statConfigManager;
    private GuardingSystem guardingSystem;

    @Override
    public void onEnable() {
        statConfigManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getStatConfigManager();

        guardingSystem = new GuardingSystem(this);
        guardingSystem.start();

        getServer().getPluginManager().registerEvents(new ShieldListener(this), this);
    }

    @Override
    public void onDisable() {
        guardingSystem.stop();
    }

    public StatConfigManager getStatConfigManager() {
        return statConfigManager;
    }

    public GuardingSystem getGuardingSystem() {
        return guardingSystem;
    }
}
