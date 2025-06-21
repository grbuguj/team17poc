package com.team17.poc.products.dto;

import com.team17.poc.products.entity.Product;
import com.team17.poc.products.entity.ProductType;
import com.team17.poc.util.TimeUtils;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductListResponseDto {
    private Long id;
    private String title;
    private int salePrice;
    private ProductType type;
    private String location;
    private String thumbnail;
    private String timeAgo;     // ✅ 추가
    private String sellerName;  // ✅ 추가

    @Builder
    public ProductListResponseDto(Long id, String title, int salePrice, ProductType type,
                                  String location, String thumbnail, String timeAgo, String sellerName) {
        this.id = id;
        this.title = title;
        this.salePrice = salePrice;
        this.type = type;
        this.location = location;
        this.thumbnail = thumbnail;
        this.timeAgo = timeAgo;
        this.sellerName = sellerName;
    }

    public static ProductListResponseDto fromEntity(Product product) {
        return ProductListResponseDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .salePrice(product.getSalePrice())
                .type(product.getType())
                .location(product.getLocation())
                .thumbnail(product.getThumbnailUrl())
                .timeAgo(TimeUtils.toTimeAgo(product.getCreatedAt()))    // ✅ 시간 가공
                .sellerName(product.getMember().getName())                // ✅ 판매자 이름
                .build();
    }
}
