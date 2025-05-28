package com.team17.poc.box.controller;

import com.team17.poc.auth.entity.Member;
import com.team17.poc.barcode.dto.BarcodeInfo;
import com.team17.poc.box.dto.ItemRequestDto;
import com.team17.poc.box.dto.LocationRequestDto;
import com.team17.poc.box.entity.Location;
import com.team17.poc.box.service.BarcodeDecoderService;
import com.team17.poc.box.service.BarcodeFindService;
import com.team17.poc.box.service.BoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/box")
@RequiredArgsConstructor
public class BoxController {

    private final BoxService boxService;
    private final BarcodeDecoderService barcodeDecoderService;  // ✅ 추가
    private final BarcodeFindService barcodeFindService;

    // 장소 목록 조회
    @GetMapping
    public List<Location> getLocations(@RequestParam Long memberId) {
        return boxService.getLocations(memberId);
    }

    // 장소 등록
    @PostMapping
    public Location addLocation(@RequestParam Long memberId, @RequestBody LocationRequestDto dto) {
        return boxService.addLocation(memberId, dto);
    }

    // 장소 수정
    @PatchMapping("/{locationId}")
    public Location updateLocation(@PathVariable Long locationId, @RequestBody LocationRequestDto dto) {
        return boxService.updateLocation(locationId, dto);
    }

    // 장소 삭제
    @DeleteMapping("/{locationId}")
    public void deleteLocation(@PathVariable Long locationId) {
        boxService.deleteLocation(locationId);
    }


    // 2. 제품 추가
    @PostMapping("/items")
    public void addItem(@AuthenticationPrincipal Member member,
                        @RequestBody ItemRequestDto dto) {
        boxService.addItem(member, dto);
    }


    // 바코드 촬영

    @PostMapping("/items/shot-barcode")
    public ResponseEntity<Map<String, String>> readBarcode(@RequestParam("file") MultipartFile file) {
        System.out.println("✅ 컨트롤러 진입 성공"); // for test
        try {
            String barcode = barcodeDecoderService.decodeBarcode(file);
            Optional<BarcodeInfo> result = barcodeFindService.findByBarcode(barcode);

            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "등록되지 않은 바코드입니다."));
            }

            return ResponseEntity.ok(Map.of(
                    "productName", result.get().getName(),
                    "imageUrl", result.get().getImage()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "바코드 인식 실패"));
        }
    }


}
