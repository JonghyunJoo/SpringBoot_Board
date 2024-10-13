package com.security_board.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.security_board.security.jwt.filter.JwtAuthenticationFilter;
import com.security_board.security.jwt.service.JwtService;
import com.security_board.security.member.service.MemberService;
import com.security_board.security.springSecurity.handler.FormLoginFailHandler;
import com.security_board.security.springSecurity.handler.FormLoginSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableWebMvc
public class SecurityConfig {
	public static final String PERMITTED_URI[] = {"/api/auth/**", "/signup"};
    private static final String PERMITTED_ROLES[] = {"USER", "ADMIN"};
    private final JwtService jwtService;
    private final MemberService memberService;
    private final FormLoginSuccessHandler formLoginSuccessHandler;
    private final FormLoginFailHandler formLoginFailHandler;
    
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		
		corsConfiguration.addAllowedOrigin("http://localhost:3000");
		corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"));
		corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
		corsConfiguration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration); // 모든 경로에 대해서 CORS 설정을 적용

		return source;
	}


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()));

        http.csrf(AbstractHttpConfigurer::disable);
        
        http.httpBasic(HttpBasicConfigurer::disable);

        http.formLogin(AbstractHttpConfigurer::disable);
        
		http.sessionManagement(httpSecuritySessionManagementConfigurer -> {
			httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.NEVER);
		});

		http.authorizeHttpRequests(request -> request
				.requestMatchers(PERMITTED_URI).permitAll()
			    .requestMatchers("/api/admin/**").hasRole("ADMIN")
			    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
			    .anyRequest().hasAnyRole(PERMITTED_ROLES)
			);


		http.addFilterBefore(new JwtAuthenticationFilter(jwtService, memberService), UsernamePasswordAuthenticationFilter.class);

//		http.formLogin(httpSecurityCorsConfigurer -> {
//			httpSecurityCorsConfigurer.loginPage("/login")
//			.successHandler(formLoginSuccessHandler)
//			.failureHandler(formLoginFailHandler);
//		});
		
//		http.oauth2Login(httpSecurityOAuth2Loginconfigurer->
//				httpSecurityOAuth2Loginconfigurer.loginPage("/oauth2/login")
//				.successHandler(commonLoginSuccessHandler())
//						.userInfoEndpoint(
//								userInfoEndpointConfig -> userInfoEndpointConfig.userService(oAuth2UserService)));
		
		
		return http.build();
	}
}
