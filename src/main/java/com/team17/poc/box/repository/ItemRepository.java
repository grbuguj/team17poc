// 제품 리퍼지토리. JpaRepository<Item, Long> 상속 받아 기본 crud 기능을 제공함.
// memberId와 locationId 체크, 날짜 계산도 이 곳에서 처리함.
package com.team17.poc.box.repository;

import com.team17.poc.box.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByMemberId(Long memberId);
    List<Item> findByLocationId(Long locationId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Item i WHERE i.location.id = :locationId")
    void deleteByLocationId(@Param("locationId") Long locationId);


}
