package com.team17.poc.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team17.poc.products.entity.Product;
import com.team17.poc.products.entity.ProductType;
import com.team17.poc.util.TimeUtils;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductListResponseDto {
    private Long id;
    private String title;
    private int originalPrice;
    private int salePrice;
    private ProductType type;
    private String location;
    private String thumbnail;
    private String timeAgo;
    private String sellerName;

    @JsonProperty("favorited")
    private boolean favorited; // ✅ 즐겨찾기 여부 추가

    @Builder
    public ProductListResponseDto(Long id, String title, int originalPrice, int salePrice, ProductType type,
                                  String location, String thumbnail, String timeAgo,
                                  String sellerName, boolean favorited) {
        this.id = id;
        this.title = title;
        this.originalPrice = originalPrice;
        this.salePrice = salePrice;
        this.type = type;
        this.location = location;
        this.thumbnail = thumbnail;
        this.timeAgo = timeAgo;
        this.sellerName = sellerName;
        this.favorited = favorited;
    }


    // 기본: 즐겨찾기 여부 false
    public static ProductListResponseDto fromEntity(Product product, String thumbnailUrl) {
        return fromEntity(product, thumbnailUrl, false);
    }

    // 오버로드: 즐겨찾기 여부 명시
    public static ProductListResponseDto fromEntity(Product product, String thumbnailUrl, boolean isFavorite) {
        return ProductListResponseDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .originalPrice(product.getOriginalPrice()) // ✅ 원가 포함
                .salePrice(product.getSalePrice())
                .type(product.getType())
                .location(product.getLocation())
                .thumbnail(thumbnailUrl)
                .timeAgo(TimeUtils.toTimeAgo(product.getCreatedAt()))
                .sellerName(product.getMember().getName())
                .favorited(isFavorite)
                .build();
    }

}
