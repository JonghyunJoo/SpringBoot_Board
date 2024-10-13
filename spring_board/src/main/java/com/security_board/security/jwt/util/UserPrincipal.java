package com.security_board.security.jwt.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.security_board.security.member.domain.Member;

import lombok.Getter;


@Getter
public class UserPrincipal implements UserDetails, OAuth2User {

	private Member member;
    private String nameAttributeKey; // for OAuth
    private Map<String, Object> attributes; // for OAuth
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Member member) {
        this.member = member;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(member.getRole().getKey()));
    }

    public UserPrincipal(Member member, Map<String, Object> attributes, String nameAttributeKey) {
        this.member = member;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(member.getRole().getKey()));
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    @Override
    public String getName() {
        return member.getEmail();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return String.valueOf(member.getId());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
