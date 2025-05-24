package tech.cookiepower.economyb.api;

import java.util.concurrent.CompletableFuture;

public interface Accounts {

    /**
     * Get the UNFROZEN balance of the account
     *
     * @param currency The currency to get the balance of
     * @param type The type of the account
     * @param id The identifier of the account
     * @return The balance of the account in the specified currency
     * */
    CompletableFuture<Long> getBalance(String currency, Account.Type type, String id);

    /**
     * Test if the account has the specified amount of UNFROZEN balance
     *
     * @param currency The currency to get the balance of
     * @param type The type of the account
     * @param id The identifier of the account
     * @return True if the account has the specified amount of balance, false otherwise
     * */
    CompletableFuture<Boolean> hasBalance(String currency, Account.Type type, String id, long amount);

    /**
     * Set the UNFROZEN balance of the account
     *
     * @param currency The currency to get the balance of
     * @param type The type of the account
     * @param id The identifier of the account
     * @param amount The amount to set the balance to
     * */
    CompletableFuture<Void> setBalance(String currency, Account.Type type, String id, long amount);

    /**
     * Add the specified amount to the balance of the account
     *
     * @param currency The currency to get the balance of
     * @param type The type of the account
     * @param id The identifier of the account
     * @param amount The amount to add to the balance, if negative, it will be removed
     * */
    CompletableFuture<Void> addBalance(String currency, Account.Type type, String id, long amount);

    /**
     * Remove the specified amount from the balance of the account
     *
     * @param currency The currency to get the balance of
     * @param type The type of the account
     * @param id The identifier of the account
     * @param amount The amount to remove from the balance, if negative, it will be added
     * */
    CompletableFuture<Void> removeBalance(String currency, Account.Type type, String id, long amount);

    /**
     * Frozen the specified balance of the account
     *
     * @param currency The currency to get the balance of
     * @param type The type of the account
     * @param id The identifier of the account
     * @param amount The amount to frozen from the balance
     * */
    CompletableFuture<Void> frozenBalance(String currency, Account.Type type, String id, long amount);

    /**
     * Unfrozen the specified balance of the account
     *
     * @param currency The currency to get the balance of
     * @param type The type of the account
     * @param id The identifier of the account
     * @param amount The amount to unfrozen from the balance
     * */
    CompletableFuture<Void> unfrozenBalance(String currency, Account.Type type, String id, long amount);

    /**
     * Full-featured transfers
     * Include features of transfer and exchange, and support different currencies and account.
     * */
    CompletableFuture<Void> transfer(
            String fromCurrency,  Account.Type fromType, String fromId, long decreases,
            String toCurrency,  Account.Type toType, String toId, long increases);

}
