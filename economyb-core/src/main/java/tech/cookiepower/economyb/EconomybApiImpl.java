package tech.cookiepower.economyb;

import tech.cookiepower.economyb.api.Account;
import tech.cookiepower.economyb.api.Accounts;
import tech.cookiepower.economyb.api.EconomybAPI;

import java.util.UUID;

public class EconomybApiImpl implements EconomybAPI {

    public EconomybApiImpl(EconomybConfig config) {
        AccountService.init(config.host, config.port, config.user, config.password, config.database);
    }

    @Override
    public boolean ready() {
        return true;
    }

    @Override
    public Account getAccount(String identifier, Account.Type type) {
        return new AccountImpl(identifier, type);
    }

    @Override
    public Account getPlayerAccount(UUID uuid) {
        return new AccountImpl(uuid, Account.Type.USER);
    }

    @Override
    public Account getPlayerAccount(String uuid) {
        return new AccountImpl(uuid, Account.Type.USER);
    }

    @Override
    public Account getSystemAccount(UUID identifier) {
        return new AccountImpl(identifier, Account.Type.SYSTEM);
    }

    @Override
    public Account getSystemAccount(String identifier) {
        return new AccountImpl(identifier, Account.Type.SYSTEM);
    }

    @Override
    public Accounts getAccounts() {
        return AccountsImpl.INSTANCE;
    }
}
