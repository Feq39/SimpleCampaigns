package com.example.campains.seller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellersRepository extends JpaRepository<SellerEntity,Long> {
}
