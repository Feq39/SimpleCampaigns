package com.example.campains.campaign.dtos;

import com.example.campains.campaign.CampaignStatus;
import com.example.campains.keyword.KeywordDto;
import com.example.campains.town.TownDto;

import java.math.BigDecimal;
import java.util.List;

public record CampaignDto(
        long id,
        long productId,
        String name,
        List<KeywordDto> keywords,
        BigDecimal bidAmount,
        BigDecimal fund,
        CampaignStatus status,
        TownDto town,
        Integer radiusKm
) {
}
