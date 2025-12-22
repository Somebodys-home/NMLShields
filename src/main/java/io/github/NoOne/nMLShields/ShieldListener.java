package io.github.NoOne.nMLShields;

import io.github.NoOne.damagePlugin.customDamage.CustomDamageEvent;
import io.github.NoOne.damagePlugin.customDamage.DamageType;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.ItemType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.HashMap;
import java.util.Map;

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

    @EventHandler
    public void dontUseMainHandShields(PlayerInteractEvent event) {
        if (ItemSystem.getItemType(event.getPlayer().getInventory().getItemInMainHand()) == ItemType.SHIELD &&
            (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {

            event.setCancelled(true);
            event.getPlayer().sendMessage("§c⚠ §nShields are offhand exclusive!§r§c ⚠");
        }
    }
}
