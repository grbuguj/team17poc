// 제품 리퍼지토리. JpaRepository<Item, Long> 상속 받아 기본 crud 기능을 제공함.
// memberId와 locationId 체크, 날짜 계산도 이 곳에서 처리함.
package com.team17.poc.box.repository;

import com.team17.poc.box.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByMemberId(Long memberId);
    List<Item> findByLocationId(Long locationId);

    // 장소 조회 시, 제품 개수 카운트 위한 코드
    int countByLocationId(Long locationId);


    // 제품 단일 조회 위한 code
    Optional<Item> findByIdAndMember_Id(Long id, Long memberId);

    // 정렬 관련 (유통기한 임박순, 최신 등록순, 과거 등록순)
    List<Item> findByMemberIdOrderByExpireDateAsc(Long memberId);
    List<Item> findByMemberIdOrderByRegisterDateDesc(Long memberId);
    List<Item> findByMemberIdOrderByRegisterDateAsc(Long memberId);
    List<Item> findByMemberIdOrderByLocationIdAsc(Long memberId);


    @Transactional
    @Modifying
    @Query("DELETE FROM Item i WHERE i.location.id = :locationId")
    void deleteByLocationId(@Param("locationId") Long locationId);

    /**
     * ✅ 특정 사용자에 대해 유통기한이 기준일 이내로 남은 상품 조회
     */
    List<Item> findItemsByMemberIdAndExpireDateLessThanEqual(Long memberId, LocalDate threshold);


}
