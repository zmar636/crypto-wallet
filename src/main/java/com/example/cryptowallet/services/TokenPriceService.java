package com.example.cryptowallet.services;

import com.example.cryptowallet.entities.Token;
import com.example.cryptowallet.models.CoinCapAsset;
import com.example.cryptowallet.repos.TokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TokenPriceService {

    @Value("${token.price.update.interval}")
    private long updateInterval;

    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    private final TokenRepository tokenRepository;
    private final PricesApi pricesApi;

    private static final Logger logger = LoggerFactory.getLogger(TokenPriceService.class);

    public TokenPriceService(TokenRepository tokenRepository, PricesApi pricesApi) {
        this.tokenRepository = tokenRepository;
        this.pricesApi = pricesApi;
    }

    @Scheduled(fixedDelayString = "${token.price.update.interval}000")
    public void updateTokenPrices() {
        List<Token> tokens = tokenRepository.findAll();

        List<Callable<Void>> tasks = tokens
                .stream()
                .map(token -> (Callable<Void>) () -> {
                    fetchAndUpdateTokenPrice(token);
                    return null;
                }).toList();

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
           logger.error("Price api request failed", e);
        }
    }

    private void fetchAndUpdateTokenPrice(Token token) throws JsonProcessingException {
        double price = pricesApi.fetchTokenPrice(token.getSymbol());
        token.setPrice(String.valueOf(price));
        tokenRepository.save(token);
    }
}
