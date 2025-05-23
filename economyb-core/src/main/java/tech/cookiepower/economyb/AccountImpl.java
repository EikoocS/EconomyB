package tech.cookiepower.economyb;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import tech.cookiepower.economyb.api.Account;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
@EqualsAndHashCode
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
    public CompletableFuture<Long> getBalance(String currency) {
        var ident = getIdent(currency);
        var freeFuture = AccountService.getInstance().getBalance(ident);
        var frozenFuture = AccountService.getInstance().getFrozenBalance(ident);
        return freeFuture.thenCombine(frozenFuture, (free, frozen) -> free-frozen);
    }

    @Override
    public CompletableFuture<Boolean> hasBalance(String currency, long amount) {
        var ident = getIdent(currency);
        var freeFuture = AccountService.getInstance().getBalance(ident);
        var frozenFuture = AccountService.getInstance().getFrozenBalance(ident);
        return freeFuture.thenCombine(frozenFuture, (free, frozen) -> free-frozen>amount);
    }

    @Override
    public CompletableFuture<Long> setBalance(String currency, long amount, boolean force) {
        var ident = getIdent(currency);
        var frozenFuture = AccountService.getInstance().getFrozenBalance(ident);
        return frozenFuture.thenCompose(frozen -> AccountService.getInstance().setBalance(ident,frozen+amount));
    }

    @Override
    public CompletableFuture<Long> addBalance(String currency, long amount, boolean force) {
        var ident = getIdent(currency);
        var updateFuture = AccountService.getInstance().modifyBalance(ident,amount,force);
        return updateFuture.thenCompose( v -> AccountService.getInstance().getBalance(ident));
    }

    @Override
    public CompletableFuture<Long> removeBalance(String currency, long amount, boolean force) {
        var ident = getIdent(currency);
        var updateFuture = AccountService.getInstance().modifyBalance(ident,-amount,force);
        return updateFuture.thenCompose( v -> AccountService.getInstance().getBalance(ident));
    }

    @Override
    public CompletableFuture<Long> frozenBalance(String currency, long amount) {
        if (amount<0) throw new IllegalArgumentException("amount must be greater than zero");
        var ident = getIdent(currency);
        return AccountService.getInstance().freezeAmount(ident,amount);
    }

    @Override
    public CompletableFuture<Long> unfrozenBalance(String currency, long amount) {
        if (amount<0) throw new IllegalArgumentException("amount must be greater than zero");
        var ident = getIdent(currency);
        return AccountService.getInstance().unfreezeAmount(ident,amount);
    }

    @Override
    public CompletableFuture<Boolean> transfer(String currency, long amount, Account destination, boolean force) {
        var from = getIdent(currency);
        var to = getIdent(destination,currency);
        return AccountService.getInstance().transfer(from,to,amount,amount,force);
    }

    @Override
    public CompletableFuture<Boolean> exchange(String fromCurrency, long decreases, String toCurrency, long increases, boolean force) {
        var from = getIdent(fromCurrency);
        var to = getIdent(toCurrency);
        return AccountService.getInstance().transfer(from,to,decreases,increases,force);
    }

    @Override
    public CompletableFuture<Boolean> transfer(String fromCurrency, long decreases, Account destination, String toCurrency, long increases, boolean force) {
        var from = getIdent(fromCurrency);
        var to = getIdent(destination,toCurrency);
        return AccountService.getInstance().transfer(from,to,decreases,increases,force);
    }

    // private util
    private AccountIdent getIdent(String currency){
        if (this.type == Type.SYSTEM) return new AccountIdent(currency, Account.Type.SYSTEM, id);
        else return new AccountIdent(currency, Account.Type.USER, id);
    }

    private static AccountIdent getIdent(Account account,String currency) {
        if (account.getType() == Type.SYSTEM) return new AccountIdent(currency, Account.Type.SYSTEM, account.getId());
        else return new AccountIdent(currency, Account.Type.USER, account.getId());
    }

}
