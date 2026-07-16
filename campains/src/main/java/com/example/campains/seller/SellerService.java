package com.example.campains.seller;

import com.example.campains.common.ResourceNotFoundException;
import com.example.campains.product.ProductDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SellerService {
    private final SellersRepository sellersRepository;
    private final EmeraldAccountsRepository emeraldAccountsRepository;

    public SellerService(SellersRepository sellersRepository,EmeraldAccountsRepository emeraldAccountsRepository) {
        this.sellersRepository = sellersRepository;
        this.emeraldAccountsRepository = emeraldAccountsRepository;
    }
    @Transactional(readOnly = true)
    public SellerDto getSellerInfo(String sellerName) {
        Optional<SellerEntity> sellerOpt = sellersRepository.findByName(sellerName);
        if (sellerOpt.isEmpty()) {
            throw new ResourceNotFoundException("Seller witn name " + sellerName + " does not exist");
        }
        SellerEntity seller = sellerOpt.get();
        return new SellerDto(
                seller.getName(),
                seller.getEmeraldAccount().getFunds(),
                seller.getProducts().stream().map(p -> new ProductDto(p.getName())).toList()
                );
    }

    public List<SellerDto> getAllSellers() {
        return sellersRepository.findAll().stream().map(s -> new SellerDto(
                s.getName(),
                s.getEmeraldAccount().getFunds(),
                s.getProducts().stream().map(p -> new ProductDto(s.getName())).toList()
                )).toList();
    }
}
