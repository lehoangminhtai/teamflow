package com.teamflow.backend.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import tools.jackson.databind.ObjectMapper;
import com.teamflow.backend.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityErrorWriter implements AuthenticationEntryPoint, AccessDeniedHandler {
	private final ObjectMapper objectMapper;

	public SecurityErrorWriter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) 
	throws IOException{
		write(response,request,HttpStatus.UNAUTHORIZED,"UNAUTHORIZED","You need to login to do this action");
	}
	@Override
	public void handle (HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException ex)
	throws IOException{
		write(response, request, HttpStatus.FORBIDDEN, "FORBIDDEN", "You do not have permission to perform this action.");
	}

	private void write(HttpServletResponse response, HttpServletRequest request, HttpStatus status, String code,
			String message) throws IOException {
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		
		ErrorResponse body = ErrorResponse.of(status.value(), code, message, request.getRequestURI());
		objectMapper.writeValue(response.getOutputStream(), body);
	}
	
}
