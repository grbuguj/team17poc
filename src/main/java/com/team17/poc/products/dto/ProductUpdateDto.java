package com.team17.poc.products.dto;

import com.team17.poc.products.entity.ProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductUpdateDto {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private Integer originalPrice;

    @NotNull
    private Integer salePrice;

    @NotNull
    private Integer quantity;

    @NotNull
    private ProductType type; // CAFE / DIRECT

    @NotBlank
    private String location;

    @NotBlank
    private String openChatUrl;

    @Size(min = 1, max = 5)
    private List<MultipartFile> images;
}
