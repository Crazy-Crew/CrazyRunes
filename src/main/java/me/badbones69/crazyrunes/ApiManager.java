package me.badbones69.crazyrunes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import me.badbones69.crazyrunes.api.FileManager.Files;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class ApiManager {

	private final CrazyRunes plugin = JavaPlugin.getPlugin(CrazyRunes.class);

	public String getPrefix() {
		return color(Files.CONFIG.getFile().getString("Settings.Prefix"));
	}

	public String color(String msg) {
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		return msg;
	}

	public String removeColor(String msg) {
		msg = ChatColor.stripColor(msg);
		return msg;
	}

	public ItemStack makeItem(String type) {
		int ty = 0;
		if (type.contains(":")) {
			String[] b = type.split(":");
			type = b[0];
			ty = Integer.parseInt(b[1]);
		}

		Material material = Material.matchMaterial(type);

        assert material != null;
        return new ItemStack(material, 1, (short) ty);
	}

	public ItemStack makeItem(String type, int amount, String name) {
		int ty = 0;
		if (type.contains(":")) {
			String[] b = type.split(":");
			type = b[0];
			ty = Integer.parseInt(b[1]);
		}

		Material material = Material.matchMaterial(type);
		assert material != null;
		ItemStack item = new ItemStack(material, amount, (short) ty);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(color(name));
		item.setItemMeta(meta);
		return item;
	}

	public ItemStack makeItem(String type, int amount, String name, List<String> lore) {
		ArrayList<String> list = new ArrayList<>();

		int ty = 0;
		if (type.contains(":")) {
			String[] b = type.split(":");
			type = b[0];
			ty = Integer.parseInt(b[1]);
		}

		Material material = Material.matchMaterial(type);
        assert material != null;
		return getItemStack(material, amount, (short) ty, name, lore, list);
	}

	public ItemStack makeItem(Material material, int amount, int type, String name) {
		ItemStack item = new ItemStack(material, amount, (short) type);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(color(name));
		item.setItemMeta(meta);
		return item;
	}

	public ItemStack makeItem(Material material, int amount, int type, String name, List<String> lore) {
		ArrayList<String> list = new ArrayList<>();
		return getItemStack(material, amount, (short) type, name, lore, list);
	}

	private ItemStack getItemStack(Material material, int amount, short type, String name, List<String> lore, ArrayList<String> list) {
		ItemStack item = new ItemStack(material, amount, type);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(color(name));
		for (String line : lore) list.add(color(line));
		meta.setLore(list);
		item.setItemMeta(meta);
		return item;
	}

	public ItemStack makeItem(Material material, int amount, int type, String name, List<String> lore, Map<Enchantment, Integer> enchants) {
		ItemStack item = new ItemStack(material, amount, (short) type);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		item.setItemMeta(meta);
		item.addUnsafeEnchantments(enchants);
		return item;
	}

	public ItemStack addLore(ItemStack item, String i) {
		ArrayList<String> lore = new ArrayList<>();
		ItemMeta meta = item.getItemMeta();

		if (item.getItemMeta().hasLore()) lore.addAll(item.getItemMeta().getLore());

		lore.add(i);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			return true;
		}

		return false;
	}

	public Player getPlayer(String name) {
		return Bukkit.getServer().getPlayer(name);
	}

	public Location getLoc(Player player) {
		return player.getLocation();
	}

	public void runCommand(Player player, String command) {
		player.performCommand(command);
	}

	public boolean isOnline(String name, CommandSender sender) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player.getName().equalsIgnoreCase(name)) {
				return false;
			}
		}

		sender.sendMessage(color("&cThat player is not online at this time."));
		return true;
	}

	public boolean hasPermission(Player player, String perm) {
		if (!player.hasPermission("crazyrunes." + perm)) {
			player.sendMessage(color("&cYou need permission to use this command."));
			return false;
		}

		return true;
	}

	public boolean hasPermission(CommandSender sender, String perm) {
		if (sender instanceof Player player) {
			if (!player.hasPermission("crazyrunes." + perm)) {
				player.sendMessage(color("&cYou need permission to use this command."));
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean chance(int min, int max) {
		if (max == min) return true;
		Random number = new Random();
		int chance = 1 + number.nextInt(max);
        return chance <= min;
    }
}