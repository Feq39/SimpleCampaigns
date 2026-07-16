package com.example.campains.keyword;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/keywords/v1")
@Validated
public class KeywordController {
    private final KeywordService keywordService;

    public KeywordController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }
    @GetMapping
    public List<KeywordDto> getKeywords() {
        return keywordService.getKeywords();
    }

    @GetMapping("/typeahead")
    public List<KeywordDto> getTypeaheadSuggestions(
            @RequestParam(name = "prefix")
            @NotBlank
            @Size(max = 255)
            String prefix
    ) {
        return keywordService.getTypeaheadSuggestions(prefix);
    }
}
