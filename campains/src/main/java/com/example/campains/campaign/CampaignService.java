package com.example.campains.campaign;

import com.example.campains.campaign.dtos.CampaignDto;
import com.example.campains.campaign.dtos.UpdateCampaignRequest;
import com.example.campains.common.InsufficientFundsException;
import com.example.campains.common.ResourceAlreadyExistsException;
import com.example.campains.common.ResourceNotFoundException;
import com.example.campains.keyword.KeywordDto;
import com.example.campains.keyword.KeywordEntity;
import com.example.campains.keyword.KeywordRepository;
import com.example.campains.product.ProductDto;
import com.example.campains.product.ProductEntity;
import com.example.campains.product.ProductRepository;
import com.example.campains.seller.EmeraldAccountEntity;
import com.example.campains.seller.EmeraldAccountsRepository;
import com.example.campains.seller.SellerEntity;
import com.example.campains.seller.SellersRepository;
import com.example.campains.town.TownDto;
import com.example.campains.town.TownEntity;
import com.example.campains.town.TownRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.*;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ProductRepository productRepository;
    private final SellersRepository sellersRepository;
    private final TownRepository townRepository;
    private final KeywordRepository keywordRepository;
    private final EmeraldAccountsRepository emeraldAccountsRepository;
    public CampaignService(
            CampaignRepository campaignRepository,
            ProductRepository productRepository,
            SellersRepository sellersRepository,
            TownRepository townRepository,
            KeywordRepository keywordRepository,
            EmeraldAccountsRepository emeraldAccountsRepository
    ) {
        this.campaignRepository = campaignRepository;
        this.productRepository = productRepository;
        this.sellersRepository = sellersRepository;
        this.townRepository = townRepository;
        this.keywordRepository = keywordRepository;
        this.emeraldAccountsRepository = emeraldAccountsRepository;
    }
    @Transactional(readOnly = true)
    public CampaignDto getCampaign(String campaignName,String sellerName,String productName) {
        SellerEntity seller = getSellerEntityOrThrow(sellerName);
        ProductEntity product = getProductEntityOrThrow(productName,seller);
        CampaignEntity campaign = getCampaignEntityOrThrow(campaignName,product);
        return CampaignService.convertCampaignEntityToDto(campaign);
    }

    @Transactional
    public CampaignDto createCampaign(String sellerName, String productName, UpdateCampaignRequest campaignInfo) {
        SellerEntity seller = getSellerEntityOrThrow(sellerName);
        ProductEntity product = getProductEntityOrThrow(productName,seller);
        if (product.getCampaigns().stream().anyMatch(e->e.getName().equals(campaignInfo.name()))) {
            throw new ResourceAlreadyExistsException("Campaign with name " + campaignInfo.name() + " alreadi exists for product " + productName);
        }
        CampaignEntity newCampaign = new CampaignEntity();
        newCampaign.setName(campaignInfo.name());
        newCampaign.setStatus(campaignInfo.status());
        newCampaign.setRadiusKm(campaignInfo.radiusKm());
        newCampaign.setBidAmount(campaignInfo.bidAmount());
        newCampaign.setProduct(product);
        newCampaign.setTown(getTownEntityOrThrow(campaignInfo.town()));
        newCampaign.setKeywords(getMatchingKeywordEntitiesOrThrow(campaignInfo.keywords()));
        newCampaign.setFund(campaignInfo.fund());
        EmeraldAccountEntity sellersAccount = seller.getEmeraldAccount();
        if (sellersAccount.getFunds().compareTo(newCampaign.getFund()) < 0) {
            throw new InsufficientFundsException("Not enough funds to create campaign " + campaignInfo.name());
        }
        campaignRepository.save(newCampaign);

        sellersAccount.setFunds(sellersAccount.getFunds().subtract(newCampaign.getFund()));
        emeraldAccountsRepository.save(sellersAccount);
        return CampaignService.convertCampaignEntityToDto(newCampaign);
    }
    @Transactional
    public void deleteCampaign(String sellerName,String productName,String campaignName) {
        SellerEntity seller = getSellerEntityOrThrow(sellerName);
        ProductEntity product = getProductEntityOrThrow(productName,seller);
        CampaignEntity campaign = getCampaignEntityOrThrow(campaignName,product);
        EmeraldAccountEntity sellersAccount = seller.getEmeraldAccount();
        sellersAccount.setFunds(sellersAccount.getFunds().add(campaign.getFund()));
        emeraldAccountsRepository.save(sellersAccount);
        campaignRepository.delete(campaign);
    }
    private Set<KeywordEntity> getMatchingKeywordEntitiesOrThrow(Set<String> keywordNames) {
        Set<KeywordEntity> result = new HashSet<>();
        for(String k : keywordNames) {
            Optional<KeywordEntity> keywordOpt = keywordRepository.findByValue(k);
            if(keywordOpt.isEmpty()) {
                throw new ResourceNotFoundException("Keyword " + k + " does not exist");
            }
            result.add(keywordOpt.get());
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<String> getAllCampaignNamesForProduct(String sellerName,String productName) {
        SellerEntity seller = getSellerEntityOrThrow(sellerName);
        ProductEntity product = getProductEntityOrThrow(productName,seller);
        return campaignRepository.findAllByProduct(product).stream().map(CampaignEntity::getName).toList();
    }
    @Transactional
    public void updateCampaign(String sellerName,String productName,String campaignName,UpdateCampaignRequest campaignUpdateInfo) {
        SellerEntity seller = getSellerEntityOrThrow(sellerName);
        ProductEntity product = getProductEntityOrThrow(productName,seller);
        CampaignEntity campaign = getCampaignEntityOrThrow(campaignName,product);
        if( !campaignName.equals(campaignUpdateInfo.name() )&& campaignRepository.findByNameAndProduct(campaignUpdateInfo.name(),product).isPresent()) {
            throw new ResourceAlreadyExistsException("Name of the campain " + campaignUpdateInfo.name() +" is taken");
        }
        TownEntity town =
                getTownEntityOrThrow(campaignUpdateInfo.town());
        Set<KeywordEntity> keywords =
                getMatchingKeywordEntitiesOrThrow(
                        campaignUpdateInfo.keywords()
                );
        EmeraldAccountEntity account =
                seller.getEmeraldAccount();
        BigDecimal availableFunds =
                account.getFunds()
                        .add(campaign.getFund());

        if (availableFunds.compareTo(campaignUpdateInfo.fund()) < 0) {
            throw new InsufficientFundsException("not enough funds to add to campaign fund");
        }
        CampaignEntity newCampaign = new CampaignEntity();
        newCampaign.setName(campaignUpdateInfo.name());
        newCampaign.setStatus(campaignUpdateInfo.status());
        newCampaign.setRadiusKm(campaignUpdateInfo.radiusKm());
        newCampaign.setBidAmount(campaignUpdateInfo.bidAmount());
        newCampaign.setProduct(product);
        newCampaign.setTown(getTownEntityOrThrow(campaignUpdateInfo.town()));
        newCampaign.setKeywords(getMatchingKeywordEntitiesOrThrow(campaignUpdateInfo.keywords()));
        newCampaign.setFund(campaignUpdateInfo.fund());
        campaignRepository.delete(campaign);
        campaignRepository.flush();
        campaignRepository.save(newCampaign);

        account.setFunds(
                availableFunds.subtract(campaignUpdateInfo.fund())
        );
        emeraldAccountsRepository.save(account);

    }
    private TownEntity getTownEntityOrThrow(String townName) {
        Optional<TownEntity> townOpt =  townRepository.findByName(townName);
        if(townOpt.isEmpty()) {
            throw new ResourceNotFoundException("Town with name " + townName + " does not exist");
        }
        return townOpt.get();
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
