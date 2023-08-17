package me.badbones69.crazyrunes.controllers;

import java.util.ArrayList;
import java.util.UUID;
import me.badbones69.crazyrunes.ApiManager;
import me.badbones69.crazyrunes.CrazyRunes;
import me.badbones69.crazyrunes.api.PlayerRunes;
import me.badbones69.crazyrunes.api.enums.Rune;
import org.bukkit.Effect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class RuneListener implements Listener {

	private final CrazyRunes plugin = JavaPlugin.getPlugin(CrazyRunes.class);

	private final PlayerRunes playerRunes = this.plugin.getPlayerRunes();
	private final ApiManager apiManager = this.plugin.getApiManager();

	private final ArrayList<UUID> players = new ArrayList<>();

	public void setSpeed(Player player, Integer level) {
		if (level >= 1) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999 * 20, level - 1));
		} else {
			player.removePotionEffect(PotionEffectType.SPEED);
		}
	}

	public void setTank(Player player, Integer level) {
		int health = level * 6;
		player.setMaxHealth(20 + health);
	}

	public void setSurvivor(Player player, Boolean toggle) {
		if (toggle) {
			player.setSaturation(1000000);
			player.setFoodLevel(20);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 99999999 * 20, 10000));
		} else {
			player.setSaturation(20);
			player.setFoodLevel(20);
			player.removePotionEffect(PotionEffectType.SATURATION);
		}
	}

	public void setMedic(Player player, Integer level) {
		if (level > 0) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 99999999 * 20, level - 1));
		} else {
			player.removePotionEffect(PotionEffectType.REGENERATION);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		//if (ApiManager.allowsPVP(e.getDamager().getLocation())) {
			if (e.getEntity() instanceof Player player && e.getDamager() instanceof LivingEntity) {
				final LivingEntity damager = (LivingEntity) e.getDamager();

				//if (!ApiManager.isFriendly(damager, player)) {
					if (this.playerRunes.getRuneActivation(player, Rune.REBORN)) {
						int chance = this.playerRunes.getRuneLevel(player, Rune.REBORN) * 10;
						if (player.getHealth() <= 5) {
							if (this.apiManager.chance(chance, 100)) {
								this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> player.setHealth((player.getMaxHealth() / 2)), 0);
							}
						}
					}

					if (this.playerRunes.getRuneActivation(player, Rune.ROCKET)) {
						int chance = playerRunes.getRuneLevel(player, Rune.ROCKET) * 15;
						if (player.getHealth() <= 5) {
							if (apiManager.chance(chance, 100)) {
								this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                    Vector v = player.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize().setY(1);
                                    player.setVelocity(v);
                                }, 1);

								player.getWorld().playEffect(player.getLocation(), Effect.DRAGON_BREATH, 1);

								this.players.add(player.getUniqueId());
								this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> this.players.remove(player.getUniqueId()), 8 * 20);
							}
						}
					}
				//}
			}

			if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity entity) {
				Player damager = (Player) e.getDamager();

				//if (!ApiManager.isFriendly(damager, entity)) {
					if (this.playerRunes.getRuneActivation(damager, Rune.PYRO)) {
						int chance = this.playerRunes.getRuneLevel(damager, Rune.PYRO) * 10;
						if (this.apiManager.chance(chance, 100)) entity.setFireTicks(6 * 20);
					}

					if (this.playerRunes.getRuneActivation(damager, Rune.LEECH)) {
						int chance = this.playerRunes.getRuneLevel(damager, Rune.LEECH) * 15;
						if (this.apiManager.chance(chance, 100)) {
							double damage = e.getDamage() / 10;
                            damager.setHealth(Math.min(damager.getHealth() + damage, damager.getMaxHealth()));
						}
					}
				//}
			}
		//}
	}

	@EventHandler
	public void onPlayerFallDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player player) {
			if (e.getCause() == DamageCause.FALL) return;

			if (this.players.contains(player.getUniqueId())) e.setCancelled(true);
		}
	}
}