package com.teamflow.backend.exception;

import java.time.Instant;
import java.util.List;

import org.slf4j.MDC;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
		Instant timestamp,
		int status,
		String code,
		String message,
		String path,
		String requestId,
		List<FieldErrorItem> fieldErrors
		
		) {
	public record FieldErrorItem (String field, String message) {}
	
	public static ErrorResponse of(int status, String code, String message, String path) {
		return new ErrorResponse(Instant.now(), status, code, message, path, MDC.get("requestId"),null);
	}
}
