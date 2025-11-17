package com.test.testopia.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider;     // google, kakao, naver
    private String providerId;   // 각 서비스의 고유 ID

    private String email;
    private String name;
    private String role;         // ROLE_USER, ROLE_ADMIN 등
}
