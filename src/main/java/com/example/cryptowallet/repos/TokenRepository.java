package com.example.cryptowallet.repos;

import com.example.cryptowallet.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, String> {
}
