package com.test.testopia.auth.repository;

import com.test.testopia.auth.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByProviderAndProviderId(String provider, String providerId);
}
