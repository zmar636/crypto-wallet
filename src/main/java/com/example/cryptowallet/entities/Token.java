package com.example.cryptowallet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @Getter
    @Setter
    private String symbol;

    @Column(nullable = false)
    @Setter
    @Getter
    private String price;

    @Column(nullable = false)
    @Setter
    @Getter
    private String slug;
}
