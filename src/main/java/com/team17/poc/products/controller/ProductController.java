package com.team17.poc.products.controller;

import com.team17.poc.auth.entity.Member;
import com.team17.poc.auth.model.CustomUserPrincipal;
import com.team17.poc.products.dto.*;
import com.team17.poc.products.entity.ProductType;
import com.team17.poc.products.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@CrossOrigin(
        origins = {
                "http://localhost:3000",
                "http://localhost:5173",
                "http://keepbara.duckdns.org",
                "http://keepbara.duckdns.org:8082",
                "http://localhost:8082",
                "https://keepbara.duckdns.org",
                "https://2025-unithon-team-17-fe.vercel.app"
        },
        allowCredentials = "true"
)

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ✅  상품 등록
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestPart("requestDto") ProductRequestDto requestDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal
    ) {
        if (userPrincipal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해 주세요.");
        }

        Member member = userPrincipal.getMember();
        productService.createProduct(requestDto, images, member);
        return ResponseEntity.ok("상품 등록 완료");
    }



    // ✅  상품 수정
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateProduct(
            @PathVariable Long id,
            @ModelAttribute @Valid ProductUpdateDto request,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal
    ) {
        Member member = userPrincipal.getMember();
        productService.updateProduct(id, request, member);
        return ResponseEntity.ok("상품 수정 완료");
    }

    // ✅  상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal
    ) {
        Member member = userPrincipal.getMember();
        productService.deleteProduct(id, member);
        return ResponseEntity.ok("상품 삭제 완료");
    }



    // ✅ 상품 목록 조회 (전체 or 타입별)
    @GetMapping
    public ResponseEntity<List<ProductListResponseDto>> getAllProducts(
            @RequestParam(required = false) ProductType type,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal  // ← 수정
    ) {
        Member member = (userPrincipal != null) ? userPrincipal.getMember() : null;
        List<ProductListResponseDto> list = productService.getAllProducts(type, member);
        return ResponseEntity.ok(list);
    }


    // ✅  상품 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponseDto> getProductDetail(@PathVariable Long id,
                                                                     @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        Member member = (userPrincipal != null) ? userPrincipal.getMember() : null;
        ProductDetailResponseDto detail = productService.getProductDetail(id, member);
        return ResponseEntity.ok(detail);
    }


    // ✅  즐겨찾기 토글
    @PostMapping("/{id}/favorite")
    public ResponseEntity<Boolean> toggleFavorite(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        Member member = userPrincipal.getMember();
        boolean result = productService.toggleFavorite(id, member);
        return ResponseEntity.ok(result);
    }

    // ✅ 5. 즐겨찾기 목록
    @GetMapping("/favorites")
    public ResponseEntity<List<ProductListResponseDto>> getFavorites(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        Member member = userPrincipal.getMember();
        List<ProductListResponseDto> favorites = productService.getFavoriteProducts(member);
        return ResponseEntity.ok(favorites);
    }
}
