package com.team17.poc.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class LocalFileUploader {

    private final String uploadDir = "C:/upload/images/"; // 운영체제 외부 경로로 변경


    public String upload(MultipartFile file) throws IOException {
        System.out.println(">>>> UploadDir: " + uploadDir);
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            System.out.println(">>>> 폴더가 없어 생성 시도");
            dir.mkdirs();
        }

        File target = new File(dir, filename);
        System.out.println(">>>> 저장할 파일 경로: " + target.getAbsolutePath());

        try {
            file.transferTo(target);
        } catch (Exception e) {
            e.printStackTrace();  // 🔥 여기에 반드시 추가
            throw e;               // 원래 예외를 다시 던져서 위에서 잡힘
        }

        return "/images/" + filename;
    }

}
