package io.github.NoOne.nMLShields;

import io.github.NoOne.nMLDefenses.DefenseManager;
import io.github.NoOne.nMLDefenses.NMLDefenses;
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
    private ItemSystem itemSystem;
    private NMLDefenses nmlDefenses;
    private DefenseManager defenseManager;
    private ShieldManager shieldManager;

    @Override
    public void onEnable() {
        instance = this;
        nmlItems = JavaPlugin.getPlugin(NMLItems.class);
        nmlDefenses = JavaPlugin.getPlugin(NMLDefenses.class);

        itemSystem = nmlItems.getItemSystem();
        defenseManager = nmlDefenses.getDefenseManager();

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

    public ItemSystem getItemSystem() {
        return itemSystem;
    }

    public DefenseManager getDefenseManager() {
        return defenseManager;
    }

    public ShieldManager getShieldManager() {
        return shieldManager;
    }
}
