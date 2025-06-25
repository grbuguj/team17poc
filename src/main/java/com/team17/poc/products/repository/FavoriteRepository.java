package com.team17.poc.products.repository;

import com.team17.poc.auth.entity.Member;
import com.team17.poc.products.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {




    // 로그인 사용자의 찜한 상품 목록
    List<Favorite> findByMember(Member member);

    boolean existsByProduct_IdAndMember_Id(Long productId, Long memberId);

    void deleteByProductIdAndMemberId(Long productId, Long memberId);


}
