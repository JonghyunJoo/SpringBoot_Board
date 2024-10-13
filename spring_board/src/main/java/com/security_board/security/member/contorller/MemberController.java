package com.security_board.security.member.contorller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.security_board.security.member.dto.MemberDto;
import com.security_board.security.member.service.MemberService;
import com.security_board.security.util.Header;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class MemberController {
	private final MemberService memberService;
	
	@PostMapping("/signup")
	public Header<String> signUp(@RequestBody MemberDto memberDto){
		return memberService.saveMember(memberDto);
	}
}
