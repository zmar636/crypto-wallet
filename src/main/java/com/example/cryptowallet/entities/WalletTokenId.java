package com.example.cryptowallet.entities;

import lombok.Getter;

import java.util.Objects;

public class WalletTokenId {
    @Getter
    private Long walletId;
    @Getter
    private String tokenSymbol;

    public WalletTokenId() {
    }

    public WalletTokenId(Long walletId, String tokenSymbol) {
        this.walletId = walletId;
        this.tokenSymbol = tokenSymbol;
    }

    // equals & hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WalletTokenId)) return false;
        WalletTokenId that = (WalletTokenId) o;
        return Objects.equals(walletId, that.getWalletId())
                && Objects.equals(tokenSymbol, that.getTokenSymbol());
    }

    @Override
    public int hashCode() {
        return Objects.hash(walletId, tokenSymbol);
    }
}
