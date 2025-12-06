package com.test.testopia.auth.dto;

import com.test.testopia.auth.entity.MemberEntity;
import lombok.Getter;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

@Getter
public class MemberVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long memId;
    private String memName;
    private String memEmail;
    private String role;
    private String provider;

    private String createdAt;
    private String updatedAt;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static MemberVO from(MemberEntity member) {
        MemberVO vo = new MemberVO();

        vo.memId = member.getMemId();
        vo.memName = member.getMemName();
        vo.memEmail = member.getMemEmail();
        vo.role = member.getRole();
        vo.provider = member.getProvider();

        vo.createdAt = member.getCreatedAt() != null
                ? member.getCreatedAt().format(FORMATTER)
                : "-";

        vo.updatedAt = member.getUpdatedAt() != null
                ? member.getUpdatedAt().format(FORMATTER)
                : "-";

        return vo;
    }
}
