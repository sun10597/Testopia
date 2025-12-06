package com.test.testopia.auth.service;

import com.test.testopia.auth.DTO.MemberVO;
import com.test.testopia.auth.entity.MemberEntity;
import com.test.testopia.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String provider = registrationId;
        String providerId;
        String name;
        String email;
        String principalKey; // ★ provider별 principal 키

        // ★★★ provider별 attribute 파싱 ★★★
        if ("google".equals(registrationId)) {
            providerId = (String) attributes.get("sub");
            name = (String) attributes.get("name");
            email = (String) attributes.get("email");
            principalKey = "sub";
            System.err.println("🔍 GOOGLE ATTRIBUTES = " + oAuth2User.getAttributes());

        } else if ("kakao".equals(registrationId)) {
            providerId = String.valueOf(attributes.get("id"));

            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = kakaoAccount != null
                    ? (Map<String, Object>) kakaoAccount.get("profile")
                    : null;

            name = profile != null ? (String) profile.get("nickname") : null;
            email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
            principalKey = "id";
            attributes = Map.of(
                    "id", providerId,
                    "name", name,
                    "email", email
            );

            System.err.println("🔍 KAKAO FLAT ATTRIBUTES = " + attributes);

        } else if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");

            providerId = (String) response.get("id");
            name = (String) response.get("name");
            email = (String) response.get("email");

            attributes = Map.of(
                    "id", providerId,
                    "name", name,
                    "email", email
            );

            principalKey = "id";
            System.err.println("🔍 NAVER ATTRIBUTES = " + oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 provider: " + registrationId);
        }

        // ★★★ DB 저장 ★★★
        MemberEntity member = memberRepository
                .findByProviderAndProviderId(provider, providerId)
                .orElseGet(() ->
                        memberRepository.save(MemberEntity.builder()
                                .provider(provider)
                                .providerId(providerId)
                                .memName(name)
                                .memEmail(email)
                                .role("0")
                                .build())
                );
        MemberVO vo = new MemberVO(member);

        // ★★★ Spring Security에서 사용할 OAuth2User 생성 ★★★
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole())),
                Map.of(principalKey, providerId, "member", vo),
                principalKey
        );
    }
}
