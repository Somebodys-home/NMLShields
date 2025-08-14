package io.github.NoOne.nMLShields;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class BlockBar {
    private NMLShields nmlShields;
    private BukkitTask playerBlockingTask;
    private final HashMap<UUID, BossBar> playerBars = new HashMap<>();
    private final HashMap<UUID, BukkitTask> regenBlockTasks = new HashMap<>();
    private final HashMap<UUID, Integer> damageCooldowns = new HashMap<>();
    private static int DAMAGE_BAR_DURATION = 40;

    public BlockBar(NMLShields nmlShields) {
        this.nmlShields = nmlShields;
    }

    public void start() {
        playerBlockingTask = Bukkit.getScheduler().runTaskTimer(nmlShields, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();
                BossBar bar = playerBars.computeIfAbsent(playerId, id -> {
                    BossBar newBar = Bukkit.createBossBar("Block", BarColor.WHITE, BarStyle.SOLID);
                    newBar.addPlayer(player);
                    newBar.setProgress(1);
                    return newBar;
                });

                int cooldown = damageCooldowns.getOrDefault(playerId, 0);
                //player.sendMessage(String.valueOf(cooldown));

                BukkitTask regenTask = regenBlockTasks.get(playerId);
                if (player.isBlocking() || (regenTask != null && !regenTask.isCancelled()) || cooldown > 0) {
                    bar.addPlayer(player);
                } else {
                    bar.removePlayer(player);
                }

                if (cooldown > 0) damageCooldowns.put(playerId, cooldown - 1);
            }
        }, 0L, 1L);
    }

    public void stop() {
        if (playerBlockingTask != null) {
            playerBlockingTask.cancel();
        }

        for (BukkitTask task : regenBlockTasks.values()) {
            if (task != null) task.cancel();
        }
        regenBlockTasks.clear();

        for (BossBar bar : playerBars.values()) {
            bar.removeAll();
        }
        playerBars.clear();
    }

    public void damageBar(Player player, double damage) {
        UUID uuid = player.getUniqueId();
        BossBar blockBar = playerBars.get(uuid);
        int blockStat = nmlShields.getNmlPlayerStats().getProfileManager().getPlayerProfile(uuid).getStats().getBlock();
        double damageFraction = damage / blockStat;
        double newProgress = Math.max(0, blockBar.getProgress() - damageFraction);

        if (regenBlockTasks.containsKey(uuid)) {
            regenBlockTasks.get(uuid).cancel();
            regenBlockTasks.remove(uuid);
        }

        blockBar.setProgress(newProgress);
        blockBar.setColor(BarColor.RED);
        blockBar.setTitle("§cBlock");
        damageCooldowns.put(uuid, DAMAGE_BAR_DURATION);

        if (blockBar.getProgress() == 0) {
            guardBreak(player);
        }

        // Schedule regen to start after short delay
        Bukkit.getScheduler().runTaskLater(nmlShields, () -> {
            blockBar.setColor(BarColor.WHITE);
            blockBar.setTitle("Block");
            startRegenBlockTask(player);
        }, damageCooldowns.get(player.getUniqueId()));
        //player.sendMessage("cooldown after damage: " + damageCooldowns.get(player.getUniqueId()));
    }

    private void startRegenBlockTask(Player player) {
        UUID uuid = player.getUniqueId();
        if (regenBlockTasks.containsKey(uuid)) return;
        regenBlockTasks.put(uuid, null);

        player.sendMessage("started regen at: " + damageCooldowns.get(uuid));

        BossBar blockBar = playerBars.get(uuid);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(nmlShields, () -> {
            if (blockBar.getProgress() >= 1) {
                blockBar.setProgress(1);
                BukkitTask t = regenBlockTasks.remove(uuid);
                if (t != null) t.cancel();
                return;
            }
            blockBar.setProgress(Math.min(1, blockBar.getProgress() + 0.0015));
        }, 0L, 1L);

        regenBlockTasks.put(uuid, task);
    }

    public void guardBreak(Player player) {
        player.sendTitle("GUARD BREAK!", "", 10, 40, 20);

        // attempt 1
        ItemStack shield = player.getInventory().getItemInOffHand();
        player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        Bukkit.getScheduler().runTaskLater(nmlShields, () -> {
            player.getInventory().setItemInOffHand(shield);
        }, 1L);

        //player.swingOffHand();

        player.setCooldown(Material.SHIELD, 40);
        damageCooldowns.put(player.getUniqueId(), damageCooldowns.get(player.getUniqueId()) + 40);
    }
}