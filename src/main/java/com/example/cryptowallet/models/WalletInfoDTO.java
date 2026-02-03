package com.example.cryptowallet.models;

import java.util.List;

public record WalletInfoDTO(
        Long walletId,
        List<WalletInfoTokenDTO> assets,
        Double totalValue) {
}
