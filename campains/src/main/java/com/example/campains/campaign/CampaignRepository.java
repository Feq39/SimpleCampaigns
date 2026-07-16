package com.example.campains.campaign;

import com.example.campains.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<CampaignEntity,Long> {
    Optional<CampaignEntity> findByNameAndProduct(
            String name,
            ProductEntity product
    );

    boolean existsByNameAndProduct(
            String name,
            ProductEntity product
    );
}
