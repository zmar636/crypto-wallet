package com.example.cryptowallet.services;

import com.example.cryptowallet.exceptions.TokenAssetNotFoundException;
import com.example.cryptowallet.exceptions.TokenPriceNotFoundException;
import com.example.cryptowallet.models.CoinCapAsset;
import com.example.cryptowallet.models.CoinCapAssetResponse;
import com.example.cryptowallet.models.HistoricalPriceResponseDto;
import com.example.cryptowallet.models.TokenPriceDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Locale.filter;

@Service
public class PricesApi {
    private final String SYMBOL_API = "https://rest.coincap.io/v3/price/bysymbol/";
    private final String ASSETS_API = "https://rest.coincap.io/v3/assets/";

    private static final Logger logger = LoggerFactory.getLogger(PricesApi.class);

    @Value("${coincap.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public double fetchTokenPrice(String symbol) {
        logger.info("Fetching latest price for " + symbol);

        String url = SYMBOL_API + symbol.toLowerCase();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);

        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);
        org.springframework.http.ResponseEntity<TokenPriceDto> response =
                restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, TokenPriceDto.class);

        if (response == null || response.getBody() == null || response.getBody().data().isEmpty()) {
            throw new TokenPriceNotFoundException(symbol);
        }

        return response.getBody().data().get(0);
    }

    public CoinCapAsset fetchAssetData(String symbol) {
        logger.info("Fetching token metadata for " + symbol);
        String url = UriComponentsBuilder
                .fromUriString(ASSETS_API)
                .queryParam("search", symbol)
                .toUriString();

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);


        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);
        org.springframework.http.ResponseEntity<CoinCapAssetResponse> response =
                restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, CoinCapAssetResponse.class);

        CoinCapAssetResponse coinCapAssetResponse = response.getBody();

        if (coinCapAssetResponse == null || coinCapAssetResponse.data() == null || coinCapAssetResponse.data().isEmpty()) {
            throw new TokenAssetNotFoundException(symbol);
        }


        Optional<CoinCapAsset> coinCapAsset =
                response.getBody()
                        .data()
                        .stream()
                        .filter(asset -> asset.symbol().equals(symbol))
                        .findAny();

        if (coinCapAsset.isEmpty()) {
            throw new TokenAssetNotFoundException(symbol);
        }

        return coinCapAsset.get();
    }

    public List<CoinCapAsset> fetchAssetsData(List<String> symbols) {
        logger.info("Fetching token metadata for " + symbols);
        String url = UriComponentsBuilder
                .fromUriString(ASSETS_API)
                .queryParam("search", symbols)
                .toUriString();

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);


        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);
        org.springframework.http.ResponseEntity<CoinCapAssetResponse> response =
                restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, CoinCapAssetResponse.class);

        CoinCapAssetResponse coinCapAssetResponse = response.getBody();

        if (coinCapAssetResponse == null || coinCapAssetResponse.data() == null || coinCapAssetResponse.data().isEmpty()) {
            throw new TokenAssetNotFoundException(symbols.toString());
        }

        HashSet<String> symbolsSet = new HashSet<>(symbols);

        List<CoinCapAsset> coinCapAsset =
                response.getBody()
                        .data()
                        .stream()
                        .filter(asset -> symbolsSet.contains(asset.symbol()))
                        .toList();

        if (coinCapAsset.isEmpty()) {
            throw new TokenAssetNotFoundException(symbols.toString());
        }

        return coinCapAsset;
    }

    public double fetchHistoricalPrice(String slug, String dateStr) {
            LocalDate date = dateStr != null ? LocalDate.parse(dateStr) : LocalDate.now();
            long start = date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
            long end = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli() - 1;

            String url = ASSETS_API + slug +
                    "/history?interval=d1&start=" + start + "&end=" + end;

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            org.springframework.http.ResponseEntity<HistoricalPriceResponseDto> response =
                    restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, HistoricalPriceResponseDto.class);

            HistoricalPriceResponseDto historicalPriceResponseDto = response.getBody();

            if (historicalPriceResponseDto == null || historicalPriceResponseDto.data() == null || historicalPriceResponseDto.data().isEmpty()) {
                throw new TokenAssetNotFoundException(slug);
            }

            return Double.parseDouble(historicalPriceResponseDto.data().getFirst().priceUsd());
    }
}
