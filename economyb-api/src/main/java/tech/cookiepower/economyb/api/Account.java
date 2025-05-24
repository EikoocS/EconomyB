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
     * */
    CompletableFuture<Void> setBalance(String currency, long amount);

    /**
     * Add the specified amount to the balance of the account
     *
     * @param currency The currency to add the amount to
     * @param amount The amount to add to the balance, if negative, it will be removed
     * */
    CompletableFuture<Void> addBalance(String currency, long amount);

    /**
     * Remove the specified amount from the balance of the account
     *
     * @param currency The currency to remove the amount from
     * @param amount The amount to remove from the balance, if negative, it will be added
     * */
    CompletableFuture<Void> removeBalance(String currency, long amount);

    /**
     * Frozen the specified balance of the account
     *
     * @param currency The currency to freeze the amount from
     * @param amount The amount to frozen from the balance
     * */
    CompletableFuture<Void> frozenBalance(String currency, long amount);

    /**
     * Unfrozen the specified balance of the account
     *
     * @param currency The currency to unfreeze the amount from
     * @param amount The amount to unfrozen from the balance
     * */
    CompletableFuture<Void> unfrozenBalance(String currency, long amount);

    /**
     * Transfer the specified amount from this account to the destination account
     *
     * @param currency The currency to unfreeze the amount from
     * @param amount The amount to unfrozen from the balance
     * @param destination The destination account to add the amount to
     * */
    CompletableFuture<Void> transfer(String currency, long amount, Account destination);

    /**
     * Transfer the specified amount from a currency to the destination currency
     *
     * @param fromCurrency The currency to remove the amount from
     * @param decreases The amount to remove from the balance
     * @param toCurrency The currency to add the amount to
     * @param increases The amount to add to the balance
     * */
    CompletableFuture<Void> exchange(String fromCurrency, long decreases,String toCurrency, long increases);

    /**
     * Full-featured transfers
     * Include features of transfer and exchange, and support different currencies and accounts
     *
     * @param fromCurrency The currency to remove the amount from
     * @param decreases The amount to remove from the balance
     * @param destination The destination account to add the amount to
     * @param toCurrency The currency to add the amount to
     * @param increases The amount to add to the balance
     * */
    CompletableFuture<Void> transfer(String fromCurrency, long decreases, Account destination, String toCurrency, long increases);

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
