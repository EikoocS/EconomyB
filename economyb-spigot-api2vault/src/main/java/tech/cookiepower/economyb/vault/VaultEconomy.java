package tech.cookiepower.economyb.vault;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import tech.cookiepower.economyb.api.Account;
import tech.cookiepower.economyb.api.Accounts;

import java.util.List;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("deprecation")
public class VaultEconomy extends AbstractEconomy {
    private final Accounts accounts;
    private final String currency;
    private final static EconomyResponse BANKING_IS_NOT_SUPPORTED =
            new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banking is not supported");

    public VaultEconomy(Accounts accounts,String currency) {
        this.accounts = accounts;
        this.currency = currency;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "eb2vault";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double v) {
        return String.format("%.0f", v);
    }

    @Override
    public String currencyNamePlural() {
        return "";
    }

    @Override
    public String currencyNameSingular() {
        return "";
    }

    @Override
    public boolean hasAccount(String playerName) {
        return true; // Account always automatically created
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return true; // Account always automatically created
    }

    @Override
    public double getBalance(String playerName) {
        var player = Bukkit.getOfflinePlayer(playerName);
        var uuid = player.getUniqueId();
        try {
            return accounts.getBalance(currency, Account.Type.USER, uuid.toString()).get();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getBalance(String playerName, String worldName) {
        return getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        var player = Bukkit.getOfflinePlayer(playerName);
        var uuid = player.getUniqueId();
        try {
            return accounts.hasBalance(currency, Account.Type.USER, uuid.toString(), (long) amount).get();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        var player = Bukkit.getOfflinePlayer(playerName);
        var uuid = player.getUniqueId();
        try {
            accounts.removeBalance(currency, Account.Type.USER, uuid.toString(), (long) amount).join();
            var balance = accounts.getBalance(currency, Account.Type.USER, uuid.toString()).get();
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
        } catch (InterruptedException | ExecutionException e) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, e.getMessage());
        }
    }

    @Override
        public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        var player = Bukkit.getOfflinePlayer(playerName);
        var uuid = player.getUniqueId();
        try {
            accounts.addBalance(currency, Account.Type.USER, uuid.toString(), (long) amount).join();
            var balance = accounts.getBalance(currency, Account.Type.USER, uuid.toString()).get();
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
        } catch (InterruptedException | ExecutionException e) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        var player = Bukkit.getOfflinePlayer(playerName);
        var uuid = player.getUniqueId();
        accounts.addBalance(currency, Account.Type.USER, uuid.toString(),0).join();
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }

    // BANK IS NOT SUPPORTED

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return BANKING_IS_NOT_SUPPORTED;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return BANKING_IS_NOT_SUPPORTED;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return BANKING_IS_NOT_SUPPORTED;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return BANKING_IS_NOT_SUPPORTED;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return BANKING_IS_NOT_SUPPORTED;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return BANKING_IS_NOT_SUPPORTED;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return BANKING_IS_NOT_SUPPORTED;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return BANKING_IS_NOT_SUPPORTED;
    }

    @Override
    public List<String> getBanks() {
        return List.of();
    }
}
