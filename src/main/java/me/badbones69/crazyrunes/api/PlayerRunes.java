package me.badbones69.crazyrunes.api;

import java.util.HashMap;
import me.badbones69.crazyrunes.CrazyRunes;
import me.badbones69.crazyrunes.api.enums.Rune;
import me.badbones69.crazyrunes.controllers.RuneListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerRunes {

	private final CrazyRunes plugin = JavaPlugin.getPlugin(CrazyRunes.class);
	private final RuneListener runeListener = this.plugin.getRuneListener();

	private final HashMap<Player, HashMap<Rune, Integer>> playerStats = new HashMap<>();
	private final HashMap<Player, HashMap<Rune, Boolean>> playerActivations = new HashMap<>();
	private final HashMap<Player, Integer> maxCredits = new HashMap<>();

	public void loadPlayer(Player player) {
		FileConfiguration data = FileManager.Files.DATA.getFile();
		FileConfiguration config = FileManager.Files.CONFIG.getFile();

		String uuid = player.getUniqueId().toString();
		HashMap<Rune, Integer> stats = new HashMap<>();
		HashMap<Rune, Boolean> activations = new HashMap<>();
		if (data.contains("Players." + uuid)) {
			for (Rune rune : Rune.values()) {
				stats.put(rune, data.getInt("Players." + uuid + "." + rune.getName() + ".Level"));
				if (stats.get(rune) > 0) {
					activations.put(rune, true);
				} else {
					activations.put(rune, false);
				}
			}

			maxCredits.put(player, data.getInt("Players." + uuid + ".Max-Credits"));
		} else {
			for (Rune rune : Rune.values()) {
				stats.put(rune, 0);
				activations.put(rune, false);
			}

			maxCredits.put(player, config.getInt("Settings.Default-Credits-Amount"));
		}

		playerStats.put(player, stats);
		playerActivations.put(player, activations);
		activateRunes(player);
	}

	public void unloadPlayer(Player player) {
		FileConfiguration data = FileManager.Files.DATA.getFile();

		String uuid = player.getUniqueId().toString();
		data.set("Players." + uuid + ".Name", player.getName());
		data.set("Players." + uuid + ".Max-Credits", maxCredits.get(player));
		for (Rune rune : Rune.values()) {
			data.set("Players." + uuid + "." + rune.getName() + ".Level", playerStats.get(player).get(rune));
			if (playerStats.get(player).get(rune) > 0) {
				data.set("Players." + uuid + "." + rune.getName() + ".Active", true);
			} else {
				data.set("Players." + uuid + "." + rune.getName() + ".Active", false);
			}
		}

		deactivateRunes(player);
		maxCredits.remove(player);
		playerStats.remove(player);
		playerActivations.remove(player);
	}

	public void activateRunes(Player player) {
		this.runeListener.setMedic(player, playerStats.get(player).get(Rune.MEDIC));
		this.runeListener.setSpeed(player, playerStats.get(player).get(Rune.SPEED));
        this.runeListener.setSurvivor(player, playerStats.get(player).get(Rune.SURVIVOR) > 0);
		this.runeListener.setTank(player, playerStats.get(player).get(Rune.TANK));
	}

	public void deactivateRunes(Player player) {
		this.runeListener.setMedic(player, 0);
		this.runeListener.setSpeed(player, 0);
		this.runeListener.setSurvivor(player, false);
		this.runeListener.setTank(player, 0);
	}

	public Integer getRuneLevel(Player player, Rune rune) {
		return playerStats.get(player).get(rune);
	}

	public Boolean getRuneActivation(Player player, Rune rune) {
		return playerActivations.get(player).get(rune);
	}

	public void setRuneLevel(Player player, Rune rune, Integer level) {
		playerStats.get(player).put(rune, level);
	}

	public void setRuneActivation(Player player, Rune rune, Boolean toggle) {
		playerActivations.get(player).put(rune, toggle);
	}

	public Integer getMaxCredits(Player player) {
		return maxCredits.get(player);
	}

	public void addCredits(Player player, int amount) {
		maxCredits.put(player, maxCredits.get(player) + amount);
	}

	public void removeCredits(Player player, int amount) {
		amount = maxCredits.get(player) - amount;
		if (amount < 0) amount = 0;
		maxCredits.put(player, amount);
	}

	public Integer getAvailableCredits(Player player) {
		int available = 0;
		int used = 0;
		int max = getMaxCredits(player);

		for (Rune rune : Rune.values()) {
			for (int i = 1; i <= getRuneLevel(player, rune); i++) {
				used += this.plugin.getCrazyManager().getLevelCost(rune, i);
			}
		}

		available = (max - used);
		if (available < 0) available = 0;
		return available;
	}
}