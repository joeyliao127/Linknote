package com.penguin.linknote.domain.auth;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthClaim {
	private UUID userId;
	private String email;
	private String username;
}
