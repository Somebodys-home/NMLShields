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

import java.util.HashMap;
import java.util.Map;

public class ShieldListener implements Listener {
    private GuardingSystem guardingSystem;

    public ShieldListener(NMLShields nmlShields) {
        guardingSystem = nmlShields.getGuardingSystem();
    }

    @EventHandler()
    public void shieldLevelCheck(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItem(event.getNewSlot());
        boolean usable = ItemSystem.isItemUsable(heldItem, player);

        if (heldItem == null || heldItem.getType() == Material.AIR) { return; }
        if (!heldItem.hasItemMeta()) { return; }
        if (ItemSystem.getItemType(heldItem) == null) { return; }
        if (!usable) {
            player.sendMessage("§c⚠ §nYou are too inexperienced for this item!§r§c ⚠");
        }

        ItemSystem.updateUnusableItemName(heldItem, usable);
    }

    @EventHandler
    public void blockIncomingDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.isBlocking()) {
            guardingSystem.damageBar(player, event.getDamage());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void blockIncomingCustomDamage(CustomDamageEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (player.isBlocking()) {
            event.setCancelled(true);

            double totalDamage = 0;
            HashMap<DamageType, Double> damageSplits = event.getDamageSplits();

            for (Map.Entry<DamageType, Double> entry : damageSplits.entrySet()) {
                totalDamage += entry.getValue();
            }

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
