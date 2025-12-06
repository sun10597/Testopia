package com.test.testopia.admin.service;

import com.test.testopia.auth.entity.MemberEntity;
import com.test.testopia.auth.DTO.MemberForm;
import com.test.testopia.auth.repository.MemberRepository;
import com.test.testopia.auth.DTO.MemberVO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {
    @Autowired
    MemberRepository memberRepository;

    public List<MemberVO> selectMemberList() {
        return memberRepository.findAll().stream()
                .map(MemberVO::new)
                .collect(Collectors.toList());
    }

    public MemberVO findById(Long memId) {
        Optional<MemberEntity> memberOptional = memberRepository.findById(memId);
        return memberOptional.map(MemberVO::new).orElse(null);
    }


    public MemberVO updateMember(MemberForm form) {
        Optional<MemberEntity> memberOptional = memberRepository.findById(form.getMemId());

        if (memberOptional.isEmpty()) {
            return null;
        }

        MemberEntity member = memberOptional.get();
        member.setRole(form.getRole());
        MemberEntity updatedMember = memberRepository.save(member);
        return new MemberVO(updatedMember);
    }
    @Transactional
    public void deleteMember(Long memId) {
        memberRepository.deleteById(memId);
    }
}
