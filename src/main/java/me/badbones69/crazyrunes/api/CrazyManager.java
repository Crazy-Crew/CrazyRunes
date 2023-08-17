package me.badbones69.crazyrunes.api;

import me.badbones69.crazyrunes.api.enums.Rune;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;
import java.util.List;

public class CrazyManager {

	private final HashMap<Rune, String> names = new HashMap<>();
	private final HashMap<Rune, Integer> slot = new HashMap<>();
	private final HashMap<Rune, Boolean> active = new HashMap<>();
	private final HashMap<Rune, String> material = new HashMap<>();
	private final HashMap<Rune, String> customName = new HashMap<>();
	private final HashMap<Rune, List<String>> description = new HashMap<>();
	private final HashMap<Rune, List<String>> levelDescription = new HashMap<>();
	private final HashMap<Rune, HashMap<Integer, Integer>> levelCost = new HashMap<>();

	public void load() {
		slot.clear();
		names.clear();
		active.clear();
		material.clear();
		levelCost.clear();
		customName.clear();
		description.clear();

		FileConfiguration runes = FileManager.Files.RUNES.getFile();
		
		for (Rune rune : getRunes()) {
			if (runes.contains("Runes." + rune.getName())) {
				String name = rune.getName();
				names.put(rune, name);
				HashMap<Integer, Integer> cost = new HashMap<Integer, Integer>();
				for (int i = 1; i <= rune.getMaxLevel(); i++) {
					cost.put(i, runes.getInt("Runes." + name + ".Level-Costs." + i));
				}

				levelCost.put(rune, cost);
				slot.put(rune, runes.getInt("Runes." + name + ".Slot") - 1);
				active.put(rune, runes.getBoolean("Runes." + name + ".Active"));
				material.put(rune, runes.getString("Runes." + name + ".Item"));
				customName.put(rune, runes.getString("Runes." + name + ".Name"));
				description.put(rune, runes.getStringList("Runes." + name + ".Description"));
				levelDescription.put(rune, runes.getStringList("Runes." + name + ".Level-Description"));
			}
		}
	}

	public Rune[] getRunes() {
		return Rune.values();
	}

	public String getCustomName(Rune rune) {
		return this.customName.get(rune);
	}

	public List<String> getDescription(Rune rune) {
		return this.description.get(rune);
	}

	public List<String> getLevelDescription(Rune rune) {
		return this.levelDescription.get(rune);
	}

	public boolean isActive(Rune rune) {
		return this.active.get(rune);
	}

	public int getSlot(Rune rune) {
		return this.slot.get(rune);
	}

	public String getMaterial(Rune rune) {
		return this.material.get(rune);
	}

	public int getLevelCost(Rune rune, Integer level) {
		return this.levelCost.get(rune).get(level);
	}

	public Boolean isRune(String name) {
		for (Rune rune : getRunes()) {
			if (rune.getName().equalsIgnoreCase(name)) return true;

			if (getCustomName(rune).equalsIgnoreCase(name)) return true;
		}
		return false;
	}

	public Rune getFromName(String name) {
		for (Rune rune : getRunes()) {
			if (rune.getName().equalsIgnoreCase(name)) return rune;

			if (getCustomName(rune).equalsIgnoreCase(name)) return rune;
		}

		return null;
	}
}