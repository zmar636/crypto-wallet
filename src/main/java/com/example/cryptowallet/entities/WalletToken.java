package com.example.cryptowallet.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "wallet_tokens")
public class WalletToken {

    @EmbeddedId
    @Setter
    private WalletTokenId id;

    @ManyToOne
    @MapsId("walletId")
    @JoinColumn(name = "wallet_id")
    @Setter
    private Wallet wallet;

    @ManyToOne
    @MapsId("tokenSymbol")
    @JoinColumn(name = "token_symbol")
    @Setter
    @Getter
    private Token token;

    @Setter
    @Getter
    private Double quantity;
}
