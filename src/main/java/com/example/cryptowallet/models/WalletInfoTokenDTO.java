package com.example.cryptowallet.models;

public record WalletInfoTokenDTO(
        String symbol,
        String price,
        Double quantity,
        Double value) {
}
