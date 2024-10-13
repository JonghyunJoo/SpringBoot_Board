package com.security_board.security.jwt.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.security_board.security.jwt.util.UserPrincipal;
import com.security_board.security.member.domain.Member;
import com.security_board.security.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{
	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = memberRepository.findByEmail(username)
				.orElseThrow(()-> new UsernameNotFoundException("Member Not Fount"));
		
		return new UserPrincipal(member);
	}
	
}
