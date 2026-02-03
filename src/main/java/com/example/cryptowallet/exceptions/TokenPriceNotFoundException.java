package com.example.cryptowallet.exceptions;

public class TokenPriceNotFoundException extends RuntimeException {
    public TokenPriceNotFoundException(String tokenSymbol) {
        super(tokenSymbol);
    }
}
