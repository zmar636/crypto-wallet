package com.example.cryptowallet.models;

public record AssetInputDTO(
        String symbol,
        Double quantity,
        Double value) {
}
