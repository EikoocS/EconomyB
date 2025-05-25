package tech.cookiepower.economyb;

import io.vertx.mysqlclient.MySQLBuilder;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.*;
import io.vertx.sqlclient.Tuple;
import tech.cookiepower.economyb.api.Account;

import java.util.concurrent.CompletableFuture;

public class AccountService {
    public static AccountService INSTANCE = null;
    private final Pool sqlPool;

    public static void init(String host, int port, String user, String password, String database) {
        if (INSTANCE != null) throw new IllegalStateException("already initialized");
        INSTANCE = new AccountService(host, port, user, password, database);
    }

    public static AccountService getInstance() {
        if (INSTANCE == null) throw new IllegalStateException("not initialized");
        return INSTANCE;
    }

    private AccountService(String host, int port, String user, String password, String database) {
        var connectOptions = new MySQLConnectOptions()
                .setHost(host)
                .setPort(port)
                .setUser(user)
                .setPassword(password)
                .setDatabase(database);

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

    public CompletableFuture<Boolean> exist(String currency, Account.Type type, String identifier) {
        String sql = """
            SELECT EXISTS(
                SELECT 1 FROM economyb_accounts WHERE currency = ? AND type = ? AND identifier = ?
            ) AS `exists`
        """;

        Tuple params = Tuple.of(currency, type, identifier);

        return sqlPool.preparedQuery(sql)
                .execute(params)
                .toCompletionStage()
                .toCompletableFuture()
                .thenApply(result -> result.iterator().next().getBoolean("exists"));
    }

    public CompletableFuture<Void> create(String currency, Account.Type type, String identifier) {
        String sql = """
            INSERT IGNORE INTO economyb_accounts (currency, type, identifier)VALUE (?,?,?);
        """;

        Tuple params = Tuple.of(currency, type, identifier);

        return sqlPool.withTransaction(conn ->
                conn.preparedQuery(sql)
                        .execute(params)
        ).toCompletionStage().toCompletableFuture().thenApply(res -> null);
    }

    public CompletableFuture<Long> get(String currency, Account.Type type, String identifier) {
        String sql = """
            SELECT COALESCE(SUM(balance), 0) AS balance
            FROM economyb_accounts
            WHERE currency = ? AND type = ? AND identifier = ?;
        """;

        Tuple params = Tuple.of(currency, type, identifier);

        return sqlPool.preparedQuery(sql)
                .execute(params)
                .toCompletionStage()
                .toCompletableFuture()
                .thenApply(res -> res.iterator().next().getLong("balance"));
    }

    public CompletableFuture<Void> set(String currency, Account.Type type, String identifier, long balance) {
        String sql = """
                INSERT INTO economyb_accounts (currency, type, identifier, balance)VALUE (?,?,?,?)
                ON DUPLICATE KEY UPDATE balance = ?;
        """;

        Tuple params = Tuple.of(currency, type, identifier, balance, balance);

        return sqlPool.preparedQuery(sql)
                .execute(params)
                .toCompletionStage()
                .toCompletableFuture()
                .thenApply(res -> null);
    }

    public CompletableFuture<Void> modify(String currency, Account.Type type, String identifier, long delta) {
        String sql = """
                INSERT INTO economyb_accounts (currency, type, identifier, balance)VALUE (?,?,?,?)
                ON DUPLICATE KEY UPDATE balance = balance+?;
        """;

        Tuple params = Tuple.of(currency, type, identifier, delta, delta);

        return sqlPool.preparedQuery(sql)
                .execute(params)
                .toCompletionStage()
                .toCompletableFuture()
                .thenApply(res -> null);
    }

    public CompletableFuture<Long> getFrozen(String currency, Account.Type type, String identifier) {
        String sql = """
            SELECT COALESCE(SUM(frozen), 0) AS frozen
            FROM economyb_accounts
            WHERE currency = ? AND type = ? AND identifier = ?;
        """;

        Tuple params = Tuple.of(currency, type, identifier);

        return sqlPool.preparedQuery(sql)
                .execute(params)
                .toCompletionStage()
                .toCompletableFuture()
                .thenApply(res -> res.iterator().next().getLong("frozen"));
    }

    public CompletableFuture<Void> freeze(String currency, Account.Type type, String identifier, long amount) {
        String sql = """
                INSERT INTO economyb_accounts (currency, type, identifier, balance, frozen)VALUE (?,?,?,-?,?)
                ON DUPLICATE KEY UPDATE balance = balance-?, frozen = frozen+?;
                """;

        Tuple params = Tuple.of(currency, type, identifier, amount, amount, amount, amount);

        return sqlPool.preparedQuery(sql)
                .execute(params)
                .toCompletionStage()
                .toCompletableFuture()
                .thenApply(rows -> null);
    }

    public CompletableFuture<Void> unfreeze(String currency, Account.Type type, String identifier, long amount) {
        String sql = """
                INSERT INTO economyb_accounts (currency, type, identifier, balance, frozen)VALUE (?,?,?,?,-?)
                ON DUPLICATE KEY UPDATE balance = balance+?, frozen = frozen-?;
                """;

        Tuple params = Tuple.of(currency, type, identifier, amount, amount, amount, amount);

        return sqlPool.preparedQuery(sql)
                .execute(params)
                .toCompletionStage()
                .toCompletableFuture()
                .thenApply(rows -> null);
    }

            public CompletableFuture<Void> transfer(
            String fromCurrency, Account.Type fromType, String fromIdentifier, long decrease,
            String toCurrency, Account.Type toType, String toIdentifier, long increase) {
        String sql = """
                INSERT INTO economyb_accounts (currency, type, identifier, balance)VALUE (?,?,?,?)
                ON DUPLICATE KEY UPDATE balance = balance+?;
                """;

        Tuple fromParams = Tuple.of(fromCurrency, fromType, fromIdentifier, -decrease, -decrease);
        Tuple toParams = Tuple.of(toCurrency, toType, toIdentifier, increase, increase);

        return sqlPool.withTransaction(conn -> conn.preparedQuery(sql)
                .execute(fromParams)
                .compose(res -> conn.preparedQuery(sql).execute(toParams))).toCompletionStage()
                .toCompletableFuture()
                .thenApply(rows -> null);
    }
}
