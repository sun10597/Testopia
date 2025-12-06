package com.test.testopia.config;

import com.test.testopia.auth.service.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// 💡 세션 관리를 위한 import 추가
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private OAuth2UserService OAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())

                // 💡 1. 세션 관리 정책 추가
                .sessionManagement(session -> session
                        // 항상 세션을 사용하거나, 필요할 경우 생성하도록 설정
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)  // 로그아웃 시 세션 제거
                        .deleteCookies("JSESSIONID")// 쿠키(세션ID) 제거
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(OAuth2UserService)
                        )
                        .defaultSuccessUrl("/", true)
                );

        return http.build();
    }
}