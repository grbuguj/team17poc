package com.team17.poc.box.controller;

import com.team17.poc.auth.entity.Member;
import com.team17.poc.barcode.dto.BarcodeInfo;
import com.team17.poc.box.dto.ExpireOcrResultDto;
import com.team17.poc.box.dto.ItemRequestDto;
import com.team17.poc.box.dto.LocationRequestDto;
import com.team17.poc.box.dto.TempScanResult;
import com.team17.poc.box.entity.Location;
import com.team17.poc.box.repository.ItemRepository;
import com.team17.poc.box.service.BarcodeDecoderService;
import com.team17.poc.box.service.BarcodeFindService;
import com.team17.poc.box.service.BoxService;
import com.team17.poc.ocr.service.OcrService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/api/box")
@RequiredArgsConstructor
public class BoxController {

    private final BoxService boxService;
    private final BarcodeDecoderService barcodeDecoderService;  // ✅ 추가
    private final BarcodeFindService barcodeFindService;

    private final OcrService ocrService; // 🔸 OCR 서비스 주입


    // 장소 목록 조회 (ex. memberId가 3인 사용자의 모든 장소 조회)
    @GetMapping("/locations")
    public List<Location> getLocations(HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 세션 없음");
        }

        return boxService.getLocations(memberId);
    }

    // 장소 등록
    @PostMapping("/locations")
    public Location addLocation(HttpSession session, @RequestBody LocationRequestDto dto) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 세션 없음");
        }

        return boxService.addLocation(memberId, dto);
    }

    // 장소 수정
    @PatchMapping("/locations/{locationId}")
    public Location updateLocation(@PathVariable("locationId") Long locationId, @RequestBody LocationRequestDto dto) {
        return boxService.updateLocation(locationId, dto);
    }


    private final ItemRepository itemRepository;
    // 장소 삭제
    @DeleteMapping("/locations/{locationId}")
    public void deleteLocation(@PathVariable("locationId") Long locationId) {
        itemRepository.deleteByLocationId(locationId);
        boxService.deleteLocation(locationId);
    }


    // 2. 제품 추가
    /*
    @PostMapping("/items")
    public void addItem(@RequestParam("memberId") Long memberId,
                        @RequestBody ItemRequestDto dto) {
        boxService.addItem(memberId, dto);
    }
     */
    @PostMapping("/items")
    @ResponseBody
    public void addItem(HttpSession session,
                        @RequestBody ItemRequestDto dto) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 세션 없음");
        }

        boxService.addItem(memberId, dto);
    }



    // 바코드 촬영

    /*
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

            String sessionId = UUID.randomUUID().toString();
            boxService.storeTempScan(sessionId, new TempScanResult(
                    여기 주석처리
                    barcode,
                    result.get().getName(),
                    result.get().getImage(),
                    LocalDate.now()
                     여기까지 주석처리


                    result.get().getImage(),       // ✅ imageUrl
                    barcode,                       // ✅ barcodeId
                    result.get().getName(),        // ✅ productName
                    LocalDate.now()                // ✅ capturedDate
            ));

            return ResponseEntity.ok(Map.of(
                    "productName", result.get().getName(),
                    "imageUrl", result.get().getImage(),
                    "sessionId", sessionId
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "바코드 인식 실패. 직접 입력하세요."));
        }
    }
     */

    @PostMapping("/items/shot-barcode")
    public ResponseEntity<Map<String, String>> readBarcode(
            @RequestParam("file") MultipartFile file,
            HttpSession session) {  // ✅ 세션 파라미터 추가

        System.out.println("✅ 컨트롤러 진입 성공");

        Long memberId = (Long) session.getAttribute("memberId");  // ✅ 세션에서 사용자 ID 꺼내기
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인 세션 없음"));
        }

        try {
            String barcode = barcodeDecoderService.decodeBarcode(file);
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


    @PostMapping("/items/shot-expire")
    public ResponseEntity<ExpireOcrResultDto> handleExpireScan(
            @RequestParam("imageFile") MultipartFile imageFile,
            /*@RequestParam("sessionId")*/
            HttpSession session) throws IOException {

        Long memberId = (Long) session.getAttribute("memberId");  // ✅ 세션에서 꺼냄
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Multifile -> File로 변환 과정 (아래)
        // File file = convertToFile(imageFile);
        /* 잠시 주석처리.
        File convFile = new File(imageFile.getOriginalFilename());
        imageFile.transferTo(convFile);
        */

        // ocrService.extractDate(convFile);

        String ext = imageFile.getOriginalFilename().replaceAll("^.*\\.(?=\\w+$)", ".");
        File convFile = File.createTempFile("upload_", ext);
        imageFile.transferTo(convFile);
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


    // 세션 id 추가 (촬영 연속으로 하는데, 그 데이터들을 조합해서 해야함.)
    /*
    @GetMapping("/items/session-id")
    public ResponseEntity<String> getSessionId(@RequestParam("barcode") String barcode) {
        Optional<String> sessionId = boxService.findSessionIdByBarcode(barcode);

        return sessionId
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("세션 없음"));
    }
     */

    @GetMapping("/items/member-id")
    public ResponseEntity<Long> getMemberIdByBarcode(@RequestParam("barcode") String barcode) {
        Optional<Long> memberId = boxService.findMemberIdByBarcode(barcode);  // ✅ 바뀐 메서드명

        return memberId
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }






}
