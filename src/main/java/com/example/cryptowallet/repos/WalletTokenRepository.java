package com.example.cryptowallet.repos;

import com.example.cryptowallet.entities.WalletToken;
import com.example.cryptowallet.entities.WalletTokenId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTokenRepository extends JpaRepository<WalletToken, WalletTokenId> {
}
