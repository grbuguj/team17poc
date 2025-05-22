package com.team17.poc.box.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BoxRepository extends JpaRepository<Box, Long> {
    List<Box> findByMember(Member member);

}
