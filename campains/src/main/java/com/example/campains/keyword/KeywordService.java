package com.example.campains.keyword;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KeywordService {
    private final KeywordRepository keywordRepository;
    public KeywordService(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }
    @Transactional(readOnly = true)
    public List<KeywordDto> getKeywords() {
        return keywordRepository.findAll().stream().map(k -> new KeywordDto(k.getValue())).toList();
    }

    @Transactional(readOnly = true)
    public List<KeywordDto> getTypeaheadSuggestions(String prefix) {
        return keywordRepository
                .findTop10ByValueStartingWithIgnoreCaseOrderByValueAsc(
                        prefix.trim()
                )
                .stream()
                .map(KeywordService::entityToDto)
                .toList();
    }

    public static KeywordDto entityToDto(KeywordEntity keyword) {
        return new KeywordDto(keyword.getValue());
    }

}
