package com.example.campains.seller;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "emerald_accounts")
public class EmeraldAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "funds")
    private BigDecimal funds;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "seller_id"
    )
    private SellerEntity seller;

    public BigDecimal getFunds() {
        return funds;
    }

    public void setFunds(BigDecimal funds) {
        this.funds = funds;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SellerEntity getSeller() {
        return seller;
    }

    public void setSeller(SellerEntity seller) {
        this.seller = seller;
    }
}
