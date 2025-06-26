package com.team17.poc.products.service;

import com.team17.poc.auth.entity.Member;
import com.team17.poc.products.dto.*;
import com.team17.poc.products.entity.ProductType;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    void createProduct(ProductRequestDto dto, List<MultipartFile> images, Member member);


    List<ProductListResponseDto> getAllProducts(ProductType type, Member member);

    ProductDetailResponseDto getProductDetail(Long productId, Member member);
    boolean toggleFavorite(Long productId, Member member);


    List<ProductListResponseDto> getFavoriteProducts(Member member);

    void updateProduct(Long id, @Valid ProductUpdateDto request, Member member);

    void deleteProduct(Long id, Member member);

    List<ProductListResponseDto> getMySellList(Member member);
}
