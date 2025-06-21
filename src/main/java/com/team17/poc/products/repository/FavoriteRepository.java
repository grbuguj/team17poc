package com.team17.poc.products.repository;

import com.team17.poc.auth.entity.Member;
import com.team17.poc.products.entity.Favorite;
import com.team17.poc.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // 특정 사용자 + 상품 찜 여부 확인 (토글용)
    Optional<Favorite> findByMemberAndProduct(Member member, Product product);

    // 로그인 사용자의 찜한 상품 목록
    List<Favorite> findByMember(Member member);

    boolean existsByProductIdAndMemberId(Long productId, Long memberId);

    void deleteByProductIdAndMemberId(Long productId, Long memberId);

    List<Favorite> findByMember_Id(Long memberId);
}
