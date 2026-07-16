package com.example.campains.seller;


import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/sellers")
@Validated
public class SellerController {
    private final SellerService sellerService;
    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }
    @GetMapping("{seller_name}")
    public SellerDto getSeller(@PathVariable(name="seller_name") @NotBlank @Length(max = 64) String sellerName) {
        return sellerService.getSellerInfo(sellerName);
    }
}
