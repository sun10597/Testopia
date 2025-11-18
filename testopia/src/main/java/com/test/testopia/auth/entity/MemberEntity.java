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
    private Long memId;

    private String provider;     // google, kakao, naver
    private String providerId;   // 각 서비스의 고유 ID

    private String memEmail;
    private String memName;
    private String role;         // ROLE_USER, ROLE_ADMIN 등
}
