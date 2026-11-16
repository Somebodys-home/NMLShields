package io.github.NoOne.nMLShields;

import io.github.NoOne.nMLDamages.DamageHelper;
import io.github.NoOne.nMLDamages.enums.DamageSource;
import io.github.NoOne.nMLDamages.event.CustomDamageEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ShieldListener implements Listener {
    private GuardingSystem guardingSystem;

    public ShieldListener(NMLShields nmlShields) {
        guardingSystem = nmlShields.getGuardingSystem();
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void blockIncomingCustomDamage(CustomDamageEvent event) {
        // player should block attacks if theyre the one being attacked, they're blocking, theyre out of no damage ticks
        // and they can't block environmental damage
        if (event.getDamaged() instanceof Player player && player.isBlocking() && player.getNoDamageTicks() == 0 && !event.getDamagerSource().equals(DamageSource.ENVIRONMENT)) {
            event.setCancelled(true);

            double totalDamage = DamageHelper.getTotalDamage(event.getDamage());

            player.setNoDamageTicks(player.getMaximumNoDamageTicks());
            player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1f, 1f);
            guardingSystem.damageBar(player, totalDamage);
        }
    }
}
