package io.github.NoOne.nMLShields;

import io.github.NoOne.nMLItems.ItemSystem;
import io.github.NoOne.nMLItems.ItemType;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class GuardingSystem {
    private NMLShields nmlShields;
    private ProfileManager profileManager;
    private BukkitTask playerGuardingTask;
    private final HashMap<UUID, BossBar> guardBars = new HashMap<>();
    private final HashMap<UUID, BukkitTask> ongoingRegenTasks = new HashMap<>();
    private final HashMap<UUID, Integer> damageCooldowns = new HashMap<>();
    private static int regenCooldown = 40;

    public GuardingSystem(NMLShields nmlShields) {
        this.nmlShields = nmlShields;
        profileManager = nmlShields.getProfileManager();
    }

    public void start() {
        // main task that runs every tick on the server
        playerGuardingTask = Bukkit.getScheduler().runTaskTimer(nmlShields, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID playerId = player.getUniqueId();

                // give every online player that doesnt have a block bar one
                BossBar bar = guardBars.computeIfAbsent(playerId, id -> {
                    BossBar newBar = Bukkit.createBossBar("Guard", BarColor.WHITE, BarStyle.SOLID);
                    newBar.addPlayer(player);
                    newBar.setProgress(1);
                    return newBar;
                });

                // get the cooldown of each player, or 0 if it doesnt exist
                int cooldown = damageCooldowns.getOrDefault(playerId, 0);

                BukkitTask regenTask = ongoingRegenTasks.get(playerId); // gets the block regen task of that player
                // make the player's block bar visible if they're blocking,
                // not regenerating block, when the bar is red from being damaged, or after its fully regened after the method
                if ((player.isBlocking() && ItemSystem.getItemType(player.getInventory().getItemInMainHand()) != ItemType.SHIELD) ||
                    (regenTask != null && !regenTask.isCancelled()) || cooldown > 0 || player.hasMetadata("fully regenerate guard")) {

                    bar.addPlayer(player);
                } else {
                    bar.removePlayer(player);
                }

                if (cooldown > 0) {
                    damageCooldowns.put(playerId, cooldown - 1); // cooldown goes down 1 / tick

                    if (cooldown - 1 == 0) { // when going off cooldown, start the regen task
                        bar.setColor(BarColor.WHITE);
                        bar.setTitle("Guard");
                        startRegenGuardTask(player);
                    }
                }
            }
        }, 0L, 1L);
    }

    public void stop() {
        if (playerGuardingTask != null) {
            playerGuardingTask.cancel();
        }

        for (BukkitTask task : ongoingRegenTasks.values()) {
            if (task != null) task.cancel();
        }
        ongoingRegenTasks.clear();

        for (BossBar bar : guardBars.values()) {
            bar.removeAll();
        }
        guardBars.clear();
    }

    public void damageBar(Player player, double damage) {
        UUID uuid = player.getUniqueId();
        BossBar guardBar = guardBars.get(uuid);
        int guardStat = profileManager.getPlayerProfile(uuid).getStats().getGuard();
        double damageFraction = damage / guardStat;
        double newProgress = guardBar.getProgress() - damageFraction;
        double guardbreakDamage = 0;

        if (newProgress < 0) {
            double leftoverFraction = Math.abs(newProgress);
            guardbreakDamage = leftoverFraction * guardStat;
            newProgress = 0;
        }

        // stops any regen tasks when damaged
        if (ongoingRegenTasks.containsKey(uuid)) {
            ongoingRegenTasks.get(uuid).cancel();
            ongoingRegenTasks.remove(uuid);
        }

        // damaged bar indicator
        guardBar.setProgress(newProgress);
        guardBar.setColor(BarColor.RED);
        guardBar.setTitle("§cGuard");
        damageCooldowns.put(uuid, regenCooldown);

        // potentially guard break player
        if (guardBar.getProgress() == 0) {
            guardBreak(player, guardbreakDamage);
        }
    }

    private void startRegenGuardTask(Player player) {
        UUID uuid = player.getUniqueId();

        // if that player already has a regen task running, leave
        if (ongoingRegenTasks.containsKey(uuid)) return;

        BossBar blockBar = guardBars.get(uuid);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(nmlShields, () -> { // actual regen task
            if (blockBar.getProgress() >= 1) {
                blockBar.setProgress(1);
                BukkitTask t = ongoingRegenTasks.remove(uuid);
                if (t != null) t.cancel();
                return;
            }
            blockBar.setProgress(Math.min(1, blockBar.getProgress() + 0.003));
        }, 0L, 1L);

        ongoingRegenTasks.put(uuid, task); // put the task on the hashmap
    }

    public void guardBreak(Player player, double carryoverDamage) {
        player.sendTitle("§c⚠ GUARD BREAK! ⚠", "", 10, 30, 5);
        guardBars.get(player.getUniqueId()).setTitle("§cGuard Break!");
        player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1f, 1f);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, .25f, .5f);
        player.damage(carryoverDamage);

        Vector knockback = player.getLocation().getDirection().multiply(-2.6);
        knockback.setY(0);
        player.setVelocity(knockback);

        Bukkit.getScheduler().runTask(nmlShields, () -> {
            Vector vel = player.getVelocity();
            vel.setY(0);
            player.setVelocity(vel);
        });

        ItemStack shield = player.getInventory().getItemInOffHand();
        player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        Bukkit.getScheduler().runTaskLater(nmlShields, () -> {
            player.getInventory().setItemInOffHand(shield);
        }, 1L);

        player.setCooldown(Material.SHIELD, 40);
    }

    public void fullyRegenerateGuard(Player player) {
        UUID uuid = player.getUniqueId();
        BossBar blockBar = guardBars.get(uuid);

        blockBar.setProgress(1);
        player.setMetadata("fully regenerate guard", new FixedMetadataValue(nmlShields, true));

        new BukkitRunnable() {
            @Override
            public void run() {
                player.removeMetadata("fully regenerate guard", nmlShields);
            }
        }.runTaskLater(nmlShields, 40L);
    }
}