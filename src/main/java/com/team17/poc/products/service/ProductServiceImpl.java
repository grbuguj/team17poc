package com.team17.poc.products.service;

import com.team17.poc.auth.entity.Member;
import com.team17.poc.products.dto.*;
import com.team17.poc.products.entity.Favorite;
import com.team17.poc.products.entity.Product;
import com.team17.poc.products.entity.ProductImage;
import com.team17.poc.products.entity.ProductType;
import com.team17.poc.products.repository.FavoriteRepository;
import com.team17.poc.products.repository.ProductImageRepository;
import com.team17.poc.products.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final FavoriteRepository favoriteRepository;

    @Value("${product.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public void createProduct(ProductRequestDto dto, MultipartFile image, Member member) {
        Product product = Product.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .originalPrice(dto.getOriginalPrice())
                .salePrice(dto.getSalePrice())
                .quantity(dto.getQuantity())
                .type(dto.getType())
                .location(dto.getLocation())
                .openChatUrl(dto.getOpenChatUrl())
                .member(member)
                .build();

        productRepository.save(product);

        if (image != null && !image.isEmpty()) {
            try {
                String storedName = UUID.randomUUID() + "_" + image.getOriginalFilename();

                // 경로 생성
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) {
                    uploadPath.mkdirs();
                }

                File saveFile = new File(uploadPath, storedName);
                image.transferTo(saveFile);

                ProductImage productImage = ProductImage.builder()
                        .product(product)
                        .originalName(image.getOriginalFilename())
                        .storedName(storedName)
                        .imageOrder(0)
                        .build();

                productImageRepository.save(productImage);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장 실패", e);
            }
        }
    }

    @Override
    public List<ProductListResponseDto> getAllProducts(ProductType type) {
        List<Product> products = (type == null)
                ? productRepository.findAllWithMember()
                : productRepository.findByTypeWithMember(type);

        return products.stream()
                .map(ProductListResponseDto::fromEntity)
                .toList();
    }

    @Override
    public ProductDetailResponseDto getProductDetail(Long productId, Member member) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "상품 없음"));

        boolean isFavorite = member != null && favoriteRepository.existsByProductIdAndMemberId(productId, member.getId());

        return ProductDetailResponseDto.fromEntity(product, isFavorite);
    }

    @Override
    @Transactional
    public boolean toggleFavorite(Long productId, Member member) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "상품 없음"));

        boolean alreadyFavorite = favoriteRepository.existsByProductIdAndMemberId(productId, member.getId());

        if (alreadyFavorite) {
            favoriteRepository.deleteByProductIdAndMemberId(productId, member.getId());
            return false;
        } else {
            favoriteRepository.save(new Favorite(product, member));
            return true;
        }
    }

    @Override
    public List<ProductListResponseDto> getMyFavoriteProducts(Member member) {
        return favoriteRepository.findByMember_Id(member.getId()).stream()
                .map(Favorite::getProduct)
                .map(ProductListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
