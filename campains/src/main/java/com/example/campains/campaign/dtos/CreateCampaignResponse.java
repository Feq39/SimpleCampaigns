package com.example.campains.campaign.dtos;

import java.math.BigDecimal;

public record CreateCampaignResponse(
        CampaignDto campaign,
        BigDecimal emeraldBalance
) {
}
