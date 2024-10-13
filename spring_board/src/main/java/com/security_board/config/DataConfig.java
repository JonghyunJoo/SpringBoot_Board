package com.security_board.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableJpaRepositories(basePackages = { "com.security_board.board.repository",
		"com.security_board.security.member.repository" })
@EnableRedisRepositories(basePackages = "com.security_board.security.jwt.repository")
public class DataConfig {
	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	@Bean
	RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(host, port);
	}
}