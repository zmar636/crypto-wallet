package com.example.cryptowallet.services;

import com.example.cryptowallet.entities.*;
import com.example.cryptowallet.exceptions.UserNotFoundException;
import com.example.cryptowallet.exceptions.WalletAlreadyExistsException;
import com.example.cryptowallet.exceptions.WalletNotFoundException;
import com.example.cryptowallet.models.*;
import com.example.cryptowallet.repos.TokenRepository;
import com.example.cryptowallet.repos.UserRepository;
import com.example.cryptowallet.repos.WalletRepository;
import com.example.cryptowallet.repos.WalletTokenRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TokenRepository tokenRepository;
    private final WalletTokenRepository walletTokenRepository;
    private final PricesApi pricesApi;

    public WalletService(UserRepository userRepository, WalletRepository walletRepository, TokenRepository tokenRepository, WalletTokenRepository walletTokenRepository, PricesApi pricesApi) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.tokenRepository = tokenRepository;
        this.walletTokenRepository = walletTokenRepository;
        this.pricesApi = pricesApi;
    }

    public Wallet createWalletForUser(String email) throws IllegalStateException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        Optional<Wallet> existentWallet = walletRepository.findByUser(user);

        if (existentWallet.isPresent()) {
            throw new WalletAlreadyExistsException(email);
        }

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet = walletRepository.save(wallet);
        return wallet;
    }

    public WalletToken addTokenToWallet(String email, String symbol, Double quantity) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new WalletNotFoundException(email));

        String price = String.valueOf(pricesApi.fetchTokenPrice(symbol));
        CoinCapAsset coinCapAsset = pricesApi.fetchAssetData(symbol);

        Token token = tokenRepository.findById(symbol)
                .orElseGet(() -> {
                    Token t = new Token();
                    t.setSymbol(symbol);
                    t.setSlug(coinCapAsset.id());
                    t.setPrice(price);
                    return tokenRepository.save(t);
                });

        WalletTokenId id = new WalletTokenId(wallet.getId(), token.getSymbol());

        WalletToken walletToken = walletTokenRepository.findById(id)
                .orElseGet(() -> {
                    WalletToken wt = new WalletToken();
                    wt.setId(id);
                    wt.setWallet(wallet);
                    wt.setToken(token);
                    return wt;
                });

        walletToken.setQuantity(quantity);
        return walletTokenRepository.save(walletToken);
    }

    public WalletInfoDTO getWalletInfo(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        List<WalletInfoTokenDTO> tokenInfos = wallet.getTokens().stream()
                .map(wt -> {
                    Token token = wt.getToken();
                    Double price = Double.parseDouble(token.getPrice());
                    Double value = price * wt.getQuantity();
                    return new WalletInfoTokenDTO(token.getSymbol(), token.getPrice(), wt.getQuantity(), value);
                })
                .collect(Collectors.toList());

        Double totalValue = tokenInfos.stream()
                .mapToDouble(WalletInfoTokenDTO::value)
                .sum();

        return new WalletInfoDTO(wallet.getId(), tokenInfos, totalValue);
    }

    public WalletSimulationResponseDTO simulateWallet(WalletSimulationRequestDTO request) {
        List<AssetInputDTO> assets = request.assets();
        String dateStr = request.date();

        Map<String, Double> priceByAsset = new HashMap<>();

        double total = 0;
        String bestAsset = null;
        double bestPerformance = Double.NEGATIVE_INFINITY;
        String worstAsset = null;
        double worstPerformance = Double.POSITIVE_INFINITY;

        List<String> symbols = request.assets().stream().map(AssetInputDTO::symbol).toList();

        List<CoinCapAsset> coinCapAssets = pricesApi.fetchAssetsData(symbols);
        for (CoinCapAsset coinCapAsset : coinCapAssets) {
            double historicalPrice = pricesApi.fetchHistoricalPrice(coinCapAsset.id(), dateStr);
            priceByAsset.put(coinCapAsset.symbol(), historicalPrice);
        }


        for (AssetInputDTO asset : assets) {
            String symbol = asset.symbol();
            double quantity = asset.quantity();
            double initialPrice = asset.value() / quantity;

            double historicalPrice = priceByAsset.get(asset.symbol());

            double value = quantity * historicalPrice;
            double performance = (historicalPrice - initialPrice) / initialPrice * 100;

            // update total
            total += value;

            // check best/worst
            if (performance > bestPerformance) {
                bestPerformance = performance;
                bestAsset = symbol;
            }
            if (performance < worstPerformance) {
                worstPerformance = performance;
                worstAsset = symbol;
            }
        }

        total = Math.round(total * 100.0) / 100.0;
        bestPerformance = Math.round(bestPerformance * 100.0) / 100.0;
        worstPerformance = Math.round(worstPerformance * 100.0) / 100.0;

        return new WalletSimulationResponseDTO(total, bestAsset, bestPerformance, worstAsset, worstPerformance);
    }
}
