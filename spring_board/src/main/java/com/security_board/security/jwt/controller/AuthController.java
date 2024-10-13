package com.security_board.security.jwt.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security_board.security.jwt.dto.TokenRequest;
import com.security_board.security.jwt.service.JwtService;
import com.security_board.security.jwt.util.JwtRule;
import com.security_board.security.jwt.util.UserPrincipal;
import com.security_board.security.member.domain.Member;
import com.security_board.security.member.dto.MemberDto;
import com.security_board.security.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
	private final MemberService memberService;
	private final JwtService jwtService;

	@PostMapping("/login")
	public ResponseEntity<MemberDto> login(HttpServletResponse response, @RequestBody TokenRequest tokenRequest) {
		Optional<Member> requestUserOpt = memberService.searchMember(tokenRequest.email());

		if (requestUserOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		Member requestUser = requestUserOpt.get();

		if (!memberService.validatePassword(tokenRequest.password(), requestUser)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		jwtService.generateAccessToken(response, requestUser);
		jwtService.generateRefreshToken(response, requestUser);

		MemberDto memberDto = MemberDto.getDto(requestUser.getEmail(), requestUser.getName());
		return ResponseEntity.ok(memberDto);
	}

	@GetMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		Optional<Member> requestUserOpt = memberService.searchMember(userPrincipal.getMember().getEmail());
		jwtService.logout(requestUserOpt.get(), response);

		return ResponseEntity.ok("로그아웃되었습니다.");
	}
}