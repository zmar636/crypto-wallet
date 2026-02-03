package com.example.cryptowallet.models;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record WalletSimulationRequestDTO(
        @ArraySchema(schema = @Schema(implementation = AssetInputDTO.class), minItems = 1) List<AssetInputDTO> assets,
        @Schema(
                example = "2024-01-01",
                description = "Simulation date in ISO-8601 format"
        )
        String date
) {
}
