package com.example.campains.seller;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellersRepository extends JpaRepository<SellerEntity,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Optional<SellerEntity> findByName(String name);

}
