package io.github.NoOne.nMLShields;

import io.github.NoOne.nMLItems.ItemRarity;
import io.github.NoOne.nMLItems.ItemStat;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.ItemType;
import io.github.NoOne.nMLPlayerStats.statSystem.StatChangeEvent;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.Bukkit;
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

public class ShieldManager {
    private NMLShields nmlShields;

    public ShieldManager(NMLShields nmlShields) {
        this.nmlShields = nmlShields;
    }

    public ItemStack generateShield(Player receiver, ItemRarity rarity, int level) {
        ItemStack shield = new ItemStack(Material.SHIELD);
        ItemMeta meta = shield.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        List<String> lore = new ArrayList<>();

        pdc.set(ItemSystem.makeItemTypeKey(ItemType.SHIELD), PersistentDataType.INTEGER, 1);
        pdc.set(ItemSystem.makeItemRarityKey(rarity), PersistentDataType.INTEGER, 1);
        pdc.set(ItemSystem.getLevelKey(), PersistentDataType.INTEGER, level);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        shield.setItemMeta(meta);

        String name = generateShieldName(rarity, level);
        meta.setDisplayName(name);
        pdc.set(ItemSystem.getOriginalNameKey(), PersistentDataType.STRING, name);

        lore.add(ItemRarity.getItemRarityColor(rarity) + "" + ChatColor.BOLD + ItemRarity.getItemRarityString(rarity).toUpperCase() + " " + ItemType.getItemTypeString(ItemType.SHIELD).toUpperCase());
        lore.add("");
        meta.setLore(lore);
        shield.setItemMeta(meta);

        generateShieldStats(shield, rarity, level);
        ItemSystem.updateUnusableItemName(shield, ItemSystem.isItemUsable(shield, receiver));

        return shield;
    }

    public String generateShieldName(ItemRarity rarity, int level) {
        String[] nameSegments = null;
        String name = "";

        if (rarity == ItemRarity.COMMON) {

            nameSegments = new String[2];
            List<String> badAdjectives = new ArrayList<>(List.of("Garbage", "Awful", "Do Better", "Babies' First", "Oh God That", "Rotten", "Poor", "Degrading", "Forgotten", "Racist"));

            nameSegments[0] = badAdjectives.get(ThreadLocalRandom.current().nextInt(badAdjectives.size()));

        } else if (rarity == ItemRarity.UNCOMMON) {

            nameSegments = new String[2];
            List<String> goodAdjectives = new ArrayList<>(List.of("Pretty Alright", "Hand-me-downed", "Based", "W", "Neato Dorito", "Goofy Ahh", "Nobodies'"));
            int randomAdjective = ThreadLocalRandom.current().nextInt(goodAdjectives.size());

            nameSegments[0] = goodAdjectives.get(randomAdjective);

        } else if (rarity == ItemRarity.RARE) {

            nameSegments = new String[3];
            List<String> goodAdjectives = new ArrayList<>(List.of("Pretty Alright", "Solid", "Well-Made", "Lifelong", "Based", "W", "Almost Mythical", "Neato Dorito", "Goofy Ahh", "Nobodies'"));
            int randomAdjective = ThreadLocalRandom.current().nextInt(goodAdjectives.size());

            nameSegments[0] = goodAdjectives.get(randomAdjective);
            goodAdjectives.remove(randomAdjective);
            nameSegments[1] = goodAdjectives.get(ThreadLocalRandom.current().nextInt(goodAdjectives.size()));

        } else if (rarity == ItemRarity.MYTHICAL) {

            nameSegments = new String[3];
            List<String> greatAdjectives = new ArrayList<>(List.of("Amazing", "Godly", "King's", "Fabled", "Based", "W", "Legendary", "Goofy Ahh", "Nobodies'"));
            int randomAdjective = ThreadLocalRandom.current().nextInt(greatAdjectives.size());

            nameSegments[0] = greatAdjectives.get(randomAdjective);
            greatAdjectives.remove(randomAdjective);
            nameSegments[1] = greatAdjectives.get(ThreadLocalRandom.current().nextInt(greatAdjectives.size()));

        }

        assert nameSegments != null;
        List<String> shield = new ArrayList<>(List.of("Shield", "Buckler"));
        nameSegments[nameSegments.length - 1] = shield.get(ThreadLocalRandom.current().nextInt(shield.size()));

        name += "§o§fLv. " + level + "§r " + ItemRarity.getItemRarityColor(rarity);
        for (int i = 0; i < nameSegments.length; i++) {
            if (i == nameSegments.length - 1) {
                name += nameSegments[i];
            } else {
                name += nameSegments[i] + " ";
            }
        }

        return name;
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

        ItemSystem.updateLoreWithItemStats(shield);
    }

    public void addShieldStatsToPlayerStats(Player player, ItemStack shield) {
        if (ItemSystem.getItemTypeFromItemStack(shield) == ItemType.SHIELD) {
            HashMap<ItemStat, Double> defenseMap = ItemSystem.getAllStats(shield);
            Stats stats = nmlShields.getProfileManager().getPlayerProfile(player.getUniqueId()).getStats();

            for (Map.Entry<ItemStat, Double> stat : defenseMap.entrySet()) {
                switch (stat.getKey()) {
                    case GUARD -> stats.add2Stat("guard", stat.getValue().intValue());
                    case DEFENSE -> stats.add2Stat("defense", stat.getValue().intValue());
                    case OVERHEALTH -> stats.add2Stat("maxoverhealth", stat.getValue());
                    case PHYSICALRESIST -> stats.add2Stat("physicalresist", stat.getValue().intValue());
                    case FIRERESIST -> stats.add2Stat("fireresist", stat.getValue().intValue());
                    case COLDRESIST -> stats.add2Stat("coldresist", (stat.getValue().intValue()));
                    case EARTHRESIST -> stats.add2Stat("earthresist", stat.getValue().intValue());
                    case LIGHTNINGRESIST -> stats.add2Stat("lightningresist", stat.getValue().intValue());
                    case AIRRESIST -> stats.add2Stat("airresist", stat.getValue().intValue());
                    case LIGHTRESIST -> stats.add2Stat("lightresist", stat.getValue().intValue());
                    case DARKRESIST -> stats.add2Stat("darkresist", stat.getValue().intValue());
                }

                Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, ItemStat.getStatString(stat.getKey()).toLowerCase()));
            }
        }
    }

    public void removeShieldStatsFromPlayerStats(Player player, ItemStack shield) {
        if (ItemSystem.getItemTypeFromItemStack(shield) == ItemType.SHIELD) {
            HashMap<ItemStat, Double> defenseMap = ItemSystem.getAllStats(shield);
            Stats stats = nmlShields.getProfileManager().getPlayerProfile(player.getUniqueId()).getStats();

            for (Map.Entry<ItemStat, Double> stat : defenseMap.entrySet()) {
                switch (stat.getKey()) {
                    case GUARD -> stats.removeFromStat("guard", stat.getValue().intValue());
                    case DEFENSE -> stats.removeFromStat("defense", stat.getValue().intValue());
                    case OVERHEALTH -> stats.removeFromStat("maxoverhealth", stat.getValue());
                    case PHYSICALRESIST -> stats.removeFromStat("physicalresist", stat.getValue().intValue());
                    case FIRERESIST -> stats.removeFromStat("fireresist", stat.getValue().intValue());
                    case COLDRESIST -> stats.removeFromStat("coldresist", (stat.getValue().intValue()));
                    case EARTHRESIST -> stats.removeFromStat("earthresist", stat.getValue().intValue());
                    case LIGHTNINGRESIST -> stats.removeFromStat("lightningresist", stat.getValue().intValue());
                    case AIRRESIST -> stats.removeFromStat("airresist", stat.getValue().intValue());
                    case LIGHTRESIST -> stats.removeFromStat("lightresist", stat.getValue().intValue());
                    case DARKRESIST -> stats.removeFromStat("darkresist", stat.getValue().intValue());
                }

                Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, ItemStat.getStatString(stat.getKey()).toLowerCase()));
            }
        }
    }
}
