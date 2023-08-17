package me.badbones69.crazyrunes.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.badbones69.crazyrunes.ApiManager;
import me.badbones69.crazyrunes.CrazyRunes;
import me.badbones69.crazyrunes.api.CrazyManager;
import me.badbones69.crazyrunes.api.FileManager;
import me.badbones69.crazyrunes.api.PlayerRunes;
import me.badbones69.crazyrunes.api.enums.Rune;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class MenuListener implements Listener {

	private final CrazyRunes plugin = JavaPlugin.getPlugin(CrazyRunes.class);

	private final ApiManager apiManager = this.plugin.getApiManager();
	private final CrazyManager crazyManager = this.plugin.getCrazyManager();
	private final PlayerRunes playerRunes = this.plugin.getPlayerRunes();

	private final HashMap<Player, Rune> pickedRune = new HashMap<>();

	public void openMain(Player player) {
		FileConfiguration config = FileManager.Files.CONFIG.getFile();

		Inventory inv = this.plugin.getServer().createInventory(null, 45, this.apiManager.color(config.getString("Settings.Inventory-Name")));

		for (Rune rune : Rune.values()) {
			if (this.crazyManager.isActive(rune)) {
				List<String> Lore = new ArrayList<>();

				for (String lore : this.crazyManager.getDescription(rune)) {
					Lore.add(lore.replaceAll("%Level%", this.playerRunes.getRuneLevel(player, rune) + "").replaceAll("%level%", this.playerRunes.getRuneLevel(player, rune) + ""));
				}

				ItemStack item = this.apiManager.makeItem(this.crazyManager.getMaterial(rune), 1, "&6&l" + this.crazyManager.getCustomName(rune), Lore);
				inv.setItem(this.crazyManager.getSlot(rune), item);
			}
		}

		ItemStack runes = this.apiManager.makeItem("388", 1, "&2&lCredits", List.of("&6Available: &7" + this.playerRunes.getAvailableCredits(player)));
		inv.setItem(22, runes);
		player.openInventory(inv);
	}

	public void openLeveler(Player player, Rune rune) {
		FileConfiguration config = FileManager.Files.CONFIG.getFile();

		Inventory inv = this.plugin.getServer().createInventory(null, 9, this.apiManager.color(config.getString("Settings.Choose-Level")));
		Integer max = rune.getMaxLevel();
		ItemStack toggler;

		if (this.playerRunes.getRuneActivation(player, rune)) {
			toggler = this.apiManager.makeItem("165", 1, "&6&lActivated", List.of("&7Click to deactivate."));
		} else {
			toggler = this.apiManager.makeItem("152", 1, "&4&lDeactivated", List.of("&7Click a level to activate."));
		}

		inv.setItem(0, toggler);
		int total = 0;

		for (int i = 1; i <= max; i++) {
			ItemStack block;
			List<String> Lore = new ArrayList<>();
			total += this.crazyManager.getLevelCost(rune, i);
			for (String lore : this.crazyManager.getLevelDescription(rune)) {
				Lore.add(lore.replaceAll("%Power%", rune.getPower() * i + "").replaceAll("%power%", rune.getPower() * i + "")
						.replaceAll("%Level%", i + "").replaceAll("%level%", i + "")
						.replaceAll("%Cost%", this.crazyManager.getLevelCost(rune, i) + "").replaceAll("%cost%", this.crazyManager.getLevelCost(rune, i) + "")
						.replaceAll("%TotalCost%", total + "").replaceAll("%totalcost%", total + ""));
			}

			if (i <= this.playerRunes.getRuneLevel(player, rune)) {
				block = this.apiManager.makeItem("35:5", 1, "&a&lUnLocked", Lore);
			} else {
				block = this.apiManager.makeItem("35:14", 1, "&c&lLocked", Lore);
			}

			inv.addItem(block);
		}

		ItemStack back = this.apiManager.makeItem("410", 1, "&7&l<< &3Back");
		inv.setItem(8, back);
		ItemStack runes = this.apiManager.makeItem("388", 1, "&2&lCredits", List.of("&6Available: &7" + this.playerRunes.getAvailableCredits(player)));
		inv.setItem(7, runes);
		player.openInventory(inv);
	}

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		FileConfiguration config = FileManager.Files.CONFIG.getFile();

		InventoryView inv = e.getView();
		Player player = (Player) e.getWhoClicked();

        if (inv.getTitle().equalsIgnoreCase(this.apiManager.color(config.getString("Settings.Inventory-Name")))) {
            e.setCancelled(true);
            ItemStack item = e.getCurrentItem();
            if (item != null) {
                if (item.hasItemMeta()) {
                    if (item.getItemMeta().hasDisplayName()) {
                        if (this.crazyManager.isRune(this.apiManager.removeColor(item.getItemMeta().getDisplayName()))) {
                            Rune rune = this.crazyManager.getFromName(this.apiManager.removeColor(item.getItemMeta().getDisplayName()));
                            openLeveler(player, rune);
                            pickedRune.put(player, rune);
                            return;
                        }
                    }
                }
            }
        }
		
        if (inv.getTitle().equalsIgnoreCase(this.apiManager.color(config.getString("Settings.Choose-Level")))) {
            e.setCancelled(true);
            ItemStack item = e.getCurrentItem();
            Rune rune = pickedRune.get(player);

            if (item != null) {
                if (item.hasItemMeta()) {
                    if (item.getItemMeta().hasDisplayName()) {
                        String name = item.getItemMeta().getDisplayName();
                        if (name.equalsIgnoreCase(this.apiManager.color("&7&l<< &3Back"))) {
                            openMain(player);
                            return;
                        }

                        if (name.equalsIgnoreCase(this.apiManager.color("&6&lActivated"))) {
                            this.playerRunes.setRuneLevel(player, rune, 0);
                            this.playerRunes.setRuneActivation(player, rune, false);
                            openLeveler(player, rune);
                            this.playerRunes.activateRunes(player);
                            return;
                        }

                        int slot = e.getRawSlot();

                        if (slot < inv.getTopInventory().getSize()) {
                            int credits = this.playerRunes.getAvailableCredits(player);
                            int cost = 0;

                            for (int i = 1; i <= slot; i++) {
                                cost += this.crazyManager.getLevelCost(rune, i);
                            }

                            if (this.playerRunes.getRuneLevel(player, rune) == slot) return;

                            if ((credits - cost) < 0) return;

                            this.playerRunes.setRuneLevel(player, rune, slot);
                            this.playerRunes.setRuneActivation(player, rune, slot > 0);
                            openLeveler(player, rune);
                            this.playerRunes.activateRunes(player);
                        }
                    }
                }
            }
        }
    }
}