package tech.cookiepower.economyb.api;

import java.util.UUID;

public interface EconomybAPI {
    /**
     * Check if the API is ready to be used
     * */
    boolean ready();

    /**
     * Get the account with the specified identifier and type
     *
     * @param identifier The identifier of the account
     * @param type The type of the account
     * @return The account with the specified identifier and type
     * */
    Account getAccount(String identifier,Account.Type type);

    /**
     * Get the account with the specified identifier
     *
     * @param uuid The identifier of the account
     * @return The account with the specified identifier
     * */
    Account getPlayerAccount(UUID uuid);
    /**
     * Get the account with the specified identifier
     *
     * @param uuid The identifier of the account
     * @return The account with the specified identifier
     * */
    Account getPlayerAccount(String uuid);

    /**
     * Get the account with the specified identifier
     *
     * @param identifier The identifier of the account
     * @return The account with the specified identifier
     * */
    Account getSystemAccount(UUID identifier);

    /**
     * Get the account with the specified identifier
     *
     * @param identifier The identifier of the account
     * @return The account with the specified identifier
     * */
    Account getSystemAccount(String identifier);
}
