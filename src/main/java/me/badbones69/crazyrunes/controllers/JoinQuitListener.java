package me.badbones69.crazyrunes.controllers;

import me.badbones69.crazyrunes.CrazyRunes;
import me.badbones69.crazyrunes.api.PlayerRunes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinQuitListener implements Listener {

	private final CrazyRunes plugin = JavaPlugin.getPlugin(CrazyRunes.class);
	private final PlayerRunes playerRunes = this.plugin.getPlayerRunes();

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		this.playerRunes.loadPlayer(player);
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		this.playerRunes.unloadPlayer(player);
	}
}