package com.team17.poc.products.service;

import com.team17.poc.auth.entity.Member;
import com.team17.poc.products.dto.*;
import com.team17.poc.products.entity.ProductType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    void createProduct(ProductRequestDto dto, MultipartFile image, Member member);

    List<ProductListResponseDto> getAllProducts(ProductType type);
    ProductDetailResponseDto getProductDetail(Long productId, Member member);
    boolean toggleFavorite(Long productId, Member member);
    List<ProductListResponseDto> getMyFavoriteProducts(Member member);
}
