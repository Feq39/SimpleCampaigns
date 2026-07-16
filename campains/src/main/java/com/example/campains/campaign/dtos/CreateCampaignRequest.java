package com.example.campains.campaign.dtos;

import com.example.campains.campaign.CampaignStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Set;

public record CreateCampaignRequest(
        @NotBlank
        @Size(max = 255)
        String name,
        @NotNull
        Set<@NotBlank String> keywords,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal bidAmount,

        @NotNull
        @Positive
        BigDecimal fund,

        @NotNull
        CampaignStatus status,

        @Size(max = 255)
        @NotNull
        String town,

        @NotNull
        @Positive
        Integer radiusKm
) {}