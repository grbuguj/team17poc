package com.team17.poc.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    private String password;    // 일반 로그인만 사용

    private String provider;    // 'local' or 'google'
    private String providerId;  // 구글 sub 값


    // 필요시 생성자/Getter/Setter 추가 (또는 @Data, @Builder 등 Lombok 활용)
}
