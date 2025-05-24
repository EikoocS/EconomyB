package tech.cookiepower.economyb.api;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EconomyCommandExecutor implements CommandExecutor {
    private final Accounts accounts;
    public EconomyCommandExecutor(Accounts accounts) {
        this.accounts = accounts;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] argv) {
        if (argv.length < 2){
            commandSender.sendMessage(ChatColor.RED + "Usage: /" + label + " <get|set|add|remove> <currency> [amount] [player]");
            return false;
        }

        var action = argv[0];
        var currency = argv[1];

        switch (action) {
            case "get" -> {
                if (!(commandSender instanceof Player)&&argv.length <3) {
                    commandSender.sendMessage(ChatColor.RED + "Usage: /" + label + " get <currency> [player]");
                    commandSender.sendMessage(ChatColor.RED + "You must specify a player when using this command from console.");
                    return false;
                }
                var target = argv.length >= 3 ?
                        Bukkit.getOfflinePlayer(argv[2]).getUniqueId().toString() :
                        ((Player) commandSender).getUniqueId().toString();
                var result = accounts.getBalance(currency, Account.Type.USER, target);
                result.thenAccept(balance -> {
                    commandSender.sendMessage(ChatColor.GOLD + "Balance: " + ChatColor.GREEN + balance);
                });
            }
            case "set" -> {
                if (argv.length < 3) {
                    commandSender.sendMessage(ChatColor.RED + "Usage: /" + label + " set <currency> <amount> [player]");
                    return false;
                }
                if (!(commandSender instanceof Player)&&argv.length <4) {
                    commandSender.sendMessage(ChatColor.RED + "Usage: /" + label + " set <currency> <amount> [player]");
                    commandSender.sendMessage(ChatColor.RED + "You must specify a player when using this command from console.");
                    return false;
                }
                var target = argv.length >= 4 ?
                        Bukkit.getOfflinePlayer(argv[3]).getUniqueId().toString() :
                        ((Player) commandSender).getUniqueId().toString();
                long amount = Long.parseLong(argv[2]);
                accounts.setBalance(currency, Account.Type.USER, target, amount)
                        .thenAccept(v -> commandSender.sendMessage(ChatColor.GOLD + "Set balance to " + ChatColor.GREEN + amount))
                        .exceptionally(e -> {
                            commandSender.sendMessage(ChatColor.RED + "Failed to set balance: " + e.getMessage());
                            return null;
                        });
            }
            case "add" -> {
                if (argv.length < 3) {
                    commandSender.sendMessage(ChatColor.RED + "Usage: /" + label + " add <currency> <amount> [player]");
                    return false;
                }
                if (!(commandSender instanceof Player)&&argv.length <4) {
                    commandSender.sendMessage(ChatColor.RED + "Usage: /" + label + " add <currency> <amount> [player]");
                    commandSender.sendMessage(ChatColor.RED + "You must specify a player when using this command from console.");
                    return false;
                }
                var target = argv.length >= 4 ?
                        Bukkit.getOfflinePlayer(argv[3]).getUniqueId().toString() :
                        ((Player) commandSender).getUniqueId().toString();
                long amount = Long.parseLong(argv[2]);
                accounts.addBalance(currency, Account.Type.USER, target, amount)
                        .thenAccept(v -> commandSender.sendMessage(ChatColor.GOLD + "Added " + ChatColor.GREEN + amount + " to balance"))
                        .exceptionally(e -> {
                            commandSender.sendMessage(ChatColor.RED + "Failed to add balance: " + e.getMessage());
                            return null;
                        });
            }
            case "remove" -> {
                if (argv.length < 3) {
                    commandSender.sendMessage(ChatColor.RED + "Usage: /" + label + " remove <currency> <amount> [player]");
                    return false;
                }
                if (!(commandSender instanceof Player)&&argv.length <4) {
                    commandSender.sendMessage(ChatColor.RED + "Usage: /" + label + " remove <currency> <amount> [player]");
                    commandSender.sendMessage(ChatColor.RED + "You must specify a player when using this command from console.");
                    return false;
                }
                var target = argv.length >= 4 ?
                        Bukkit.getOfflinePlayer(argv[3]).getUniqueId().toString() :
                        ((Player) commandSender).getUniqueId().toString();
                long amount = Long.parseLong(argv[2]);
                accounts.removeBalance(currency, Account.Type.USER, target, amount)
                        .thenAccept(v -> commandSender.sendMessage(ChatColor.GOLD + "Removed " + ChatColor.GREEN + amount + " from balance"))
                        .exceptionally(e -> {
                            commandSender.sendMessage(ChatColor.RED + "Failed to remove balance: " + e.getMessage());
                            return null;
                        });
            }
            default -> {
                commandSender.sendMessage("Unknown sub command: " + action);
                return false;
            }
        }
        return true;
    }
}