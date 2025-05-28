package com.team17.poc.box.service;

import com.team17.poc.barcode.dto.BarcodeInfo;
import com.team17.poc.box.repository.BarcodeJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BarcodeFindService {

    private final BarcodeJdbcRepository barcodeJdbcRepository;


    /*
    public Optional<BarcodeInfo> findByBarcode(String barcode) {
        return barcodeJdbcRepository.findByBarcode(barcode);
    }

     */


    public Optional<BarcodeInfo> findByBarcode(String barcode) {
        System.out.println("🔍 DB에서 바코드 조회: " + barcode);
        try {
            Optional<BarcodeInfo> result = barcodeJdbcRepository.findByBarcode(barcode);
            System.out.println("✅ DB 조회 결과: " + result);
            return result;
        } catch (Exception e) {
            System.out.println("❌ DB 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }


}
