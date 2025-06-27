package com.team17.poc.box.controller;

import com.team17.poc.barcode.dto.BarcodeInfo;
import com.team17.poc.box.dto.*;
import com.team17.poc.box.entity.Location;
import com.team17.poc.box.repository.ItemRepository;
import com.team17.poc.box.service.BarcodeDecoderService;
import com.team17.poc.box.service.BarcodeFindService;
import com.team17.poc.box.service.BoxService;
import com.team17.poc.ocr.service.OcrService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.team17.poc.ocr.service.OcrService; // 🔸 OCR 의존성 주입을 위한 import
import org.springframework.web.server.ResponseStatusException;


import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

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
@RequestMapping("/api/box")
@RequiredArgsConstructor
public class BoxController {

    private final BoxService boxService;
    private final BarcodeDecoderService barcodeDecoderService;  // ✅ 추가
    private final BarcodeFindService barcodeFindService;

    private final OcrService ocrService; // 🔸 OCR 서비스 주입


    // 장소 목록 조회 (ex. memberId가 3인 사용자의 모든 장소 조회)

    /*
    @GetMapping("/locations")
    public List<Location> getLocations(HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            // throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 세션 없음");
            return boxService.getLocations(memberId);
        }
        return boxService.getLocations(memberId);
    }
     */
    @GetMapping("/locations-count")
    public List<LocationWithCountDto> getLocations(HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId != null) {
            return boxService.getLocationsWithItemCount(memberId);
        }

        return List.of(); // 또는 boxService.getPublicLocationsWithItemCount();
    }






    // 장소 등록
/*
    @PostMapping(value = "/locations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Location addLocation(
            HttpSession session,
            @RequestPart("dto") LocationRequestDto dto
    ) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 세션 없음");
        }

        return boxService.addLocation(memberId, dto);
    }
 */
// 장소 등록 새롭게 추가 (이미지 추가 관련)
    @PostMapping(value = "/locations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Location addLocation(
            HttpSession session,
            @ModelAttribute LocationRequestDto dto
    ) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 세션 없음");
        }

        return boxService.addLocation(memberId, dto);
    }




    // 장소 수정 (기존. 이미지 넣기 전)
    /*
    @PatchMapping("/locations/{locationId}")
    public Location updateLocation(@PathVariable("locationId") Long locationId, @RequestBody LocationRequestDto dto) {
        return boxService.updateLocation(locationId, dto);
    }
     */

    // 장소 수정 (new. 이미지 넣고)
    @PatchMapping(value = "/locations/{locationId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Location updateLocation(
            @PathVariable("locationId") Long locationId,
            @ModelAttribute LocationRequestDto dto
    ) {
        return boxService.updateLocation(locationId, dto);
    }



    private final ItemRepository itemRepository;

    // 장소 삭제 (수정중.)
    @DeleteMapping("/locations/{locationId}")
    public ResponseEntity<Map<String, String>> deleteLocation(@PathVariable("locationId") Long locationId) {
        //itemRepository.deleteByLocationId(locationId);
        boxService.deleteLocation(locationId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "장소가 성공적으로 삭제되었습니다.");

        return ResponseEntity.ok(response);
    }


    // 2. 제품 추가
    @PostMapping("/items")
    @ResponseBody
    public void addItem(HttpSession session,
                        @RequestBody ItemRequestDto dto) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 세션 없음");
        }

        if (!memberId.equals(dto.getMemberId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "memberId 위조 시도 감지");
        }

        boxService.addItem(memberId, dto);
    }

    // 전체 제품 조회 및 정렬 기능
    @GetMapping("/items")
    @ResponseBody
    public List<BoxResponseDto> getItemsSorted(@RequestParam(name = "sortBy", required = false, defaultValue = "expireDate") String sortBy,
                                               HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 세션 없음");
        }

        return boxService.getSortedItemsByMemberId(memberId, sortBy); // ✅ 이걸로 호출해야 함
    }


    // 상세 제품 조회
    @GetMapping("/items/{itemId}")
    @ResponseBody
    public BoxResponseDto getItemById(@PathVariable("itemId") Long itemId, HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 세션 없음");
        }

        return boxService.getItemByIdAndMember(itemId, memberId);
    }





    // 바코드 촬영
    @PostMapping("/items/shot-barcode")
    public ResponseEntity<Map<String, String>> readBarcode(
            @RequestParam("image") MultipartFile image,
            HttpSession session) {  // ✅ 세션 파라미터 추가

        System.out.println("✅ 컨트롤러 진입 성공");

        Long memberId = (Long) session.getAttribute("memberId");  // ✅ 세션에서 사용자 ID 꺼내기
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인 세션 없음"));
        }

        try {
            String barcode = barcodeDecoderService.decodeBarcode(image);
            Optional<BarcodeInfo> result = barcodeFindService.findByBarcode(barcode);

            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "등록되지 않은 바코드입니다."));
            }

            // ✅ 더 이상 랜덤 세션 ID 만들지 않고, 진짜 사용자 ID로 저장
            boxService.storeTempScan(memberId, new TempScanResult(
                    result.get().getImage(),       // imageUrl
                    barcode,                       // barcodeId
                    result.get().getName(),        // productName
                    LocalDate.now()                // capturedDate
            ));

            return ResponseEntity.ok(Map.of(
                    "productName", result.get().getName(),
                    "imageUrl", result.get().getImage()
                    // ❌ sessionId 제거
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "바코드 인식 실패. 직접 입력하세요."));
        }
    }



    // new..
    private File convertToFile(MultipartFile multipartFile) {
        try {
            File convFile = File.createTempFile("upload-", multipartFile.getOriginalFilename());
            multipartFile.transferTo(convFile);
            return convFile;
        } catch (IOException e) {
            throw new RuntimeException("파일 변환 실패", e);
        }
    }

    // 유통기한 촬영
    @PostMapping("/items/shot-expire")
    public ResponseEntity<ExpireOcrResultDto> handleExpireScan(
            @RequestParam("image") MultipartFile image,
            /*@RequestParam("sessionId")*/
            HttpSession session) throws IOException {

        Long memberId = (Long) session.getAttribute("memberId");  // ✅ 세션에서 꺼냄
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String ext = image.getOriginalFilename().replaceAll("^.*\\.(?=\\w+$)", ".");
        File convFile = File.createTempFile("upload_", ext);
        image.transferTo(convFile);
        convFile.deleteOnExit();



        TempScanResult result = boxService.getTempScan(memberId);
        String ocrText = ocrService.extractText(convFile).getRawText();
        String expireDate = ocrService.extractDate(convFile).getExtractedDate();

        ExpireOcrResultDto dto = new ExpireOcrResultDto(
                result.getProductName(),
                result.getImageUrl(),
                result.getCaptureDate(),
                expireDate
        );

        return ResponseEntity.ok(dto);
    }



    // 상품 수정
    @PatchMapping("/items/{itemId}")
    public ResponseEntity<Void> updateItem(@PathVariable("itemId") Long itemId,
                                           @RequestBody ItemRequestDto requestDto,
                                           HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");  // 세션에서 memberId 추출
        boxService.updateItem(itemId, requestDto, memberId);
        return ResponseEntity.ok().build();
    }


    // 상품 삭제

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable("itemId") Long itemId) {
        boxService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

    /* 알람 관련 부분 */


    /**
     * 알림 상품 조회 - 기준일 이내 유통기한이 도래하는 상품들
     * /api/box/notify?standardDays=14
     */

    // 알림 상품 조회
    @GetMapping("/notify")
    public ResponseEntity<List<NotifyItemDto>> getNotifiedItems(
            @RequestParam(name = "standardDays", required = false) Integer standardDays,
            HttpSession session
    ) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 세션 없음");
        }

        List<NotifyItemDto> items = boxService.getItemsExpiringSoon(memberId, standardDays);
        return ResponseEntity.ok(items);
    }


    /**
     * 알림 기준일 설정
     * /api/box/notify-date-change
     */
    @PatchMapping("/notify-date-change")
    public ResponseEntity<NotifyStandardResponseDto> updateNotifyStandard(
            @RequestBody NotifyStandardRequestDto requestDto,
            HttpSession session
    ) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인을 해주세요!");
        }

        int updatedDays = requestDto.getStandardDays();
        boxService.setNotifyStandard(memberId, updatedDays);

        NotifyStandardResponseDto response = new NotifyStandardResponseDto(
                "기준일 변경이 완료되었습니다.",
                updatedDays
        );

        return ResponseEntity.ok(response);
    }

}
