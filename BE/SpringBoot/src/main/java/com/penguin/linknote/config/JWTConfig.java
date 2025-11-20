package com.penguin.linknote.config;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Configuration
public class JWTConfig {

	@Value("${jwt.secret.key}")
	private String key;
    
	@Bean
    public SecretKey jwtSecretKey() {
        // 從環境變量讀取,轉換成 SecretKey
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }
	
}
