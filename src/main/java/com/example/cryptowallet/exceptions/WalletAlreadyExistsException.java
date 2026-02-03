package com.example.cryptowallet.exceptions;

public class WalletAlreadyExistsException extends RuntimeException {
    public WalletAlreadyExistsException(String email) {
        super("Wallet already exists for user with email: " + email);
    }
}
