package com.test.testopia.auth.service;

import com.test.testopia.auth.entity.MemberEntity;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class MemberVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long memId;
    private String memName;
    private String memEmail;
    private String role;

    // MemberEntity를 받아 DTO를 생성하는 생성자
    public MemberVO(MemberEntity member) {
        this.memId = member.getMemId();
        this.memName = member.getMemName();
        this.memEmail = member.getMemEmail();
        this.role = member.getRole();
    }
}