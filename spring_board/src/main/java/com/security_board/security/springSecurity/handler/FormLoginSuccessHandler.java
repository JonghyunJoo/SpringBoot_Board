package com.security_board.security.springSecurity.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.security_board.security.member.domain.Member;
import com.security_board.security.member.service.MemberService;
import com.security_board.security.util.Role;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FormLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final String SIGNUP_URL;
    private final String AUTH_URL;
    private final MemberService memberService;

    public FormLoginSuccessHandler(@Value("${url.base}") String BASE_URL,
                                   @Value("${url.path.signup}") String SIGN_UP_PATH,
                                   @Value("${url.path.auth}") String AUTH_PATH,
                                   MemberService memberService) {
        this.memberService = memberService;
        this.SIGNUP_URL = BASE_URL + SIGN_UP_PATH;
        this.AUTH_URL = BASE_URL + AUTH_PATH;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) authentication;
        
        String email = authToken.getName();

        Member member = memberService.searchMember(email)
                .orElseThrow(() -> new RuntimeException("MEMBER NOT FOUND"));

        // 역할에 따라 리다이렉트 URL 설정
        String redirectUrl = getRedirectUrlByRole(member.getRole(), email);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String getRedirectUrlByRole(Role role, String email) {
        if (role == Role.NOT_REGISTERED) {
            return UriComponentsBuilder.fromUriString(SIGNUP_URL)
                    .queryParam("email", email)
                    .build()
                    .toUriString();
        }

        return UriComponentsBuilder.fromHttpUrl(AUTH_URL)
                .queryParam("email", email)
                .build()
                .toUriString();
    }
}
