package com.example.cryptowallet.exceptions;

public class WalletNotFoundException extends  RuntimeException {
    public WalletNotFoundException(String email) {
        super("Wallet not found for email: " + email);
    }

    public WalletNotFoundException(Long id) {
        super("Wallet not found for id: " + id);
    }
}
