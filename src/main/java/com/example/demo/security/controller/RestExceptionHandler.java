package com.example.demo.security.controller;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.status;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.example.demo.security.jwt.InvalidJwtAuthenticationException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {
	

	@ExceptionHandler(value = { InvalidJwtAuthenticationException.class })
	public ResponseEntity invalidJwtAuthentication(InvalidJwtAuthenticationException ex, WebRequest request) {
		log.debug("handling InvalidJwtAuthenticationException...");
		return status(UNAUTHORIZED).build();
	}
}
