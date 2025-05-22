package tech.cookiepower.economyb.api;

import java.util.concurrent.CompletableFuture;

public interface Account {
    /**
     * Get the identifier of account
     * If the type is USER, it is the player's UUID
     * If the type is SYSTEM, it is specified by administrators and developers
     * @return UUID or String set by administrators
     * **/
    String getId();

    /**
     * Get the type of account
     *
     * @return USER or SYSTEM type enum
     * */
    Type getType();

    /**
     * Get the UNFROZEN balance of the account
     *
     * @param currency The currency to get the balance of
     * @return The balance of the account in the specified currency
     * */
    CompletableFuture<Long> getBalance(String currency);

    /**
     * Test if the account has the specified amount of UNFROZEN balance
     *
     * @param currency The currency to test the balance of
     * @return True if the account has the specified amount of balance, false otherwise
     * */
    CompletableFuture<Boolean> hasBalance(String currency, long amount);

    /**
     * Set the UNFROZEN balance of the account
     *
     * @param currency The currency to set the balance of
     * @param amount The amount to set the balance to
     * @param force If true, the balance will be set even if it is negative
     * @return The new balance of the account in the specified currency
     * */
    CompletableFuture<Long> setBalance(String currency, long amount, boolean force);

    /**
     * Add the specified amount to the balance of the account
     *
     * @param currency The currency to add the amount to
     * @param amount The amount to add to the balance, if negative, it will be removed
     * @param force If true, the amount will be added even if it is negative
     * @return The new balance of the account in the specified currency
     * */
    CompletableFuture<Long> addBalance(String currency, long amount, boolean force);

    /**
     * Remove the specified amount from the balance of the account
     *
     * @param currency The currency to remove the amount from
     * @param amount The amount to remove from the balance, if negative, it will be added
     * @param force If true, the amount will be removed even if it is negative
     * @return The new balance of the account in the specified currency
     *         If the amount is negative and force is false, it will fail
     * */
    CompletableFuture<Long> removeBalance(String currency, long amount, boolean force);

    /**
     * Frozen the specified balance of the account
     *
     * @param currency The currency to freeze the amount from
     * @param amount The amount to frozen from the balance
     * @return if the amount is negative or unfrozen balance is less than the amount, it will fail
     * */
    CompletableFuture<Long> frozenBalance(String currency, long amount);

    /**
     * Unfrozen the specified balance of the account
     *
     * @param currency The currency to unfreeze the amount from
     * @param amount The amount to unfrozen from the balance
     * @return if the amount is negative or frozen balances is less than the amount, it will fail
     * */
    CompletableFuture<Long> unfrozenBalance(String currency, long amount);

    /**
     * Transfer the specified amount from this account to the destination account
     *
     * @param currency The currency to unfreeze the amount from
     * @param amount The amount to unfrozen from the balance
     * @param destination The destination account to add the amount to
     * @param force If true, the amount will be transferred even if it is negative
     * @return Success execute action or not
     * */
    CompletableFuture<Boolean> transfer(String currency, long amount, Account destination, boolean force);

    /**
     * Transfer the specified amount from a currency to the destination currency
     *
     * @param fromCurrency The currency to remove the amount from
     * @param decreases The amount to remove from the balance
     * @param toCurrency The currency to add the amount to
     * @param increases The amount to add to the balance
     * @param force If true, the amount will be transferred even if it is negative
     * @return Success execute action or not
     * */
    CompletableFuture<Boolean> exchange(String fromCurrency, long decreases,String toCurrency, long increases, boolean force);

    /**
     * Full-featured transfers
     * Include features of transfer and exchange, and support different currencies and accounts
     *
     * @param fromCurrency The currency to remove the amount from
     * @param decreases The amount to remove from the balance
     * @param destination The destination account to add the amount to
     * @param toCurrency The currency to add the amount to
     * @param increases The amount to add to the balance
     * @param force If true, the amount will be transferred even if it is negative
     * @return Success execute action or not
     * */
    CompletableFuture<Boolean> transfer(String fromCurrency, long decreases, Account destination, String toCurrency, long increases, boolean force);

    enum Type {
        USER,
        SYSTEM;
        public boolean isSystem() {
            return this == SYSTEM;
        }
        public boolean isUser() {
            return this == USER;
        }
    }
}
