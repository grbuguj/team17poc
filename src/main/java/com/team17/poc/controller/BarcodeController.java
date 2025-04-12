package com.team17.poc.controller;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@RestController

@RequestMapping("/api")
public class BarcodeController {
    @PostMapping("/scanBarcode")
    public String scanBarcode(@RequestParam("file") MultipartFile file) {
        try {
            // 이미지 파일을 BufferedImage로 변환
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            
            //Zxing 사용해 바코드 인코딩
            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            
            // 디코딩 설정
            Map<DecodeHintType, Object> hintMap = new HashMap<>();
            hintMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            
            // 바코드 디코더
            MultiFormatReader reader = new MultiFormatReader();
            String barcode = reader.decode(bitmap, hintMap).getText();

            // Open food facts api url
            String url =  "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";

            // api 호출
            ResponseEntity<String> response = new RestTemplate().getForEntity(url, String.class);
            return response.getBody();

        } catch (IOException | NotFoundException e) {
            e.printStackTrace();
            return "바코드 스캔 실패: " + e.getMessage();
        }
    }
}



