package com.example.campains.campaign;

import com.example.campains.campaign.dtos.CampaignDto;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/campaigns")
public class CampaignController {
    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }
    @GetMapping("/{seller_name}/{product_name}/{campaign_name")
    public CampaignDto getCampaignInfo(
            @PathVariable(name = "seller_name")
            @NotBlank
            @Length(max = 64)
            String sellerName,
            @PathVariable(name = "product_name")
            @NotBlank
            @Length(max = 64)
            String productName,
            @PathVariable(name = "campaign_name")
            @NotBlank
            @Length(max = 64)
            String campaignName
            ) {
        return campaignService.getCampaign(campaignName,sellerName,productName);
    }
}
