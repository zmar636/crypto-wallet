package com.example.cryptowallet.repos;

import com.example.cryptowallet.entities.User;
import com.example.cryptowallet.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUser(User user);
}
