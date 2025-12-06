package com.test.testopia.admin.service;

import com.test.testopia.auth.entity.MemberEntity;
import com.test.testopia.auth.dto.MemberForm;
import com.test.testopia.auth.repository.MemberRepository;
import com.test.testopia.auth.dto.MemberVO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    MemberRepository memberRepository;

    public Page<MemberVO> selectMemberList(int page) {
        // page: 0부터, size: 10명씩, 최신 가입자 먼저
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "memId"));

        return memberRepository.findAll(pageable)
                .map(MemberVO::from);
    }

    public MemberVO findById(Long memId) {
        Optional<MemberEntity> memberOptional = memberRepository.findById(memId);
        return memberOptional
                .map(MemberVO::from)
                .orElse(null);
    }

    public MemberVO updateMember(MemberForm form) {
        Optional<MemberEntity> memberOptional = memberRepository.findById(form.getMemId());

        if (memberOptional.isEmpty()) {
            return null;
        }

        MemberEntity member = memberOptional.get();
        member.setRole(form.getRole());
        MemberEntity updatedMember = memberRepository.save(member);

        return MemberVO.from(updatedMember);
    }

    @Transactional
    public void deleteMember(Long memId) {
        memberRepository.deleteById(memId);
    }
}
