package com.team17.poc.barcode.controller;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.team17.poc.barcode.dto.BarcodeInfo;
import com.team17.poc.barcode.repository.BarcodeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller  // ← RestController → Controller로 변경
@RequestMapping
public class BarcodeController {

    private final BarcodeRepository barcodeRepository = new BarcodeRepository();

    @PostMapping("/scanBarcode")
    public String scanBarcode(@RequestParam("file") MultipartFile file, org.springframework.ui.Model model) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

            MultiFormatReader reader = new MultiFormatReader();
            String barcode = reader.decode(bitmap, hints).getText();

            BarcodeInfo info = barcodeRepository.findByBarcode(barcode);

            if (info == null) {
                model.addAttribute("error", "해당 바코드 정보를 찾을 수 없습니다.");
            } else {
                model.addAttribute("info", info);
            }

            return "barcodeImage";

        } catch (IOException | NotFoundException e) {
            model.addAttribute("error", "바코드 스캔 실패: " + e.getMessage());
            return "barcodeImage";
        }
    }
}