package io.github.NoOne.nMLShields;

import io.github.NoOne.damagePlugin.customDamage.CustomDamageEvent;
import io.github.NoOne.damagePlugin.customDamage.DamageType;
import io.github.NoOne.expertiseStylePlugin.abilitySystem.AbilityItemManager;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.ItemType;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShieldListener implements Listener {
    private GuardingSystem guardingSystem;

    public ShieldListener(NMLShields nmlShields) {
        guardingSystem = nmlShields.getGuardingSystem();
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void blockIncomingCustomDamage(CustomDamageEvent event) {
        if (event.getTarget() instanceof Player player && player.isBlocking()) {
            event.setCancelled(true);

            double totalDamage = 0;
            HashMap<DamageType, Double> damageSplits = event.getDamageSplits();

            for (Map.Entry<DamageType, Double> entry : damageSplits.entrySet()) {
                totalDamage += entry.getValue();
            }

            player.setNoDamageTicks(player.getMaximumNoDamageTicks());
            player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1f, 1f);
            guardingSystem.damageBar(player, totalDamage);
        }
    }
}
