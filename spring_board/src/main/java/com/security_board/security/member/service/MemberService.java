package com.security_board.security.member.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.security_board.security.member.domain.Member;
import com.security_board.security.member.dto.MemberDto;
import com.security_board.security.member.repository.MemberRepository;
import com.security_board.security.util.Header;
import com.security_board.security.util.MemberMapper;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class MemberService {
	
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	
	public Header<String> saveMember(MemberDto memberDto) {
		if(this.searchMember(memberDto.getEmail()).isPresent()) {
			return Header.ERROR("동일한 이메일이 존재합니다");
		}
		memberRepository.save(MemberMapper.toEntityFromPostDto(memberDto, passwordEncoder));
		return Header.OK("성공적으로 가입되었습니다");
	}
	
	public Optional<Member> searchMember(String email){
		return memberRepository.findByEmail(email);
	}
	
	public boolean validatePassword(String rawPassword, Member member) {
	    return passwordEncoder.matches(rawPassword, member.getPassword());
	}
}