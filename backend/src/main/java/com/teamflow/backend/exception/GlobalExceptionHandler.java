package com.teamflow.backend.exception;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ErrorResponse> handleApi(ApiException ex, HttpServletRequest req){
		log.warn("{} tai {}: {}", ex.getCode(), req.getRequestURI(), ex.getMessage());
		ErrorResponse body = ErrorResponse.of(ex.getStatus().value(), ex.getCode(), ex.getMessage(), req.getRequestURI());
		return ResponseEntity.status(ex.getStatus()).body(body);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(
			MethodArgumentNotValidException ex, HttpServletRequest req
			){
		List<ErrorResponse.FieldErrorItem> fields = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(fe -> new ErrorResponse.FieldErrorItem(
						fe.getField(), fe.getDefaultMessage()))
				.toList();
		
		ErrorResponse body = new ErrorResponse(
				Instant.now(),
				HttpStatus.BAD_REQUEST.value(),
				"VALIDATION_ERROR",
				"Data not valid",
				req.getRequestURI(),
				req.getRequestId(),
				fields
				);
		return ResponseEntity.badRequest().body(body);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleUnreadable(
			HttpMessageNotReadableException ex, HttpServletRequest req
			){
		
		ErrorResponse body = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", "Can not read body request", req.getRequestURI());
		return ResponseEntity.badRequest().body(body);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpected(
			Exception ex, HttpServletRequest req
			){
		log.error("Error at {}", req.getRequestURI(), ex);
		ErrorResponse body = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_ERROR", "Erro Orcurce", req.getRequestURI());
		return ResponseEntity.internalServerError().body(body);
	}
	
	
	
}
