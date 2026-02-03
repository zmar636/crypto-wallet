package com.example.cryptowallet.controllers;

import com.example.cryptowallet.entities.Wallet;
import com.example.cryptowallet.entities.WalletToken;
import com.example.cryptowallet.models.WalletInfoDTO;
import com.example.cryptowallet.models.WalletResponseDTO;
import com.example.cryptowallet.models.WalletSimulationRequestDTO;
import com.example.cryptowallet.models.WalletSimulationResponseDTO;
import com.example.cryptowallet.services.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/wallets")
@Tag(name = "Wallet", description = "Operations for wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @Operation(summary = "Create wallet for an user")
    @ApiResponse(responseCode = "201", description = "Wallet created")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "400", description = "Wallet already exists")
    @PostMapping("/")
    public ResponseEntity<?> createWallet(@RequestParam @Email String email) {
        Wallet wallet = walletService.createWalletForUser(email);

        WalletResponseDTO responseDTO = new WalletResponseDTO(
                wallet.getId(),
                email,
                Collections.emptySet()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @Operation(summary = "Add tokens to an existent wallet")
    @ApiResponse(responseCode = "201", description = "Tokens added")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "404", description = "Wallet not found")
    @PostMapping("/add-token")
    public ResponseEntity<?> addToken(@RequestParam @Email String email,
                                      @RequestParam @NotBlank String symbol,
                                      @RequestParam @Positive Double quantity) {

        WalletToken walletToken = walletService.addTokenToWallet(email, symbol, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(walletToken);
    }

    @Operation(summary = "Get wallet information")
    @ApiResponse(responseCode = "200", description = "Wallet information")
    @ApiResponse(responseCode = "404", description = "Wallet not found")
    @GetMapping("/{walletId}")
    public ResponseEntity<WalletInfoDTO> getWalletInfo(@PathVariable Long walletId) {
        WalletInfoDTO walletInfo = walletService.getWalletInfo(walletId);
        return ResponseEntity.ok(walletInfo);
    }

    @PostMapping("/simulate")
    @Operation(summary = "Get wallet simulation")
    @ApiResponse(responseCode = "200", description = "Wallet simulation")
    public ResponseEntity<WalletSimulationResponseDTO> simulateWallet(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = WalletSimulationRequestDTO.class)))
            @RequestBody WalletSimulationRequestDTO request) {
        WalletSimulationResponseDTO response = walletService.simulateWallet(request);
        return ResponseEntity.ok(response);
    }
}
