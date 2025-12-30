package io.github.NoOne.nMLShields;

import io.github.NoOne.nMLItems.ItemRarity;
import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.itemDictionary.Shields;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenerateShieldCommand implements CommandExecutor, TabCompleter {
    private ProfileManager profileManager;

    public GenerateShieldCommand(NMLShields nmlShields) {
        profileManager = nmlShields.getProfileManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            int level = Integer.parseInt(args[0]);
            String rarity = args[1];
            ItemStack shield = Shields.generateShield(player, ItemRarity.getItemRarityFromString(rarity), level);
            PlayerInventory playerInventory = player.getInventory();

            playerInventory.addItem(shield);

            if (playerInventory.getItemInMainHand().isSimilar(shield)) {
                Stats stats = profileManager.getPlayerProfile(player.getUniqueId()).getStats();

                for (Map.Entry<String, Double> statEntry : ItemSystem.convertItemStatsToPlayerStats(shield).entrySet()) {
                    stats.add2Stat(statEntry.getKey(), statEntry.getValue());
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(List.of("<level>")).stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return new ArrayList<>(List.of("common", "uncommon", "rare", "mythical")).stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
