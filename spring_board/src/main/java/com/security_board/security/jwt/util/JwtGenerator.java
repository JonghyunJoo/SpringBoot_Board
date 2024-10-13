package com.security_board.security.jwt.util;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.security_board.security.member.domain.Member;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtGenerator {
	public String generateAccessToken(final Key ACCESS_SECRET, final long ACCESS_EXPIRATION, Member member) {
		Long now = System.currentTimeMillis();
		
		return Jwts.builder()
				.setHeader(createHeader())
				.setClaims(createClaims(member))
				.setSubject(member.getEmail())
				.setExpiration(new Date(now + ACCESS_EXPIRATION))
				.signWith(ACCESS_SECRET, SignatureAlgorithm.HS256)
				.compact();
	}
	
	public String generateRefreshToken(final Key REFRESH_SECRET, final long REFRESH_EXPIRATION, Member member) {
		Long now = System.currentTimeMillis();
		
		return Jwts.builder()
				.setHeader(createHeader())
				.setSubject(member.getEmail())
				.setExpiration(new Date(now + REFRESH_EXPIRATION))
				.signWith(REFRESH_SECRET, SignatureAlgorithm.HS256)
				.compact();
	}

	public Map<String, Object> createHeader() {
		Map<String, Object> header = new HashMap<>();
		header.put("typ", "JWT");
		header.put("alg", "HS512");
		return header;
	}
	
	private Map<String, Object> createClaims(Member member){
		Map<String, Object> claims = new HashMap<>();
		claims.put("email", member.getEmail());
		claims.put("role", member.getRole());
		return claims;
	}
}
