package tech.cookiepower.economyb;

import io.quarkus.arc.Arc;
import io.smallrye.mutiny.Uni;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import tech.cookiepower.economyb.api.Account;
import tech.cookiepower.economyb.entity.AccountEntity;
import tech.cookiepower.economyb.entity.AccountIdent;
import tech.cookiepower.economyb.service.AccountService;

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
        var free = getService().getBalance(ident);
        var frozen = getService().getFrozenBalance(ident);
        return Uni.combine().all().unis(free, frozen)
                .asTuple()
                .map(tuple -> tuple.getItem1() - tuple.getItem2())
                .subscribeAsCompletionStage();
    }

    @Override
    public CompletableFuture<Boolean> hasBalance(String currency, long amount) {
        var ident = getIdent(currency);
        var free = getService().getBalance(ident);
        var frozen = getService().getFrozenBalance(ident);
        return Uni.combine().all().unis(free, frozen)
                .asTuple()
                .map(tuple -> (tuple.getItem1() - tuple.getItem2()) >= amount)
                .subscribeAsCompletionStage();
    }

    @Override
    public CompletableFuture<Long> setBalance(String currency, long amount, boolean force) {
        var ident = getIdent(currency);
        return getService().getFrozenBalance(ident)
                .flatMap(frozen -> getService().updateAccount(ident,force, 0L,0L,amount,null))
                .map(account -> account.balance-account.frozen)
                .subscribeAsCompletionStage();
    }

    @Override
    public CompletableFuture<Long> addBalance(String currency, long amount, boolean force) {
        var ident = getIdent(currency);
        return getService().updateAccount(ident,force,amount,0L,null,null)
                .map(account -> account.balance-account.frozen)
                .subscribeAsCompletionStage();
    }

    @Override
    public CompletableFuture<Long> removeBalance(String currency, long amount, boolean force) {
        var ident = getIdent(currency);
        return getService().updateAccount(ident,force,-amount,0L,null,null)
                .map(account -> account.balance-account.frozen)
                .subscribeAsCompletionStage();
    }

    @Override
    public CompletableFuture<Void> frozenBalance(String currency, long amount) {
        if (amount<0) throw new IllegalArgumentException("amount must be greater than zero");
        var ident = getIdent(currency);
        return getService().updateAccount(ident,false,0L,amount,null,null)
                .flatMap(account -> Uni.createFrom().voidItem())
                .subscribeAsCompletionStage();
    }

    @Override
    public CompletableFuture<Void> unfrozenBalance(String currency, long amount) {
        if (amount<0) throw new IllegalArgumentException("amount must be greater than zero");
        var ident = getIdent(currency);
        return getService().updateAccount(ident,false,0L,-amount,null,null)
                .flatMap(account -> Uni.createFrom().voidItem())
                .subscribeAsCompletionStage();
    }

    @Override
    public CompletableFuture<Void> transfer(String currency, long amount, Account destination) {
        var from = getIdent(currency);
        var to = getIdent(destination,currency);
        return getService().transfer(from,to,amount,amount)
                .flatMap(account -> Uni.createFrom().voidItem())
                .subscribeAsCompletionStage();
    }

    @Override
    public CompletableFuture<Void> exchange(String fromCurrency, long decreases, String toCurrency, long increases) {
        var from = getIdent(fromCurrency);
        var to = getIdent(toCurrency);
        return getService().transfer(from,to,decreases,increases)
                .flatMap(account -> Uni.createFrom().voidItem())
                .subscribeAsCompletionStage();
    }

    @Override
    public CompletableFuture<Void> transfer(String fromCurrency, long decreases, Account destination, String toCurrency, long increases) {
        var from = getIdent(fromCurrency);
        var to = getIdent(destination,toCurrency);
        return getService().transfer(from,to,decreases,increases)
                .flatMap(account -> Uni.createFrom().voidItem())
                .subscribeAsCompletionStage();
    }

    // private util
    private AccountIdent getIdent(String currency){
        if (this.type == Type.SYSTEM) return new AccountIdent(currency, AccountEntity.Type.SYSTEM, id);
        else return new AccountIdent(currency, AccountEntity.Type.USER, id);
    }

    private static AccountIdent getIdent(Account account,String currency) {
        if (account.getType() == Type.SYSTEM) return new AccountIdent(currency, AccountEntity.Type.SYSTEM, account.getId());
        else return new AccountIdent(currency, AccountEntity.Type.USER, account.getId());
    }

    private AccountService getService() {
        return Arc.container().instance(AccountService.class).get();
    }
}
