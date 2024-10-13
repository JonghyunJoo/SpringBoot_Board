package com.security_board.security.jwt.service;

import java.security.Key;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.security_board.security.jwt.domain.Token;
import com.security_board.security.jwt.repository.TokenRepository;
import com.security_board.security.jwt.util.JwtGenerator;
import com.security_board.security.jwt.util.JwtRule;
import com.security_board.security.jwt.util.JwtUtil;
import com.security_board.security.jwt.util.TokenStatus;
import com.security_board.security.jwt.util.UserPrincipal;
import com.security_board.security.member.domain.Member;
import com.security_board.security.member.dto.MemberDto;
import com.security_board.security.util.Role;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Slf4j
public class JwtService {
	private final CustomUserDetailsService customUserDetailsService;
	private final JwtGenerator jwtGenerator;
	private final JwtUtil jwtUtil;
	private final TokenRepository tokenRepository;

	private final Key ACCESS_SECRET_KEY;
	private final Key REFRESH_SECRET_KEY;
	private final long ACCESS_EXPIRATION;
	private final long REFRESH_EXPIRATION;

	
	public JwtService(CustomUserDetailsService customUserDetailService, JwtGenerator jwtGenerator, JwtUtil jwtUtil,
			TokenRepository tokenRepository, @Value("${jwt.access-secret}") String ACCESS_SECRET_KEY,
			@Value("${jwt.refresh-secret}") String REFRESH_SECRET_KEY,
			@Value("${jwt.access-expiration}") long ACCESS_EXPIRATION,
			@Value("${jwt.refresh-expiration}") long REFRESH_EXPIRATION) {
		super();
		this.customUserDetailsService = customUserDetailService;
		this.jwtGenerator = jwtGenerator;
		this.jwtUtil = jwtUtil;
		this.tokenRepository = tokenRepository;
		this.ACCESS_SECRET_KEY = jwtUtil.getSigningKey(ACCESS_SECRET_KEY);
		this.REFRESH_SECRET_KEY = jwtUtil.getSigningKey(REFRESH_SECRET_KEY);
		this.ACCESS_EXPIRATION = ACCESS_EXPIRATION;
		this.REFRESH_EXPIRATION = REFRESH_EXPIRATION;
	}

	// Member 객체가 가입된 유저인지 확인하는 메서드
	// 회원이 아직 등록되지 않은 사용자일 경우 인증되지 않은 사용자로 간주하고 예외를 던짐
	public void validateUser(Member requestUser) {
	    if (requestUser.getRole() == Role.NOT_REGISTERED) {
	        throw new RuntimeException("NOT AUTHENTICATED USER");
	    }
	}

	// AccessToken을 생성하고 해당 토큰을 Cookie에 설정하는 메서드
	// 생성된 AccessToken은 클라이언트에게 발급되어 Authorization 헤더로 전달됨
	public String generateAccessToken(HttpServletResponse response, Member requestUser) {
	    String accessToken = jwtGenerator.generateAccessToken(ACCESS_SECRET_KEY, ACCESS_EXPIRATION, requestUser);
	    ResponseCookie cookie = setTokenToCookie(JwtRule.ACCESS_PREFIX.getValue(), accessToken, ACCESS_EXPIRATION / 1000);
	    response.addHeader(JwtRule.JWT_ISSUE_HEADER.getValue(), cookie.toString());

	    return accessToken;
	}

	// RefreshToken을 생성하고 해당 토큰을 Cookie에 설정하는 메서드
	// RefreshToken은 데이터베이스에 저장되며, Redis를 사용하여 저장하는 예시임
	@Transactional
	public String generateRefreshToken(HttpServletResponse response, Member requestUser) {
	    String refreshToken = jwtGenerator.generateRefreshToken(REFRESH_SECRET_KEY, REFRESH_EXPIRATION, requestUser);
	    ResponseCookie cookie = setTokenToCookie(JwtRule.REFRESH_PREFIX.getValue(), refreshToken, REFRESH_EXPIRATION / 1000);
	    response.addHeader(JwtRule.JWT_ISSUE_HEADER.getValue(), cookie.toString());

	    Token token = Token.builder().email(requestUser.getEmail()).refreshToken(refreshToken).ttl(REFRESH_EXPIRATION / 1000).build();
	    tokenRepository.save(token);

	    return refreshToken;
	}

	// JWT 토큰을 Cookie에 설정하는 메서드
	// HttpOnly 및 Secure 속성으로 보안을 강화하며, 토큰의 만료 시간도 설정됨
	private ResponseCookie setTokenToCookie(String tokenPrefix, String token, long maxAgeSeconds) {
	    return ResponseCookie.from(tokenPrefix, token).path("/").maxAge(maxAgeSeconds).httpOnly(true).sameSite("Lax").secure(true).build();
	}

	// Authorization 헤더에서 AccessToken을 추출하는 메서드
	// "Bearer "로 시작하는 헤더 값에서 실제 토큰 값을 추출함
	public String resolveTokenFromHeader(HttpServletRequest request) {
	    String bearerToken = request.getHeader("Authorization");
	    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
	        return bearerToken.substring(7); // "Bearer " 제거 후 토큰 반환
	    }
	    return null;
	}

	// Cookie에서 특정 토큰을 추출하는 메서드
	// 주어진 tokenPrefix에 해당하는 토큰을 Cookie에서 찾음
	public String resolveTokenFromCookie(HttpServletRequest request, JwtRule tokenPrefix) {
	    Cookie[] cookies = request.getCookies();
	    if (cookies == null) {
	        throw new RuntimeException("JWT TOKEN NOT FOUND");
	    }
	    return jwtUtil.resolveTokenFromCookie(cookies, tokenPrefix);
	}

	// AccessToken의 유효성을 검사하는 메서드
	// JWT 유틸리티 클래스를 사용하여 토큰의 상태가 유효한지 확인함
	public boolean validateAccessToken(String token) {
	    return jwtUtil.getTokenStatus(token, ACCESS_SECRET_KEY) == TokenStatus.AUTHENTICATED;
	}

	// RefreshToken의 유효성을 검사하는 메서드
	// 데이터베이스에 저장된 토큰과 일치하는지 여부도 추가로 확인함
	public boolean validateRefreshToken(String token, String email) {
	    Optional<Token> optionalStoredToken = tokenRepository.findByEmail(email);
	    if (!optionalStoredToken.isPresent()) {
	        return false;
	    }

	    Token storedToken = optionalStoredToken.get();
	    boolean isRefreshValid = jwtUtil.getTokenStatus(token, REFRESH_SECRET_KEY) == TokenStatus.AUTHENTICATED;
	    boolean isTokenMatched = storedToken.getRefreshToken().equals(token);

	    return isRefreshValid && isTokenMatched;
	}

	// JWT 토큰에서 인증 정보를 추출하여 Authentication 객체를 반환하는 메서드
	// CustomUserDetailsService를 통해 사용자 정보를 로드함
	public Authentication getAuthentication(String token) {
	    UserDetails principal = customUserDetailsService.loadUserByUsername(getUserPk(token, ACCESS_SECRET_KEY));
	    return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
	}

	// JWT 토큰에서 사용자 PK를 추출하는 메서드
	// 토큰을 파싱하여 사용자 정보를 반환함
	private String getUserPk(String token, Key secretKey) {
	    return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
	}

	// RefreshToken에서 이메일 정보를 추출하는 메서드
	// RefreshToken이 유효하지 않을 경우 예외를 던짐
	public String getEmailFromRefresh(String refreshToken) {
	    try {
	        return Jwts.parserBuilder().setSigningKey(REFRESH_SECRET_KEY).build().parseClaimsJws(refreshToken).getBody().getSubject();
	    } catch (Exception e) {
	        throw new RuntimeException("INVALID JWT");
	    }
	}
	
	// 로그아웃 처리 메서드
	// 저장된 토큰을 삭제하고, AccessToken과 RefreshToken을 쿠키에서 초기화함
	public void logout(Member requestUser, HttpServletResponse response) {
	    tokenRepository.deleteById(requestUser.getEmail());

	    Cookie accessCookie = jwtUtil.resetToken(JwtRule.ACCESS_PREFIX);
	    Cookie refreshCookie = jwtUtil.resetToken(JwtRule.REFRESH_PREFIX);

	    response.addCookie(accessCookie);
	    response.addCookie(refreshCookie);
	}

}
