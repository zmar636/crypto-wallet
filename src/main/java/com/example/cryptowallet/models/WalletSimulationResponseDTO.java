package com.example.cryptowallet.models;

public record WalletSimulationResponseDTO(
        Double total,
        String bestAsset,
        Double bestPerformance,
        String worstAsset,
        Double worstPerformance) {
}
