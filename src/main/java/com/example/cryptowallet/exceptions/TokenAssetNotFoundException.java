package com.example.cryptowallet.exceptions;

public class TokenAssetNotFoundException extends RuntimeException {
    public TokenAssetNotFoundException(String symbol) {
        super("Token asset not found for symbol: " + symbol);
    }
}
