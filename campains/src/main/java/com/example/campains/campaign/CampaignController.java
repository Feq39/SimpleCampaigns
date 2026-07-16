package com.example.campains.campaign;

import com.example.campains.campaign.dtos.CampaignDto;
import com.example.campains.campaign.dtos.UpdateCampaignRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/campaigns")
public class CampaignController {
    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }
    @GetMapping("/{seller_name}/{product_name}/{campaign_name}")
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
    @PostMapping("/{sellerName}/{productName}")
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignDto createCampaign(
            @PathVariable @NotBlank @Length(max = 64)String sellerName,
            @PathVariable @NotBlank @Length(max = 64)String productName,
            @Valid @RequestBody UpdateCampaignRequest request
    ) {
        return campaignService.createCampaign(sellerName,productName,request);
    }
    @DeleteMapping("/{sellerName}/{productName}/{campaignName}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCampaign(
            @PathVariable String sellerName,
            @PathVariable String productName,
            @PathVariable String campaignName
    ) {
        campaignService.deleteCampaign(
                sellerName,
                productName,
                campaignName
        );
    }
    @PutMapping("/{sellerName}/{productName}/{campaignName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCampaign(
            @PathVariable @NotBlank @Length(max = 64) String sellerName,
            @PathVariable @NotBlank @Length(max = 64) String productName,
            @PathVariable @NotBlank @Length(max = 64) String campaignName,
            @Valid @RequestBody UpdateCampaignRequest request
    ) {
        campaignService.updateCampaign(
                sellerName,
                productName,
                campaignName,
                request
        );
    }
}
