package com.team17.poc.products.service;

import com.team17.poc.auth.entity.Member;
import com.team17.poc.auth.repository.MemberRepository;
import com.team17.poc.products.dto.*;
import com.team17.poc.products.entity.Favorite;
import com.team17.poc.products.entity.Product;
import com.team17.poc.products.entity.ProductImage;
import com.team17.poc.products.entity.ProductType;
import com.team17.poc.products.repository.FavoriteRepository;
import com.team17.poc.products.repository.ProductImageRepository;
import com.team17.poc.products.repository.ProductRepository;
import com.team17.poc.util.LocalFileUploader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final FavoriteRepository favoriteRepository;

    @Autowired
    private LocalFileUploader fileUploader;

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

        // 이미지 저장


        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = fileUploader.upload(image);  // → /images/uuid_filename.jpg
                String storedName = imageUrl.substring("/images/".length()); // uuid_filename.jpg

                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(imageUrl);
                productImage.setStoredName(storedName); // ⭐ 이걸 추가해야 storedName이 null 아님
                productImage.setImageOrder(0); // 순서 지정 필요 시 로직 수정
                productImage.setProduct(product);

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

        boolean isFavorite = false;
        if (member != null) {
            isFavorite = favoriteRepository.existsByProductIdAndMemberId(productId, member.getId());
        }

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
        List<Product> products = favoriteRepository.findByMember_Id(member.getId()).stream()
                .map(Favorite::getProduct)
                .toList();

        return products.stream()
                .map(ProductListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
