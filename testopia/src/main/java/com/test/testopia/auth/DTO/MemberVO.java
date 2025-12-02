package com.test.testopia.auth.DTO;

import com.test.testopia.auth.entity.MemberEntity;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
public class MemberVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long memId;
    private String memName;
    private String memEmail;
    private String role;

    private String provider;     // google, kakao, naver
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // MemberEntity를 받아 DTO를 생성하는 생성자
    public MemberVO(MemberEntity member) {
        this.memId = member.getMemId();
        this.memName = member.getMemName();
        this.memEmail = member.getMemEmail();
        this.role = member.getRole();
        this.provider = member.getProvider();
        this.createdAt = member.getCreatedAt();
        this.updatedAt = member.getUpdatedAt();
    }
}