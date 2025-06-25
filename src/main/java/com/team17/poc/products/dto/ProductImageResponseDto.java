package com.team17.poc.products.dto;

import com.team17.poc.products.entity.ProductImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductImageResponseDto {
    private String storedName;
    private String originalName;
    private int imageOrder;

    public static ProductImageResponseDto fromEntity(ProductImage image) {
        return ProductImageResponseDto.builder()
                .storedName(image.getStoredName())
                .originalName(image.getOriginalName())
                .imageOrder(image.getImageOrder())
                .build();
    }
}
