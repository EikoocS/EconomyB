package tech.cookiepower.economyb;

import tech.cookiepower.economyb.api.Account;

public record AccountIdent(
        String currency,
        Account.Type type,
        String identifier
){ }