package me.badbones69.crazyrunes;

import me.badbones69.crazyrunes.api.CrazyManager;
import me.badbones69.crazyrunes.api.FileManager;
import me.badbones69.crazyrunes.api.PlayerRunes;
import me.badbones69.crazyrunes.commands.BaseCommand;
import me.badbones69.crazyrunes.controllers.MenuListener;
import me.badbones69.crazyrunes.controllers.JoinQuitListener;
import me.badbones69.crazyrunes.controllers.RuneListener;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyRunes extends JavaPlugin {

	private CrazyManager crazyManager;
	private FileManager fileManager;
	private ApiManager apiManager;
	private PlayerRunes playerRunes;

	private JoinQuitListener joinQuitListener;
	private MenuListener menuListener;
	private RuneListener runeListener;

	@Override
	public void onEnable() {

		this.fileManager = new FileManager();
		this.fileManager.setup();

		this.crazyManager = new CrazyManager();
		this.crazyManager.load();

		this.apiManager = new ApiManager();

		this.playerRunes = new PlayerRunes();

		registerCommand(getCommand("runes"), null, new BaseCommand());

		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(this.menuListener = new MenuListener(), this);
		pluginManager.registerEvents(this.runeListener = new RuneListener(), this);
		pluginManager.registerEvents(this.joinQuitListener = new JoinQuitListener(), this);

		for (Player player : getServer().getOnlinePlayers()) {
			this.playerRunes.loadPlayer(player);
		}
	}

	@Override
	public void onDisable() {
		this.fileManager.saveFile(FileManager.Files.CONFIG);
		this.fileManager.saveFile(FileManager.Files.DATA);
		this.fileManager.saveFile(FileManager.Files.MESSAGES);
		this.fileManager.saveFile(FileManager.Files.RUNES);

		for (Player player : getServer().getOnlinePlayers()) {
			this.playerRunes.unloadPlayer(player);
		}
	}

	private void registerCommand(PluginCommand pluginCommand, TabCompleter tabCompleter, CommandExecutor commandExecutor) {
		if (pluginCommand != null) {
			pluginCommand.setExecutor(commandExecutor);

			if (tabCompleter != null) pluginCommand.setTabCompleter(tabCompleter);
		}
	}

	public FileManager getFileManager() {
		return this.fileManager;
	}

	public ApiManager getApiManager() {
		return this.apiManager;
	}

	public CrazyManager getCrazyManager() {
		return this.crazyManager;
	}

	public PlayerRunes getPlayerRunes() {
		return this.playerRunes;
	}

	public RuneListener getRuneListener() {
		return this.runeListener;
	}

	public JoinQuitListener getJoinQuitListener() {
		return this.joinQuitListener;
	}

	public MenuListener getMenuListener() {
		return this.menuListener;
	}
}