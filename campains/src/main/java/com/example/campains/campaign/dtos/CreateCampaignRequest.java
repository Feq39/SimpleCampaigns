package com.example.campains.campaign.dtos;

import com.example.campains.campaign.CampaignStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Set;

public record CreateCampaignRequest(
        @NotBlank
        String name,

        @NotEmpty
        Set<Long> keywordIds,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal bidAmount,

        @NotNull
        @Positive
        BigDecimal fund,

        @NotNull
        CampaignStatus status,

        Long townId,

        @NotNull
        @Positive
        Integer radiusKm
) {
}