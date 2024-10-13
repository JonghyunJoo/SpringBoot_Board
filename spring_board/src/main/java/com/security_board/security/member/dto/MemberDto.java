package com.security_board.security.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor // 기본 생성자 자동 생성
public class MemberDto {
    private String email;
    private String password;
    private String name;

    public static MemberDto postDto(String email, String password, String name) {
        return MemberDto.builder()
                .email(email)
                .password(password)
                .name(name)
                .build();
    }

    public static MemberDto getDto(String email, String name) {
        return MemberDto.builder()
                .email(email)
                .name(name)
                .build();
    }
}
