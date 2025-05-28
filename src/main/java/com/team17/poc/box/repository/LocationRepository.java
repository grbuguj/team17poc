package com.team17.poc.box.repository;

import com.team17.poc.box.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByMemberId(Long memberId);
}
