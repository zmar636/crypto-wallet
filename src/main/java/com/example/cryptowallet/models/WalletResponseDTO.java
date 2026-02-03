package com.example.cryptowallet.models;

import java.util.Set;

public record WalletResponseDTO(
        Long walletId,
        String userEmail,
        Set<String> tokens
) {
}
