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
    private BlockBar blockBar;

    public ShieldListener(NMLShields nmlShields) {
        this.nmlShields = nmlShields;
        blockBar = nmlShields.getBlockBar();
    }

    @EventHandler()
    public void shieldLevelCheck(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItem(event.getNewSlot());
        boolean usable = ItemSystem.isItemUsable(heldItem, player);

        if (heldItem == null || heldItem.getType() == Material.AIR) { return; }
        if (!heldItem.hasItemMeta()) { return; }
        if (ItemSystem.getItemTypeFromItemStack(heldItem) == null) { return; }
        if (!usable) {
            player.sendMessage("§c⚠ §nYou are too inexperienced for this item!§r§c ⚠");
        }

        ItemSystem.updateUnusableItemName(heldItem, usable);
    }

    // todo: make this work eventually
//    @EventHandler
//    public void updatePlayerStatsWhenHoldingShield(PlayerItemHeldEvent event) {
//        Player player = event.getPlayer();
//        ItemStack newHand = player.getInventory().getItem(event.getNewSlot());
//        ItemStack oldHand = player.getInventory().getItem(event.getPreviousSlot());
//
//        Bukkit.getPluginManager().callEvent(new ArmorChangeEvent(player, oldHand, newHand));
//    }
//
//    @EventHandler
//    public void updatePlayerStatsWhenInterractingWithMainhandShield(InventoryClickEvent event) {
//        Player player = (Player) event.getWhoClicked();
//        PlayerInventory playerInventory = player.getInventory();
//        ItemStack cursorItem = event.getCursor();
//        ItemStack clickedItem = playerInventory.getItem(event.getSlot());
//
//        if (clickedItem != null) {
//            clickedItem = clickedItem.clone(); // lock in the pre-click item
//        }
//
//        // putting shield in main hand
//        if (ItemSystem.getItemTypeFromItemStack(cursorItem) == ItemType.SHIELD && event.getSlot() == playerInventory.getHeldItemSlot()) {
//            Bukkit.getPluginManager().callEvent(new ArmorChangeEvent(player, playerInventory.getItem(event.getSlot()), cursorItem));
//            return;
//        }
//
//        ItemStack finalClickedItem = clickedItem;
//        Bukkit.getScheduler().runTaskLater(nmlShields , () -> {
//            ItemStack newItem = playerInventory.getItem(event.getSlot()); // REFRESH
//            if (newItem == null) {
//                newItem = new ItemStack(Material.AIR);
//            }
//
//            // for taking shields out of your main hand
//            if (ItemSystem.getItemTypeFromItemStack(finalClickedItem) == ItemType.SHIELD && event.getSlot() == playerInventory.getHeldItemSlot()) {
//                Bukkit.getPluginManager().callEvent(new ArmorChangeEvent(player, finalClickedItem, newItem));
//            }
//        }, 1L);
//    }

    @EventHandler
    public void blockIncomingDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.isBlocking()) {
            blockBar.damageBar(player, event.getDamage());
        }
    }
}
