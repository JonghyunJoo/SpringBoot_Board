package com.security_board.security.jwt.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.security_board.security.jwt.domain.Token;

@Repository
public interface TokenRepository extends CrudRepository<Token, String>{
	Optional<Token> findByEmail(String email);
}
