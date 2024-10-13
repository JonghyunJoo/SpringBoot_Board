package com.security_board.security.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.security_board.security.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
	Optional<Member> findByEmail(@Param("email") String email);
	
    Optional<Member> findBySocialId(@Param("socialId") String socialId);
}
