package com.example.campains.campaign;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class CampaignController {
    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }


}
