package com.team17.poc.products.controller;

import com.team17.poc.auth.entity.Member;
import com.team17.poc.auth.model.CustomUserPrincipal;
import com.team17.poc.products.dto.*;
import com.team17.poc.products.entity.ProductType;
import com.team17.poc.products.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ✅ 1. 상품 등록
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestPart("requestDto") ProductRequestDto requestDto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal
    ) {
        Member member = userPrincipal.getMember();  // 핵심
        productService.createProduct(requestDto, image, member);
        return ResponseEntity.ok("상품 등록 완료");
    }



    // ✅ 2. 상품 목록 조회 (전체 or 타입별)
    @GetMapping
    public ResponseEntity<List<ProductListResponseDto>> getAllProducts(
            @RequestParam(required = false) ProductType type) {
        List<ProductListResponseDto> list = productService.getAllProducts(type);
        return ResponseEntity.ok(list);
    }

    // ✅ 3. 상품 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponseDto> getProductDetail(@PathVariable Long id,
                                                                     @AuthenticationPrincipal Member member) {
        ProductDetailResponseDto detail = productService.getProductDetail(id, member);
        return ResponseEntity.ok(detail);
    }

    // ✅ 4. 즐겨찾기 토글
    @PostMapping("/{id}/favorite")
    public ResponseEntity<Boolean> toggleFavorite(@PathVariable Long id,
                                                  @AuthenticationPrincipal Member member) {
        boolean result = productService.toggleFavorite(id, member);
        return ResponseEntity.ok(result);
    }

    // ✅ 5. 즐겨찾기 목록
    @GetMapping("/favorites")
    public ResponseEntity<List<ProductListResponseDto>> getFavorites(@AuthenticationPrincipal Member member) {
        List<ProductListResponseDto> favorites = productService.getMyFavoriteProducts(member);
        return ResponseEntity.ok(favorites);
    }
}
