package tech.cookiepower.economyb;

import tech.cookiepower.economyb.api.Account;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AccountImpl implements Account {
    private final String id;
    private final Account.Type type;

    public AccountImpl(UUID uuid,Account.Type type) {
        this.id = uuid.toString();
        this.type = type;
    }

    public AccountImpl(String ident,Account.Type type) {
        this.id = ident;
        this.type = type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public CompletableFuture<Long> getBalance(String currency) {
        return AccountService.getInstance().get(currency,type,id);
    }

    @Override
    public CompletableFuture<Boolean> hasBalance(String currency, long amount) {
        return AccountService.getInstance().get(currency,type,id)
                .thenApply(balance -> balance >= amount);
    }

    @Override
    public CompletableFuture<Void> setBalance(String currency, long amount) {
        return AccountService.getInstance().set(currency,type,id,amount);
    }

    @Override
    public CompletableFuture<Void> addBalance(String currency, long amount) {
        return AccountService.getInstance().modify(currency,type,id,amount);
    }

    @Override
    public CompletableFuture<Void> removeBalance(String currency, long amount) {
        return AccountService.getInstance().modify(currency,type,id,-amount);
    }

    @Override
    public CompletableFuture<Void> frozenBalance(String currency, long amount) {
        return AccountService.getInstance().freeze(currency,type,id,amount);
    }

    @Override
    public CompletableFuture<Void> unfrozenBalance(String currency, long amount) {
        return AccountService.getInstance().unfreeze(currency,type,id,amount);
    }

    @Override
    public CompletableFuture<Void> transfer(String currency, long amount, Account destination) {
        return AccountService.getInstance().transfer(
                currency, type, id, amount,
                currency, destination.getType(), destination.getId(), amount
        );
    }

    @Override
    public CompletableFuture<Void> exchange(String fromCurrency, long decreases, String toCurrency, long increases) {
        return AccountService.getInstance().transfer(
                fromCurrency, type, id, decreases,
                toCurrency, type, id, increases
        );
    }

    @Override
    public CompletableFuture<Void> transfer(String fromCurrency, long decreases, Account destination, String toCurrency, long increases) {
        return AccountService.getInstance().transfer(
                fromCurrency, type, id, decreases,
                toCurrency, destination.getType(), destination.getId(), increases
        );
    }
}
