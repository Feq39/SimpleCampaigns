package com.example.campains.keyword;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<KeywordEntity,Long> {
    List<KeywordEntity> findTop10ByValueContainingIgnoreCaseOrderByValue(String query);
}
