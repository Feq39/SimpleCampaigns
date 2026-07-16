package com.example.campains.campaign;

import com.example.campains.campaign.dtos.CampaignDto;
import com.example.campains.common.ResourceNotFoundException;
import com.example.campains.keyword.KeywordDto;
import com.example.campains.product.ProductDto;
import com.example.campains.product.ProductEntity;
import com.example.campains.product.ProductRepository;
import com.example.campains.seller.SellerEntity;
import com.example.campains.seller.SellersRepository;
import com.example.campains.town.TownDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ProductRepository productRepository;
    private final SellersRepository sellersRepository;
    public CampaignService(CampaignRepository campaignRepository, ProductRepository productRepository,SellersRepository sellersRepository) {
        this.campaignRepository = campaignRepository;
        this.productRepository = productRepository;
        this.sellersRepository = sellersRepository;
    }

    public CampaignDto getCampaign(String campaignName,String sellerName,String productName) {
        SellerEntity seller = getSellerEntityOrThrow(sellerName);
        ProductEntity product = getProductEntityOrThrow(productName,seller);
        CampaignEntity campaign = getCampaignEntityOrThrow(campaignName,product);
        return CampaignService.convertCampaignEntityToDto(campaign);
    }

    private SellerEntity getSellerEntityOrThrow(String sellerName) {
        Optional<SellerEntity> sellerOpt = sellersRepository.findByName(sellerName);
        if(sellerOpt.isEmpty()) {
            throw new ResourceNotFoundException("Seller with the name " + sellerName + " does not exist");
        }
        return sellerOpt.get();
    }


    private ProductEntity getProductEntityOrThrow(String productName,SellerEntity seller) {
        Optional<ProductEntity> productOpt = productRepository.findByNameAndSeller(productName,seller);
        if(productOpt.isEmpty()) {
            throw new ResourceNotFoundException("Product with the name " + productName + " and seller " + seller.getName() + " does not exist");
        }
        return productOpt.get();
    }

    private CampaignEntity getCampaignEntityOrThrow(String campaignName, ProductEntity product) {
        Optional<CampaignEntity> campaignOpt = campaignRepository.findByNameAndProduct(campaignName,product);
        if(campaignOpt.isEmpty()) {
            throw new ResourceNotFoundException("Campaign with name: " + campaignName + " and product " + product.toString() + " does not exist");
        }
        return campaignOpt.get();
    }

    public static CampaignDto convertCampaignEntityToDto(CampaignEntity campaign) {
        return new CampaignDto(
                new ProductDto(campaign.getProduct().getName()),
                campaign.getName(),
                campaign.getKeywords().stream().map(k -> new KeywordDto(k.getValue())).toList(),
                campaign.getBidAmount(),
                campaign.getFund(),
                campaign.getStatus(),
                new TownDto(campaign.getTown().getName()),
                campaign.getRadiusKm()
        );
    }

}
