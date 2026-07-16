package com.example.campains.seller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmeraldAccountsRepository extends JpaRepository<EmeraldAccountEntity,Long> {
}
