package com.security_board.security.member.domain;


import com.security_board.security.util.Role;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name= "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 50, nullable = false)
	private String email;
	
	@Column(length = 100)
	private String password;
	
	@Column(length = 20, nullable = false)
	private String name;
	
	@Column(length = 20)
	private String socialId;
	
	@Enumerated(EnumType.STRING)
	private Role role;
}
