package com.example.campains.seller;

import com.example.campains.product.ProductDto;

import java.math.BigDecimal;
import java.util.List;

public record SellerDto(String name, BigDecimal accountBalance, List<ProductDto> products) {
}
