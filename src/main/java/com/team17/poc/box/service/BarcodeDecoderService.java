// 제품 등록 -> 사진 촬영에서 1. 바코드 인식 기능.
package com.team17.poc.box.service;

import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class BarcodeDecoderService {


    /*
    public String decodeBarcode(MultipartFile file) throws IOException, NotFoundException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Result result = new MultiFormatReader().decode(bitmap);
        return result.getText(); // 예: "8801234567890"
    }

     */

    public String decodeBarcode(MultipartFile file) {
        System.out.println("📦 바코드 디코딩 시작");
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                System.out.println("❌ 이미지 디코딩 실패 (null)");
                throw new RuntimeException("이미지 파일을 읽을 수 없습니다.");
            }

            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = new MultiFormatReader().decode(bitmap);
            System.out.println("✅ 바코드 인식 성공: " + result.getText());
            return result.getText();

        } catch (Exception e) {
            System.out.println("❌ 바코드 인식 실패: " + e.getClass().getSimpleName());
            e.printStackTrace(); // 반드시 출력
            throw new RuntimeException("바코드 인식 실패");
        }
    }


}
