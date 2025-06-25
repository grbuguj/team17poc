package com.team17.poc.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team17.poc.products.entity.Product;
import com.team17.poc.products.entity.ProductImage;
import com.team17.poc.products.entity.ProductStatus;
import com.team17.poc.products.entity.ProductType;
import com.team17.poc.util.TimeUtils;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ProductDetailResponseDto {
    private Long id;
    private String title;
    private String description;
    private int originalPrice;
    private int salePrice;
    private int quantity;
    private ProductType type;
    private String location;
    private String openChatUrl;

    @JsonProperty("favorited")
    private boolean favorited;

    private ProductStatus status;
    private LocalDateTime createdAt;
    private String sellerName;
    private String timeAgo;

    private List<ProductImageResponseDto> images; // ✅ 이미지 정보 리스트 추가

    public static ProductDetailResponseDto fromEntity(Product product, boolean isFavorite, List<ProductImage> imageEntities) {
        return ProductDetailResponseDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .originalPrice(product.getOriginalPrice())
                .salePrice(product.getSalePrice())
                .quantity(product.getQuantity())
                .type(product.getType())
                .location(product.getLocation())
                .openChatUrl(product.getOpenChatUrl())
                .favorited(isFavorite)
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .timeAgo(TimeUtils.toTimeAgo(product.getCreatedAt()))
                .sellerName(product.getMember().getName())
                .images(imageEntities.stream()
                        .map(ProductImageResponseDto::fromEntity)
                        .toList())
                .build();
    }
}
