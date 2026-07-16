package com.example.campains.keyword;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/keywords/v1")
public class KeywordController {
    private final KeywordService keywordService;

    public KeywordController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }
    @GetMapping
    public List<KeywordDto> getKeywords() {
        return keywordService.getKeywords();
    }
}
