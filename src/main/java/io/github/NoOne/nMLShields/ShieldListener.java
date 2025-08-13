package io.github.NoOne.nMLShields;

import io.github.NoOne.nMLArmor.ArmorChangeEvent;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.ItemType;
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
    private NMLShields nmlShields;

    public ShieldListener(NMLShields nmlShields) {
        this.nmlShields = nmlShields;
    }

    @EventHandler
    public void updatePlayerStatsWhenHoldingShield(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newHand = player.getInventory().getItem(event.getNewSlot());
        ItemStack oldHand = player.getInventory().getItem(event.getPreviousSlot());
        
        Bukkit.getPluginManager().callEvent(new ArmorChangeEvent(player, oldHand, newHand));
    }

    @EventHandler
    public void updatePlayerStatsWhenInterractingWithMainhandShield(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerInventory playerInventory = player.getInventory();
        ItemStack cursorItem = event.getCursor();
        ItemStack clickedItem = playerInventory.getItem(event.getSlot());

        if (clickedItem != null) {
            clickedItem = clickedItem.clone(); // lock in the pre-click item
        }

        // putting shield in main hand
        if (ItemSystem.getItemTypeFromItemStack(cursorItem) == ItemType.SHIELD && event.getSlot() == playerInventory.getHeldItemSlot()) {
            Bukkit.getPluginManager().callEvent(new ArmorChangeEvent(player, playerInventory.getItem(event.getSlot()), cursorItem));
            return;
        }

        ItemStack finalClickedItem = clickedItem;
        Bukkit.getScheduler().runTaskLater(nmlShields , () -> {
            ItemStack newItem = playerInventory.getItem(event.getSlot()); // REFRESH
            if (newItem == null) {
                newItem = new ItemStack(Material.AIR);
            }

            // for taking shields out of your main hand
            if (ItemSystem.getItemTypeFromItemStack(finalClickedItem) == ItemType.SHIELD && event.getSlot() == playerInventory.getHeldItemSlot()) {
                Bukkit.getPluginManager().callEvent(new ArmorChangeEvent(player, finalClickedItem, newItem));
            }
        }, 1L);
    }

//    @EventHandler
//    public void updatePlayerStatsWhenEquippingShield(InventoryClickEvent event) {
//        if (!(event.getWhoClicked() instanceof Player player)) return;
//        if (event.getClickedInventory() == null) return;
//
//        ClickType click = event.getClick();
//        int rawSlot = event.getRawSlot();
//
//        if ((click == ClickType.LEFT || click == ClickType.RIGHT || click == ClickType.CREATIVE) && (click != ClickType.SHIFT_LEFT || click != ClickType.SHIFT_RIGHT)) {
//            if (ItemSystem.getItemTypeFromItemStack(event.getCursor()) == ItemType.SHIELD && rawSlot == 45) {
//                if (ItemSystem.isItemUsable(event.getCursor(), player)) {
//                    player.sendMessage("sneed (manually put into offhand)");
////            shieldManager.addShieldStatsToPlayerStats(player, maybeShield);
////            ItemSystem.updateUnusableItemName(maybeShield, true);
//                } else {
//                    event.setCancelled(true);
//                    player.sendMessage("§c⚠ §nYou are too inexperienced for this item!§r§c ⚠1");
//                }
//            }
//        } else if (click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT) {
//            if (shieldManager.isACustomShield(event.getCurrentItem()) && rawSlot != 45) {
//                if (ItemSystem.isItemUsable(event.getCurrentItem(), player)) {
//                    player.sendMessage("sneed (shift clicked into offhand)");
////                shieldManager.addShieldStatsToPlayerStats(player, maybeShield);
////                ItemSystem.updateUnusableItemName(maybeShield, true);
//                } else {
//                    event.setCancelled(true);
//                    player.sendMessage("§c⚠ §nYou are too inexperienced for this item!§r§c ⚠2");
//                }
//            }
//        } else if (click == ClickType.NUMBER_KEY) {
//            if (shieldManager.isACustomShield(player.getInventory().getItem(event.getHotbarButton()))) {
//                if (ItemSystem.isItemUsable(player.getInventory().getItem(event.getHotbarButton()), player)) {
//                    player.sendMessage("sneed (num key clicked into offhand)");
////                shieldManager.addShieldStatsToPlayerStats(player, maybeShield);
////                ItemSystem.updateUnusableItemName(maybeShield, true);
//                } else {
//                    event.setCancelled(true);
//                    player.sendMessage("§c⚠ §nYou are too inexperienced for this item!§r§c ⚠3");
//                }
//            }
//        } else if (click == ClickType.SWAP_OFFHAND) {
//            if (shieldManager.isACustomShield(event.getCurrentItem())) {
//                if (ItemSystem.isItemUsable(event.getCurrentItem(), player)) {
//                    player.sendMessage("sneed (swap offhand)");
////                shieldManager.addShieldStatsToPlayerStats(player, maybeShield);
////                ItemSystem.updateUnusableItemName(maybeShield, true);
//                } else {
//                    event.setCancelled(true);
//                    player.sendMessage("§c⚠ §nYou are too inexperienced for this item!§r§c ⚠4");
//                }
//            }
//        }
//    }

//    @EventHandler
//    public void updatePlayerStatsWhenUnequippingShield(InventoryClickEvent event) {
//        if (!(event.getWhoClicked() instanceof Player player)) return;
//        if (event.getClickedInventory() == null) return;
//
//        ClickType click = event.getClick();
//        int rawSlot = event.getRawSlot();
//
//        player.sendMessage("click: " + click);
//        player.sendMessage("raw slot: " + rawSlot);
//        player.sendMessage("event slot: " + event.getSlot());
//        player.sendMessage("cursor itemtype: " + event.getCursor().getType());
//        player.sendMessage("current item itemtype: " + event.getCurrentItem().getType());
//
//        // Case 1: Direct left/right click to remove shield from offhand
//        if ((click == ClickType.LEFT || click == ClickType.RIGHT || click == ClickType.CREATIVE) && slot == 40 && shieldManager.isACustomShield(shield)) {
////            shieldManager.removeShieldStatsFromPlayerStats(player, shield);
//        } else if ((click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT)) {// Case 2: Shift-click shield from offhand back into inventory
//            if (shieldManager.isACustomShield(shield)) {
//                // Only remove if the item is currently in offhand
//                if (shield.equals(player.getInventory().getItemInOffHand())) {
//                    shieldManager.removeShieldStatsFromPlayerStats(player, shield);
//                    ItemSystem.updateUnusableItemName(shield, true);
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
        boolean usable = ItemSystem.isItemUsable(heldItem, player);

        if (heldItem == null || heldItem.getType() == Material.AIR) { return; }
        if (!heldItem.hasItemMeta()) { return; }
        if (ItemSystem.getItemTypeFromItemStack(heldItem) == null) { return; }
        if (!usable) {
            player.sendMessage("§c⚠ §nYou are too inexperienced for this item!§r§c ⚠");
        }

        ItemSystem.updateUnusableItemName(heldItem, usable);
    }

//    @EventHandler
//    public void blockEquippingUnusableShield(InventoryClickEvent event) {
//        if (!(event.getWhoClicked() instanceof Player player)) return;
//        if (event.getClickedInventory() == null) return;
//
//        ClickType click = event.getClick();
//        int rawSlot = event.getRawSlot();
////        player.sendMessage("click: " + click);
////        player.sendMessage("raw slot: " + rawSlot);
////        player.sendMessage("event slot: " + event.getSlot());
////        player.sendMessage("cursor itemtype: " + event.getCursor().getType());
////        player.sendMessage("current item itemtype: " + event.getCurrentItem().getType());
//
//        if (click == ClickType.LEFT || click == ClickType.RIGHT || click == ClickType.CREATIVE) {
//            if (shieldManager.isACustomShield(event.getCursor()) && rawSlot == 45) {
//                player.sendMessage("sneed (manually put into offhand)");
////            shieldManager.addShieldStatsToPlayerStats(player, maybeShield);
////            ItemSystem.updateUnusableItemName(maybeShield, true);
//            }
//        } else if (click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT) {
//            if (shieldManager.isACustomShield(event.getCurrentItem()) && rawSlot != 45) {
//                player.sendMessage("sneed (shift clicked into offhand)");
////                shieldManager.addShieldStatsToPlayerStats(player, maybeShield);
////                ItemSystem.updateUnusableItemName(maybeShield, true);
//            }
//        } else if (click == ClickType.NUMBER_KEY) {
//            if (shieldManager.isACustomShield(player.getInventory().getItem(event.getHotbarButton()))) {
//                player.sendMessage("sneed (num key clicked into offhand)");
//            }
//        } else if (click == ClickType.SWAP_OFFHAND) {
//            if (shieldManager.isACustomShield(event.getCurrentItem())) {
//                player.sendMessage("sneed (swap offhand)");
//            }
//        }
//    }
}
