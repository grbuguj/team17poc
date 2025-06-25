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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    public void createProduct(ProductRequestDto dto, List<MultipartFile> images, Member member) {
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

        // 이미지가 있다면 최대 5개까지 저장
        if (images != null && !images.isEmpty()) {
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            for (int i = 0; i < images.size(); i++) {
                MultipartFile image = images.get(i);
                if (image.isEmpty()) continue;

                try {
                    String storedName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                    File saveFile = new File(uploadPath, storedName);
                    image.transferTo(saveFile);

                    ProductImage productImage = ProductImage.builder()
                            .product(product)
                            .originalName(image.getOriginalFilename())
                            .storedName(storedName)
                            .imageOrder(i) // 다중 이미지 순서 반영
                            .build();

                    productImageRepository.save(productImage);
                } catch (IOException e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장 실패", e);
                }
            }
        }
    }



    @Override
    public List<ProductListResponseDto> getAllProducts(ProductType type, Member member) {
        List<Product> products = (type == null)
                ? productRepository.findAllWithMember()
                : productRepository.findByTypeWithMember(type);

        // ✅ 로그인한 사용자의 즐겨찾기 상품 ID 조회
        Set<Long> favoriteIds = (member != null)
                ? favoriteRepository.findByMember(member).stream()
                .map(fav -> fav.getProduct().getId())
                .collect(Collectors.toSet())
                : Set.of();

        return products.stream()
                .map(product -> {
                    ProductImage thumbnail = productImageRepository
                            .findFirstByProductOrderByImageOrderAsc(product);

                    String thumbnailUrl = (thumbnail != null)
                            ? "/images/" + thumbnail.getStoredName()
                            : "/images/default.jpg";

                    boolean isFavorited = favoriteIds.contains(product.getId());

                    return ProductListResponseDto.fromEntity(product, thumbnailUrl, isFavorited);
                })
                .toList();
    }



    @Override
    public ProductDetailResponseDto getProductDetail(Long productId, Member member) {
        log.info("⭐ getProductDetail 호출 - member: {}", member != null ? member.getId() : "null");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "상품 없음"));

        boolean isFavorite = member != null && favoriteRepository.existsByProduct_IdAndMember_Id(productId, member.getId());
        log.info("➡️ isFavorite 결과: {}", isFavorite);

        List<ProductImage> imageEntities = productImageRepository.findByProductOrderByImageOrderAsc(product);

        return ProductDetailResponseDto.fromEntity(product, isFavorite, imageEntities);
    }




    @Override
    @Transactional
    public boolean toggleFavorite(Long productId, Member member) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "상품 없음"));

        boolean alreadyFavorite = favoriteRepository.existsByProduct_IdAndMember_Id(productId, member.getId());

        if (alreadyFavorite) {
            favoriteRepository.deleteByProductIdAndMemberId(productId, member.getId());
            return false;
        } else {
            favoriteRepository.save(new Favorite(product, member));
            return true;
        }
    }

    @Override
    public List<ProductListResponseDto> getFavoriteProducts(Member member) {
        // 즐겨찾기된 상품 ID 목록 조회
        Set<Long> favoriteIds = favoriteRepository.findByMember(member).stream()
                .map(fav -> fav.getProduct().getId())
                .collect(Collectors.toSet());

        // 즐겨찾기된 상품 엔티티 목록 조회 (동시에 멤버도 eager fetch)
        List<Product> products = productRepository.findAllById(favoriteIds);

        return products.stream()
                .map(product -> {
                    ProductImage thumbnail = productImageRepository
                            .findFirstByProductOrderByImageOrderAsc(product);

                    String thumbnailUrl = (thumbnail != null)
                            ? "/images/" + thumbnail.getStoredName()
                            : "/images/default.jpg";

                    return ProductListResponseDto.fromEntity(product, thumbnailUrl, true); // ✅ 항상 즐겨찾기 true
                })
                .toList();
    }




    @Override
    public void updateProduct(Long id, ProductUpdateDto request, Member member) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."));

        // 작성자 확인
        if (!product.getMember().getId().equals(member.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성자만 수정할 수 있습니다.");
        }

        // 내용 업데이트
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setSalePrice(request.getSalePrice());
        product.setQuantity(request.getQuantity());
        product.setLocation(request.getLocation());
        product.setOpenChatUrl(request.getOpenChatUrl());
        product.setType(request.getType());

        productRepository.save(product);
    }


    @Override
    @Transactional
    public void deleteProduct(Long id, Member member) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."));

        // 작성자 확인
        if (!product.getMember().getId().equals(member.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성자만 삭제할 수 있습니다.");
        }

        // 관련 찜(Favorite) 삭제 (옵션)
        favoriteRepository.deleteAll(favoriteRepository.findByMember(member).stream()
                .filter(fav -> fav.getProduct().getId().equals(id))
                .toList());

        // 이미지 삭제 (옵션, cascade 처리 여부에 따라 다름)
        List<ProductImage> images = productImageRepository.findByProductOrderByImageOrderAsc(product);
        productImageRepository.deleteAll(images);

        // 상품 삭제
        productRepository.delete(product);
    }



}
