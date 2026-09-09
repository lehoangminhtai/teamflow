package com.teamflow.backend.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.teamflow.backend.dto.EchoRequest;
import com.teamflow.backend.dto.EchoResponse;

@Service
public class EchoService {
	public EchoResponse echo(EchoRequest request) {
		Integer doubled = request.estimateHours() == null ? null : request.estimateHours()*2;
		return new EchoResponse(request.title(), request.priority(), request.dueDate(),doubled,Instant.now());
	}
}
