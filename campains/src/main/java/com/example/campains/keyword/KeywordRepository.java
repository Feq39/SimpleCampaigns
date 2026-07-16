package com.example.campains.keyword;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<KeywordEntity,Long> {
    Optional<KeywordEntity> findByValue(String value);
}
