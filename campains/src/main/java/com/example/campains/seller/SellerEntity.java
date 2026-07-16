package com.example.campains.seller;

import com.example.campains.product.ProductEntity;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "sellers")
public class SellerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name")
    private String name;

    @OneToOne(mappedBy = "seller")
    private EmeraldAccountEntity emeraldAccount;

    @OneToMany(
            mappedBy = "seller",
            fetch = FetchType.LAZY
    )
    private List<ProductEntity> products;

    public SellerEntity() {
    }

    public SellerEntity(long id, String name, EmeraldAccountEntity emeraldAccount, List<ProductEntity> products) {
        this.id = id;
        this.name = name;
        this.emeraldAccount = emeraldAccount;
        this.products = products;
    }

    public SellerEntity(String name, EmeraldAccountEntity emeraldAccount, List<ProductEntity> products) {
        this.name = name;
        this.emeraldAccount = emeraldAccount;
        this.products = products;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EmeraldAccountEntity getEmeraldAccount() {
        return emeraldAccount;
    }

    public void setEmeraldAccount(EmeraldAccountEntity emeraldAccount) {
        this.emeraldAccount = emeraldAccount;
    }

    public List<ProductEntity> getProducts() {
        return products;
    }

    public void setProducts(List<ProductEntity> products) {
        this.products = products;
    }
}
