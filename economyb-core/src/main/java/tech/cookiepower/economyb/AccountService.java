package tech.cookiepower.economyb;

import io.vertx.core.Future;
import io.vertx.mysqlclient.MySQLBuilder;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.*;
import io.vertx.sqlclient.Tuple;

import java.util.concurrent.CompletableFuture;

public class AccountService {
    public static AccountService INSTANCE = null;
    private final Pool sqlPool;

    public static void init() {
        if (INSTANCE != null) throw new IllegalStateException("already initialized");
        INSTANCE = new AccountService();
    }

    public static AccountService getInstance() {
        if (INSTANCE == null) throw new IllegalStateException("not initialized");
        return INSTANCE;
    }

    private AccountService() {
        var connectOptions = new MySQLConnectOptions()
                .setHost("127.0.0.1")
                .setPort(3306)
                .setUser("root")
                .setDatabase("economyb");

        var poolOptions = new PoolOptions().setMaxSize(5);

        sqlPool = MySQLBuilder
                .pool()
                .with(poolOptions)
                .connectingTo(connectOptions)
                .build();

        createTableIfNotExists().join(); // 初始化时同步建表（只调用一次）
    }

    public CompletableFuture<Void> createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS economyb_accounts (
                    currency VARCHAR(20) NOT NULL,
                    type ENUM('SYSTEM', 'USER') NOT NULL,
                    identifier CHAR(36) NOT NULL,
                    balance BIGINT NOT NULL DEFAULT 0,
                    frozen BIGINT NOT NULL DEFAULT 0,
                    PRIMARY KEY (currency, type, identifier)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
                """;

        return sqlPool.query(sql)
                .execute()
                .toCompletionStage()
                .toCompletableFuture()
                .thenApply(rs -> null);
    }

    public CompletableFuture<Boolean> existsAccount(AccountIdent accountIdent) {
        String sql = "SELECT 1 FROM economyb_accounts WHERE currency = ? AND type = ? AND identifier = ?";
        Tuple params = Tuple.of(accountIdent.currency(), accountIdent.type(), accountIdent.identifier());

        return sqlPool.preparedQuery(sql)
                .execute(params)
                .toCompletionStage()
                .toCompletableFuture()
                .thenApply(result -> result.size() > 0);
    }

    public CompletableFuture<Void> insertAccount(AccountIdent accountIdent) {
        String sql = "INSERT IGNORE INTO economyb_accounts(currency, type, identifier) VALUES (?, ?, ?)";
        Tuple params = Tuple.of(accountIdent.currency(), accountIdent.type(), accountIdent.identifier());

        return sqlPool.withTransaction(conn ->
                conn.preparedQuery(sql)
                        .execute(params)
        ).toCompletionStage().toCompletableFuture().thenApply(res -> null);
    }

    private CompletableFuture<Void> ensureAccountExists(AccountIdent accountIdent) {
        return existsAccount(accountIdent).thenCompose(exists -> {
            if (exists) return CompletableFuture.completedFuture(null);
            return insertAccount(accountIdent);
        });
    }

    public CompletableFuture<Long> getBalance(AccountIdent accountIdent) {
        return ensureAccountExists(accountIdent).thenCompose(unused -> {
            String sql = "SELECT balance FROM economyb_accounts WHERE currency = ? AND type = ? AND identifier = ?";
            Tuple params = Tuple.of(accountIdent.currency(), accountIdent.type(), accountIdent.identifier());

            return sqlPool.preparedQuery(sql)
                    .execute(params)
                    .toCompletionStage()
                    .toCompletableFuture()
                    .thenApply(rows -> rows.iterator().next().getLong("balance"));
        });
    }

    public CompletableFuture<Long> getFrozenBalance(AccountIdent accountIdent) {
        return ensureAccountExists(accountIdent).thenCompose(unused -> {
            String sql = "SELECT frozen FROM economyb_accounts WHERE currency = ? AND type = ? AND identifier = ?";
            Tuple params = Tuple.of(accountIdent.currency(), accountIdent.type(), accountIdent.identifier());

            return sqlPool.preparedQuery(sql)
                    .execute(params)
                    .toCompletionStage()
                    .toCompletableFuture()
                    .thenApply(rows -> rows.iterator().next().getLong("frozen"));
        });
    }

    public CompletableFuture<Long> setBalance(AccountIdent accountIdent, long balance) {
        return ensureAccountExists(accountIdent).thenCompose(unused -> {
            String sql = "UPDATE economyb_accounts SET balance = ? WHERE currency = ? AND type = ? AND identifier = ?";
            Tuple params = Tuple.of(balance, accountIdent.currency(), accountIdent.type(), accountIdent.identifier());

            return sqlPool.preparedQuery(sql)
                    .execute(params)
                    .toCompletionStage()
                    .toCompletableFuture()
                    .thenApply(rows -> balance);
        });
    }

    public CompletableFuture<Void> modifyBalance(AccountIdent accountIdent, long delta, boolean allowNegative) {
        return ensureAccountExists(accountIdent).thenCompose(unused -> {
            String sql = allowNegative
                    ? "UPDATE economyb_accounts SET balance = balance + ? WHERE currency = ? AND type = ? AND identifier = ?"
                    : "UPDATE economyb_accounts SET balance = balance + ? WHERE currency = ? AND type = ? AND identifier = ? AND balance + ? >= 0";

            Tuple params = allowNegative
                    ? Tuple.of(delta, accountIdent.currency(), accountIdent.type(), accountIdent.identifier())
                    : Tuple.of(delta, accountIdent.currency(), accountIdent.type(), accountIdent.identifier(), delta);

            return sqlPool.preparedQuery(sql)
                    .execute(params)
                    .toCompletionStage()
                    .toCompletableFuture()
                    .thenApply(rows -> {
                        if (rows.rowCount() > 0) return null;
                        else throw new IllegalStateException("Insufficient funds or account not found");
                    });
        });
    }

    public CompletableFuture<Long> freezeAmount(AccountIdent accountIdent, long amount) {
        return ensureAccountExists(accountIdent).thenCompose(unused -> {
            String sql = """
                UPDATE economyb_accounts\s
                SET frozen = frozen + ?\s
                WHERE currency = ? AND type = ? AND identifier = ?\s
                  AND balance - frozen >= ?
           \s""";

            Tuple params = Tuple.of(amount, accountIdent.currency(), accountIdent.type(), accountIdent.identifier(), amount);

            return sqlPool.preparedQuery(sql)
                    .execute(params)
                    .toCompletionStage()
                    .toCompletableFuture()
                    .thenApply(rows -> {
                        if (rows.rowCount() == 0) {
                            throw new IllegalStateException("Insufficient available balance to freeze");
                        }
                        return amount;
                    });
        });
    }

    public CompletableFuture<Long> unfreezeAmount(AccountIdent accountIdent, long amount) {
        return ensureAccountExists(accountIdent).thenCompose(unused -> {
            String sql = """
                UPDATE economyb_accounts
                SET frozen = frozen - ?
                WHERE currency = ? AND type = ? AND identifier = ?
                  AND frozen >= ?
            """;

            Tuple params = Tuple.of(amount, accountIdent.currency(), accountIdent.type(), accountIdent.identifier(), amount);

            return sqlPool.preparedQuery(sql)
                    .execute(params)
                    .toCompletionStage()
                    .toCompletableFuture()
                    .thenApply(rows -> {
                        if (rows.rowCount() == 0) {
                            throw new IllegalStateException("Insufficient frozen balance to unfreeze");
                        }
                        return amount;
                    });
        });
    }

    public CompletableFuture<Boolean> transfer(AccountIdent fromIdent, AccountIdent toIdent, long decrease, long increase, boolean allowNegative) {
        return ensureAccountExists(fromIdent).thenCompose(unused1 ->
                ensureAccountExists(toIdent).thenCompose(unused2 -> {
                    String decreaseSql = allowNegative
                            ? "UPDATE economyb_accounts SET balance = balance - ? WHERE currency = ? AND type = ? AND identifier = ?"
                            : "UPDATE economyb_accounts SET balance = balance - ? WHERE currency = ? AND type = ? AND identifier = ? AND balance - ? >= 0";

                    Tuple decParams = allowNegative
                            ? Tuple.of(decrease, fromIdent.currency(), fromIdent.type(), fromIdent.identifier())
                            : Tuple.of(decrease, fromIdent.currency(), fromIdent.type(), fromIdent.identifier(), decrease);

                    String increaseSql = "UPDATE economyb_accounts SET balance = balance + ? WHERE currency = ? AND type = ? AND identifier = ?";
                    Tuple incParams = Tuple.of(increase, toIdent.currency(), toIdent.type(), toIdent.identifier());

                    return sqlPool.withTransaction(conn ->
                            conn.preparedQuery(decreaseSql).execute(decParams)
                                    .compose(res -> {
                                        if (res.rowCount() == 0) {
                                            return Future.failedFuture("Transfer failed due to insufficient funds or missing sender");
                                        }
                                        return conn.preparedQuery(increaseSql).execute(incParams);
                                    })
                    ).toCompletionStage().toCompletableFuture().thenApply(res -> true);
                })
        );
    }
}
