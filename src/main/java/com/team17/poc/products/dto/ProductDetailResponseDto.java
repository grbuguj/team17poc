package com.team17.poc.products.dto;

import com.team17.poc.products.entity.Product;
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
    private boolean isFavorite;

    private ProductStatus status;
    private LocalDateTime createdAt;
    private String sellerName;
    private List<String> imageUrls;
    private String timeAgo;


    public static ProductDetailResponseDto fromEntity(Product product, boolean isFavorite) {
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
                .isFavorite(isFavorite)
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .timeAgo(TimeUtils.toTimeAgo(product.getCreatedAt()))
                .sellerName(product.getMember().getName()) // 또는 .getEmail() 등
                .imageUrls(product.getImages().stream()
                        .map(image -> "/images/" + image.getStoredName())
                        .toList())
                .build();
    }
}
