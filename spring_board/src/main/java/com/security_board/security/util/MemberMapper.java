package com.security_board.security.util;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.security_board.security.member.domain.Member;
import com.security_board.security.member.dto.MemberDto;

public class MemberMapper {
    public static MemberDto toGetDtoFromEntity(Member member) {
        return MemberDto.getDto(
            member.getEmail(),
            member.getName()
        );
    }
    
	public static Member toEntityFromPostDto(MemberDto memberDto, PasswordEncoder passwordEncoder) {
		Member member = Member.builder()
				.email(memberDto.getEmail())
				.password(passwordEncoder.encode(memberDto.getPassword()))
				.name(memberDto.getName())
				.role(Role.USER)
				.build();	
		return member;
	}
}
