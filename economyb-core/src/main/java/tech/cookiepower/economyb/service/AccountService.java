package tech.cookiepower.economyb.service;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import tech.cookiepower.economyb.entity.AccountEntity;
import tech.cookiepower.economyb.entity.AccountIdent;

@ApplicationScoped
public class AccountService {

    public Uni<Boolean> exists(@Nonnull AccountIdent accountIdent) {
        return AccountEntity.count("currency = ?1 and type = ?2 and id = ?3",
                        accountIdent.currency(),
                        accountIdent.type(),
                        accountIdent.uuid())
                .map(count -> count > 0);
    }

    @Transactional
    public Uni<AccountEntity> insertAccount(@Nonnull AccountIdent accountIdent) {
        return AccountEntity.<AccountEntity>findById(accountIdent)
                .onItem().ifNull().continueWith(() -> {
                    var entity = new AccountEntity();
                    entity.setIdent(accountIdent);
                    entity.balance = 0;
                    entity.frozen = 0;
                    return entity;
                })
                .call(entity -> entity.persist());
    }

    public Uni<Long> getBalance(@Nonnull AccountIdent accountIdent) {
        return AccountEntity.<AccountEntity>findById(accountIdent)
                .onItem().ifNotNull().transform(entity ->
                                entity.balance
                        )
                .onItem().ifNull().continueWith(0L);
    }

    public Uni<Long> getFrozenBalance(@Nonnull AccountIdent accountIdent) {
        return AccountEntity.findById(accountIdent)
                .onItem().ifNotNull().transform(entity ->
                        ((AccountEntity)entity).balance
                )
                .onItem().ifNull().continueWith(0L);
    }

    @Transactional
    public Uni<AccountEntity> updateAccount(
            @Nonnull AccountIdent accountIdent,
            @Nonnull Boolean allowNegativeBalance,
            @Nullable Long balanceDelta,
            @Nullable Long frozenDelta,
            @Nullable Long setBalanceTo,
            @Nullable Long setFrozenTo
    ) {
        return AccountEntity.<AccountEntity>findById(accountIdent)
                .onItem().ifNotNull().transformToUni(entity -> {
                    if (setBalanceTo != null) {
                        if (setBalanceTo<0&&!allowNegativeBalance) throw new IllegalArgumentException("set balance to negative and not set allowNegativeBalance");
                        entity.balance = setBalanceTo;
                    } else if (balanceDelta != null) {
                        if (entity.balance+balanceDelta<0&&!allowNegativeBalance) throw new IllegalArgumentException("set balance to negative and not set allowNegativeBalance");
                        entity.balance += balanceDelta;
                    }

                    if (setFrozenTo != null) {
                        if (setFrozenTo<0) throw new IllegalArgumentException("set frozen to negative");
                        entity.frozen = setFrozenTo;
                    } else if (frozenDelta != null) {
                        if (entity.frozen+frozenDelta<0) throw new IllegalArgumentException("set frozen to negative");
                        entity.frozen += frozenDelta;
                    }

                    if (entity.frozen > entity.balance) throw new IllegalArgumentException("set frozen to greater than balance");

                    return entity.persist();
                });
    }

    @Transactional
    public Uni<Boolean> transfer(
            @Nonnull AccountIdent fromIdent, @Nonnull AccountIdent toIdent,
            long decreases,long increases
    ) {
        if (decreases <= 0 || increases < 0) {
            return Uni.createFrom().item(false); // 不允许负数转账
        }

        return AccountEntity.<AccountEntity>findById(fromIdent)
                .flatMap(from -> {
                    if (from == null) return Uni.createFrom().item(false);

                    long available = from.balance - from.frozen;
                    if (available < decreases) return Uni.createFrom().item(false);

                    from.balance -= decreases;

                    return AccountEntity.<AccountEntity>findById(toIdent)
                            .flatMap(to -> {
                                if (to == null) return Uni.createFrom().item(false);

                                to.balance += increases;

                                return Uni.combine().all().unis(from.persist(), to.persist()).with(ignored -> true);
                            });
                });
    }


}
