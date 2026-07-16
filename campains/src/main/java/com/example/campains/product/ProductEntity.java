package com.example.campains.product;
import com.example.campains.seller.SellerEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "seller_id"
    )
    private SellerEntity seller;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SellerEntity getSeller() {
        return seller;
    }

    public void setSeller(SellerEntity seller) {
        this.seller = seller;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
