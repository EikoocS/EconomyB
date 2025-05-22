package tech.cookiepower.economyb.entity;

import java.io.Serializable;

public record AccountIdent(String currency, AccountEntity.Type type, String uuid) implements Serializable { }