package com.security_board.security.jwt.util;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nimbusds.jose.util.StandardCharset;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JwtUtil {
	public TokenStatus getTokenStatus(String token, Key secretKey) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(secretKey)
					.build()
					.parseClaimsJws(token);
			return TokenStatus.AUTHENTICATED;
		} catch (ExpiredJwtException | IllegalArgumentException e) {
			log.error(e.getMessage());
			return TokenStatus.EXPIRED;
		} catch(JwtException e) {
			throw new RuntimeException("Invalid JWT");
		}
	}
	
	public String resolveTokenFromCookie(Cookie[] cookies, JwtRule tokenPrefix) {
	    return Arrays.stream(cookies)
	            .filter(cookie -> cookie.getName().equals(tokenPrefix.getValue()))
	            .findFirst()
	            .map(Cookie::getValue)
	            .orElse("");
	}

	public Key getSigningKey(String secretKey) {
		String encodedKey = encodeToBase64(secretKey);
		return Keys.hmacShaKeyFor(encodedKey.getBytes(StandardCharset.UTF_8));
	}
	
	public String encodeToBase64(String secretKey) {
		return Base64.getEncoder().encodeToString(secretKey.getBytes());
	}
	
	public Cookie resetToken(JwtRule tokenPrefix) {
		Cookie cookie = new Cookie(tokenPrefix.getValue(), null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		return cookie;
	}
}
