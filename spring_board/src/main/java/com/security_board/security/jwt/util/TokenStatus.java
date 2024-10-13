package com.security_board.security.jwt.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TokenStatus {
	AUTHENTICATED,
	EXPIRED,
	INVALID
}
