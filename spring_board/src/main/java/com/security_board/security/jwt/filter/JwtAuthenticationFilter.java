package com.security_board.security.jwt.filter;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.security_board.security.jwt.service.JwtService;
import com.security_board.security.jwt.util.JwtRule;
import com.security_board.security.member.domain.Member;
import com.security_board.security.member.service.MemberService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	public static final String PERMITTED_URI[] = { "/api/auth/login", "/signup" };
	private final JwtService jwtService;
	private final MemberService memberService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (isPermittedURI(request.getRequestURI())) {
			SecurityContextHolder.getContext().setAuthentication(null);
			filterChain.doFilter(request, response);
			return;
		}

		String accessToken = jwtService.resolveTokenFromHeader(request);
		if (accessToken != null && jwtService.validateAccessToken(accessToken)) {
			setAuthenticationToContext(accessToken);
			filterChain.doFilter(request, response);
			return;
		}

		String refreshToken = jwtService.resolveTokenFromCookie(request, JwtRule.REFRESH_PREFIX);
		Member member = findMemberByRefreshToken(refreshToken);

		if (jwtService.validateRefreshToken(refreshToken, member.getEmail())) {
			String reissuedAccessToken = jwtService.generateAccessToken(response, member);
			jwtService.generateRefreshToken(response, member);

			setAuthenticationToContext(reissuedAccessToken);
			filterChain.doFilter(request, response);
			return;
		}
		jwtService.logout(member, response);
	}

	private boolean isPermittedURI(String requestURI) {
		return Arrays.stream(PERMITTED_URI).anyMatch(permitted -> {
			String regex = permitted.replace("**", ".*");
			return requestURI.matches(regex);
		});
	}

	private Member findMemberByRefreshToken(String refreshToken) {
		String email = jwtService.getEmailFromRefresh(refreshToken);
		return memberService.searchMember(email)
				.orElseThrow(() -> new RuntimeException("Member not found with email: " + email));
	}

	private void setAuthenticationToContext(String accessToken) {
		Authentication authentication = jwtService.getAuthentication(accessToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
