package com.team17.poc.products.repository;

import com.team17.poc.auth.entity.Member;
import com.team17.poc.products.entity.Product;
import com.team17.poc.products.entity.ProductStatus;
import com.team17.poc.products.entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 거래 유형 필터링 없이 전체 조회 + member fetch
    @Query("SELECT p FROM Product p JOIN FETCH p.member ORDER BY p.createdAt DESC")
    List<Product> findAllWithMember();

    // 거래 유형 필터링 + member fetch
    @Query("SELECT p FROM Product p JOIN FETCH p.member WHERE p.type = :type ORDER BY p.createdAt DESC")
    List<Product> findByTypeWithMember(@Param("type") ProductType type);

    // 내가 등록한 상품들 (마이페이지용 등)
    List<Product> findByMember(Member member);

    List<Product> findByStatusOrderByCreatedAtDesc(ProductStatus status);

    List<Product> findByTypeAndStatusOrderByCreatedAtDesc(ProductType type, ProductStatus status);
}
