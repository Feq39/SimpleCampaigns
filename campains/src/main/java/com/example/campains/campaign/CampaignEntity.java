package com.example.campains.campaign;

import com.example.campains.keyword.KeywordEntity;
import com.example.campains.product.ProductEntity;
import com.example.campains.town.TownEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="campains")
public class CampaignEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "bid_amount")
    private BigDecimal bidAmount;
    @Column(name = "fund")
    private BigDecimal fund;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CampaignStatus status;
    @ManyToOne()
    @JoinColumn(
            name = "town_id"
    )
    private TownEntity town;
    @Column(name = "radius_km")
    private Integer radiusKm;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id"
    )
    private ProductEntity product;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "campaign_keyword",
            joinColumns = @JoinColumn(
                    name = "campaign_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "keyword_id"
            )
    )
    private Set<KeywordEntity> keywords = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getFund() {
        return fund;
    }

    public void setFund(BigDecimal fund) {
        this.fund = fund;
    }

    public CampaignStatus getStatus() {
        return status;
    }

    public void setStatus(CampaignStatus status) {
        this.status = status;
    }

    public TownEntity getTown() {
        return town;
    }

    public void setTown(TownEntity town) {
        this.town = town;
    }

    public Integer getRadiusKm() {
        return radiusKm;
    }

    public void setRadiusKm(Integer radiusKm) {
        this.radiusKm = radiusKm;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public Set<KeywordEntity> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<KeywordEntity> keywords) {
        this.keywords = keywords;
    }
}
