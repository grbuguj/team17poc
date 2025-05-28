package com.team17.poc.box.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 장소 이름 (ex. 냉장고, 찬장 등)

    private Long memberId; // 이 장소를 등록한 사용자 ID
}
