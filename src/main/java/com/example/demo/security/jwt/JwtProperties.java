package com.example.demo.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
	private String secretKey = "secret";

	private long validityInMs = 36000000;

	// private long validityInMs = 3600;

}
