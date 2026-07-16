package com.example.campains.product;
import com.example.campains.campaign.CampaignEntity;
import com.example.campains.seller.SellerEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<CampaignEntity> campaigns = new ArrayList<>();
    public String getName() {
        return name;
    }

    public List<CampaignEntity> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(List<CampaignEntity> campaigns) {
        this.campaigns = campaigns;
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
