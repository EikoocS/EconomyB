package tech.cookiepower.economyb;

import tech.cookiepower.economyb.api.Account;
import tech.cookiepower.economyb.api.Accounts;

import java.util.concurrent.CompletableFuture;

public class AccountsImpl implements Accounts {
    public static final Accounts INSTANCE = new AccountsImpl();

    @Override
    public CompletableFuture<Long> getBalance(String currency, Account.Type type, String id) {
        return AccountService.getInstance().get(currency,type,id);
    }

    @Override
    public CompletableFuture<Boolean> hasBalance(String currency, Account.Type type, String id, long amount) {
        return AccountService.getInstance().get(currency,type,id)
                .thenApply(balance -> balance >= amount);
    }

    @Override
    public CompletableFuture<Void> setBalance(String currency, Account.Type type, String id, long amount) {
        return AccountService.getInstance().set(currency,type,id,amount);
    }

    @Override
    public CompletableFuture<Void> addBalance(String currency, Account.Type type, String id, long amount) {
        return AccountService.getInstance().modify(currency,type,id,amount);
    }

    @Override
    public CompletableFuture<Void> removeBalance(String currency, Account.Type type, String id, long amount) {
        return AccountService.getInstance().modify(currency,type,id,-amount);
    }

    @Override
    public CompletableFuture<Void> frozenBalance(String currency, Account.Type type, String id, long amount) {
        return AccountService.getInstance().freeze(currency,type,id,amount);
    }

    @Override
    public CompletableFuture<Void> unfrozenBalance(String currency, Account.Type type, String id, long amount) {
        return AccountService.getInstance().unfreeze(currency,type,id,amount);
    }

    @Override
    public CompletableFuture<Void> transfer(
            String fromCurrency, Account.Type fromType, String fromId, long decreases,
            String toCurrency, Account.Type toType, String toId, long increases) {
        return AccountService.getInstance().transfer(
                fromCurrency, fromType, fromId, decreases,
                toCurrency, toType, toId, increases
        );
    }
}
