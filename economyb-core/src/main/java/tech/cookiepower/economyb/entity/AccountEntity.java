package tech.cookiepower.economyb.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;

@Entity
@IdClass(AccountIdent.class)
@Table(name= "economyb_accounts")
public class AccountEntity extends PanacheEntityBase {
    @Id
    @Column(nullable = false, length = 20)
    public String currency;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('SYSTEM', 'USER')")
    public Type type;

    @Id
    @Column(nullable = false, length = 36)
    public String uuid;

    @Column(nullable = false)
    public long balance = 0;

    @Column(nullable = false)
    public long frozen = 0;

    public enum Type {
        SYSTEM,
        USER;
    }

    public void setIdent(@Nonnull AccountIdent ident) {
        this.currency = ident.currency();
        this.type = ident.type();
        this.uuid = ident.uuid();
    }
}
