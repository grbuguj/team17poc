package com.team17.poc.repository;

import com.team17.poc.dto.BarcodeInfo;

import java.sql.*;

public class BarcodeRepository {
    private static final String DB_PATH = "jdbc:sqlite:db/Barcode.db";

    public BarcodeInfo findByBarcode(String barcode) {
        String sql = "SELECT name, image FROM Barcode WHERE barcode = ?";

        try {
            // ✅ SQLite JDBC 드라이버 수동 로딩
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(DB_PATH);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, barcode);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    return new BarcodeInfo(
                            rs.getString("name"),
                            rs.getString("image")
                    );
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC 드라이버를 찾을 수 없습니다.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQLite DB 연결 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        return null;
    }
}
