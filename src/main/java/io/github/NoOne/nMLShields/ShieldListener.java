package io.github.NoOne.nMLShields;

import io.github.NoOne.damagePlugin.customDamage.CustomDamageEvent;
import io.github.NoOne.damagePlugin.customDamage.DamageType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class ShieldListener implements Listener {
    private GuardingSystem guardingSystem;

    public ShieldListener(NMLShields nmlShields) {
        guardingSystem = nmlShields.getGuardingSystem();
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
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
