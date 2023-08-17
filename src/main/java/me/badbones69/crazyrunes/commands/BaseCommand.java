package me.badbones69.crazyrunes.commands;

import me.badbones69.crazyrunes.ApiManager;
import me.badbones69.crazyrunes.CrazyRunes;
import me.badbones69.crazyrunes.api.CrazyManager;
import me.badbones69.crazyrunes.api.FileManager;
import me.badbones69.crazyrunes.api.PlayerRunes;
import me.badbones69.crazyrunes.api.enums.Rune;
import me.badbones69.crazyrunes.controllers.MenuListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BaseCommand implements CommandExecutor {
    
    private final CrazyRunes plugin = JavaPlugin.getPlugin(CrazyRunes.class);
    
    private final ApiManager apiManager = this.plugin.getApiManager();
    private final FileManager fileManager = this.plugin.getFileManager();
    private final CrazyManager crazyManager = this.plugin.getCrazyManager();
    private final PlayerRunes playerRunes = this.plugin.getPlayerRunes();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (label.equalsIgnoreCase("runes") || label.equalsIgnoreCase("crazyrunes")) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    this.plugin.getMenuListener().openMain((Player) sender);
                } else {
                    sender.sendMessage(this.apiManager.color("&cYou must be a player to use this command."));
                }

                return true;
            }

            if (args[0].equalsIgnoreCase("help")) {
                if (this.apiManager.hasPermission(sender, "access")) return true;
                sender.sendMessage(this.apiManager.color("&b/runes &7- Allows you to pick your runes you wish to use."));
                sender.sendMessage(this.apiManager.color("&b/runes check [player] &7- Check a players runes."));
                sender.sendMessage(this.apiManager.color("&b/runes add <amount> [player] &7- Give a player more Credits."));
                sender.sendMessage(this.apiManager.color("&b/runes remove <amount> [player] &7- Take away a players Credits."));
                sender.sendMessage(this.apiManager.color("&b/runes reload &7- Reloads the runes files."));
                sender.sendMessage(this.apiManager.color("&b/runes help &7- Shows the runes help menu."));
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (this.apiManager.hasPermission(sender, "admin")) return true;

                this.fileManager.reloadAllFiles();
                this.fileManager.setLog(true).setup();

                this.crazyManager.load();

                for (Player player : this.plugin.getServer().getOnlinePlayers()) {
                    this.playerRunes.loadPlayer(player);
                }

                sender.sendMessage(this.apiManager.color(this.apiManager.getPrefix() + "&7You have just reloaded all the files."));
                return true;
            }

            if (args[0].equalsIgnoreCase("add")) {// /runes add <amount> [player]
                if (this.apiManager.hasPermission(sender, "admin")) return true;

                if (args.length >= 2) {
                    int amount;
                    Player player;

                    if (this.apiManager.isInt(args[1])) {
                        sender.sendMessage(this.apiManager.getPrefix() + this.apiManager.color("&cThat is not a number."));
                        return true;
                    } else {
                        amount = Integer.parseInt(args[1]);
                    }
                    if (args.length >= 3) {
                        if (this.apiManager.isOnline(args[2], sender)) return true;
                        player = this.apiManager.getPlayer(args[2]);
                    } else {
                        if (sender instanceof Player) {
                            player = (Player) sender;
                        } else {
                            sender.sendMessage(this.apiManager.getPrefix() + this.apiManager.color("&cYou must be a player to use this command."));
                            return true;
                        }
                    }

                    this.playerRunes.addCredits(player, amount);

                    sender.sendMessage(this.apiManager.getPrefix() + this.apiManager.color("&7You have just given &6" + player.getName() + " " + amount + " &7more credits."));
                    return true;
                }
                sender.sendMessage(this.apiManager.getPrefix() + this.apiManager.color("&c/runes add <Amount> [Player]"));
                return true;
            }
            if (args[0].equalsIgnoreCase("remove")) {// /runes remove <amount> [player]
                if (this.apiManager.hasPermission(sender, "admin")) return true;

                if (args.length >= 2) {
                    int amount;
                    Player player;

                    if (this.apiManager.isInt(args[1])) {
                        sender.sendMessage(this.apiManager.getPrefix() + this.apiManager.color("&cThat is not a number."));
                        return true;
                    } else {
                        amount = Integer.parseInt(args[1]);
                    }

                    if (args.length >= 3) {
                        if (this.apiManager.isOnline(args[2], sender)) return true;
                        player = this.apiManager.getPlayer(args[2]);
                    } else {
                        if (sender instanceof Player) {
                            player = (Player) sender;
                        } else {
                            sender.sendMessage(this.apiManager.getPrefix() + this.apiManager.color("&cYou must be a player to use this command."));
                            return true;
                        }
                    }

                    this.playerRunes.removeCredits(player, amount);
                    sender.sendMessage(this.apiManager.getPrefix() + this.apiManager.color("&7You have just removed &6" + amount + " &7credits from &6" + player.getName() + "&7."));
                    return true;
                }
                sender.sendMessage(this.apiManager.getPrefix() + this.apiManager.color("&c/runes remove <amount> [player]"));
                return true;
            }
            if (args[0].equalsIgnoreCase("check")) {// /runes check [player]
                if (this.apiManager.hasPermission(sender, "access")) return true;
                Player player;

                if (args.length >= 2) {
                    if (this.apiManager.isOnline(args[1], sender)) return true;
                    player = this.apiManager.getPlayer(args[1]);
                } else {
                    if (sender instanceof Player) {
                        player = (Player) sender;
                    } else {
                        sender.sendMessage(this.apiManager.color("&cYou must be a player to use this command."));
                        return true;
                    }
                }

                StringBuilder msg = new StringBuilder();
                for (Rune rune : Rune.values()) {
                    if (this.playerRunes.getRuneActivation(player, rune)) {
                        msg.append("&a&l");
                    } else {
                        msg.append("&c&l");
                    }
                    msg.append(rune.getName()).append("&7: &3").append(this.playerRunes.getRuneLevel(player, rune));
                    msg.append("\n");
                }

                sender.sendMessage(this.apiManager.color("&7A list of all &b" + player.getName() + "'s &7runes."));
                sender.sendMessage(this.apiManager.color("&7Max Credits: &3" + this.playerRunes.getMaxCredits(player)));
                sender.sendMessage(this.apiManager.color("&7Available Credits: &3" + this.playerRunes.getAvailableCredits(player)));
                sender.sendMessage(this.apiManager.color(msg.toString()));
                return true;
            }

            sender.sendMessage(this.apiManager.color("&cPlease use /runes help for more information."));
            return true;
        }

        return false;
    }
}