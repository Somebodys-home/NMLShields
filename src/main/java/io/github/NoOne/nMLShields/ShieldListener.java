package io.github.NoOne.nMLShields;

import io.github.NoOne.nMLItems.ItemSystem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;
import java.util.UUID;

public class ShieldListener implements Listener {
    private ShieldManager shieldManager;
    private ItemSystem itemSystem;

    public ShieldListener(NMLShields nmlShields) {
        shieldManager = nmlShields.getShieldManager();
        itemSystem = nmlShields.getItemSystem();
    }

    @EventHandler
    public void updatePlayerStatsWhenHoldingShield(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newHand = player.getInventory().getItem(event.getNewSlot());
        ItemStack oldHand = player.getInventory().getItem(event.getPreviousSlot());

        if (shieldManager.isACustomShield(newHand)) {
            shieldManager.addShieldStatsToPlayerStats(player, newHand);
        }
        if (shieldManager.isACustomShield(oldHand)) {
            shieldManager.removeShieldStatsFromPlayerStats(player, oldHand);
        }
    }

    @EventHandler
    public void updatePlayerStatsWhenEquippingShield(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;

        ClickType click = event.getClick();
        int rawSlot = event.getRawSlot();
        ItemStack maybeShield = event.getCursor();
//        player.sendMessage("click: " + click);
//        player.sendMessage("raw slot: " + rawSlot);
//        player.sendMessage("event slot: " + event.getSlot());
//        player.sendMessage("cursor itemtype: " + maybeShield.getType());
//        player.sendMessage("current item itemtype: " + event.getCurrentItem().getType());

        if (click == ClickType.LEFT || click == ClickType.RIGHT || click == ClickType.CREATIVE) {
            if (shieldManager.isACustomShield(event.getCursor()) && rawSlot == 45) {
                player.sendMessage("sneed (manually put into offhand)");
//            shieldManager.addShieldStatsToPlayerStats(player, maybeShield);
//            itemSystem.updateUnusableItemName(maybeShield, true);
            }
        } else if (click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT) {
            if (shieldManager.isACustomShield(event.getCurrentItem()) && rawSlot != 45) {
                player.sendMessage("sneed (shift clicked into offhand)");
//                shieldManager.addShieldStatsToPlayerStats(player, maybeShield);
//                itemSystem.updateUnusableItemName(maybeShield, true);
            }
        } else if (click == ClickType.NUMBER_KEY) {
            if (shieldManager.isACustomShield(player.getInventory().getItem(event.getHotbarButton()))) {
                player.sendMessage("sneed (num key clicked into offhand)");
            }
        } else if (click == ClickType.SWAP_OFFHAND) {
            if (shieldManager.isACustomShield(event.getCurrentItem())) {
                player.sendMessage("sneed (swap offhand)");
            }
        }
    }

//    @EventHandler
//    public void updatePlayerStatsWhenUnequippingShield(InventoryClickEvent event) {
//        if (!(event.getWhoClicked() instanceof Player player)) return;
//        if (event.getClickedInventory() == null) return;
//
//        ClickType click = event.getClick();
//        int slot = event.getSlot();
//        ItemStack shield = event.getCurrentItem();
//
//        // Case 1: Direct left/right click to remove shield from offhand
//        if ((click == ClickType.LEFT || click == ClickType.RIGHT || click == ClickType.CREATIVE) && slot == 40 && shieldManager.isACustomShield(shield)) {
////            shieldManager.removeShieldStatsFromPlayerStats(player, shield);
//        } else if ((click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT)) {// Case 2: Shift-click shield from offhand back into inventory
//            if (shieldManager.isACustomShield(shield)) {
//                // Only remove if the item is currently in offhand
//                if (shield.equals(player.getInventory().getItemInOffHand())) {
//                    shieldManager.removeShieldStatsFromPlayerStats(player, shield);
//                    itemSystem.updateUnusableItemName(shield, true);
//                }
//            }
//        } else if (click == ClickType.NUMBER_KEY && slot == 40) { // Case 3: Hotkey swap shield out of offhand
//            if (shieldManager.isACustomShield(shield)) {
//                shieldManager.removeShieldStatsFromPlayerStats(player, shield);
//            }
//        }
//    }

    @EventHandler()
    public void shieldLevelCheck(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItem(event.getNewSlot());
        boolean usable = itemSystem.isItemUsable(heldItem, player);

        if (heldItem == null || heldItem.getType() == Material.AIR) { return; }
        if (!heldItem.hasItemMeta()) { return; }
        if (itemSystem.getItemTypeFromItemStack(heldItem) == null) { return; }
        if (!usable) {
            player.sendMessage("§c⚠ §nYou are too inexperienced for this item!§r§c ⚠");
        }

        itemSystem.updateUnusableItemName(heldItem, usable);
    }

//    @EventHandler
//    public void blockEquippingUnusableShield(InventoryClickEvent event) {
//        if (!(event.getWhoClicked() instanceof Player player)) return;
//        if (event.getClickedInventory() == null) return;
//
//        ClickType click = event.getClick();
//        if (click == ClickType.LEFT || click == ClickType.RIGHT) {
//            if (event.getSlot() == 40) {
//                ItemStack shield = event.getCursor();
//
//                if (shieldManager.isACustomShield(shield) && !itemSystem.isItemUsable(shield, player)) {
//                    event.setCancelled(true);
//                    player.sendMessage("§c⚠ §nYou are too inexperienced for this item!§r§c ⚠");
//                    itemSystem.updateUnusableItemName(shield, false);
//                }
//            }
//        } else if (click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT) {
//            ItemStack armor = event.getCurrentItem();
//            if (shieldManager.isACustomShield(armor) && !itemSystem.isItemUsable(armor, player)) {
//                event.setCancelled(true);
//                player.sendMessage("§c⚠ §nYou are too inexperienced for this item!§r§c ⚠");
//                itemSystem.updateUnusableItemName(armor, false);
//            }
//        }
//    }
//
//    @EventHandler
//    public void blockRightClickEquippingUnusableShieldFromHand(PlayerInteractEvent event) {
//        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
//            Player player = event.getPlayer();
//            ItemStack item = event.getItem();
//
//            if (shieldManager.isACustomShield(item) && !itemSystem.isItemUsable(item, player)) {
//                itemSystem.updateUnusableItemName(item, false);
//                event.setCancelled(true);
//                player.sendMessage("§c⚠ §nYou are too inexperienced for this item!§r§c ⚠");
//            }
//        }
//    }
}
