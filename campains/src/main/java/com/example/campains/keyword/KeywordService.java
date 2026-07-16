package com.example.campains.keyword;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeywordService {
    private final KeywordRepository keywordRepository;
    public KeywordService(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    public List<KeywordDto> getKeywords() {
        return keywordRepository.findAll().stream().map(k -> new KeywordDto(k.getValue())).toList();
    }

}
