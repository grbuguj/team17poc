package com.team17.poc.box.repository;

import com.team17.poc.barcode.dto.BarcodeInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.sql.*;

@Repository
public class BarcodeJdbcRepository {

    // ✅ SQLite DB 파일 경로 (상대 경로 또는 절대 경로 명확히)
    private static final String DB_PATH = "jdbc:sqlite:db/Barcode.db";

    public Optional<BarcodeInfo> findByBarcode(String barcode) {
        String sql = "SELECT name, image FROM `Barcode` WHERE barcode = ?";

        try {
            // ✅ 드라이버 로딩
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(DB_PATH);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, barcode);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    BarcodeInfo info = new BarcodeInfo(
                            rs.getString("name"),
                            rs.getString("image")
                    );
                    System.out.println("🔍 DB에서 바코드 조회: " + barcode);
                    return Optional.of(info);
                } else {
                    System.out.println("⚠️ DB에 해당 바코드 없음: " + barcode);
                }

            }
        } catch (Exception e) {
            System.err.println("❌ DB 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
