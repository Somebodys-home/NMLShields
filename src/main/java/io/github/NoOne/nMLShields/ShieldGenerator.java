package io.github.NoOne.nMLShields;

import io.github.NoOne.nMLItems.ItemRarity;
import io.github.NoOne.nMLItems.ItemStat;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import static io.github.NoOne.nMLItems.ItemStat.*;
import static io.github.NoOne.nMLItems.ItemType.SHIELD;

public class ShieldGenerator {

    public ShieldGenerator() {
    }

    public ItemStack generateShield(Player receiver, ItemRarity rarity, int level) {
        ItemStack shield = new ItemStack(Material.SHIELD);
        ItemMeta meta = shield.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        List<String> lore = new ArrayList<>();

        pdc.set(ItemSystem.makeItemTypeKey(SHIELD), PersistentDataType.INTEGER, 1);
        pdc.set(ItemSystem.makeItemRarityKey(rarity), PersistentDataType.INTEGER, 1);
        pdc.set(ItemSystem.getLevelKey(), PersistentDataType.INTEGER, level);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        shield.setItemMeta(meta);

        String name = ItemSystem.generateItemName(SHIELD, null, rarity);
        meta.setDisplayName(name);
        pdc.set(ItemSystem.getOriginalNameKey(), PersistentDataType.STRING, name);

        lore.add("§o§fLv. " + level + "§r " + ItemRarity.getItemRarityColor(rarity) + ChatColor.BOLD + ItemRarity.getItemRarityString(rarity).toUpperCase() + " " + ItemType.getItemTypeString(SHIELD).toUpperCase());
        lore.add("");
        meta.setLore(lore);
        shield.setItemMeta(meta);

        generateShieldStats(shield, rarity, level);
        ItemSystem.updateUnusableItemName(shield, ItemSystem.isItemUsable(shield, receiver));

        return shield;
    }

    public void generateShieldStats(ItemStack shield, ItemRarity rarity, int level) {
        List<ItemStat> possibleSecondDefenseTypes = new ArrayList<>(List.of(GUARD, DEFENSE, OVERHEALTH, PHYSICALRESIST, FIRERESIST, COLDRESIST, EARTHRESIST, LIGHTNINGRESIST, AIRRESIST, LIGHTRESIST, DARKRESIST));
        int firstDefenseValue = (level * 5) + 10;
        ItemStat secondType = possibleSecondDefenseTypes.get(ThreadLocalRandom.current().nextInt(possibleSecondDefenseTypes.size()));
        int secondDefense = level;

        switch (rarity) {
            case COMMON -> {
                ItemSystem.setStat(shield, GUARD, firstDefenseValue);
            }
            case UNCOMMON, RARE -> {
                if (secondType == GUARD) {
                    ItemSystem.setStat(shield, GUARD, firstDefenseValue + secondDefense);
                } else {
                    ItemSystem.setStat(shield, GUARD, firstDefenseValue);
                    ItemSystem.setStat(shield, secondType, secondDefense);
                }
            }
            case MYTHICAL -> {
                firstDefenseValue = (level * 8) + 10;

                if (secondType == GUARD) {
                    ItemSystem.setStat(shield, GUARD, firstDefenseValue + secondDefense);
                } else {
                    ItemSystem.setStat(shield, GUARD, firstDefenseValue);
                    ItemSystem.setStat(shield, secondType, secondDefense);
                }
            }
        }

        ItemSystem.updateLoreWithStats(shield);
    }
}
