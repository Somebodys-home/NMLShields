package io.github.NoOne.nMLShields;

import io.github.NoOne.nMLItems.ItemSystem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class ShieldListener implements Listener {
    private NMLShields nmlShields;
    private GuardingSystem guardingSystem;

    public ShieldListener(NMLShields nmlShields) {
        this.nmlShields = nmlShields;
        guardingSystem = nmlShields.getBlockBar();
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

//    @EventHandler // todo: eventually get to blocking damage
//    public void blockIncomingDamage(CustomDa event) {
//        if (!(event.getEntity() instanceof Player player)) return;
//        if (player.isBlocking()) {
//            guardingSystem.damageBar(player, event.getDamage());
//        }
//    }
}
