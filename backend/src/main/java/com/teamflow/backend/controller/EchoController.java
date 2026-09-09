package com.teamflow.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamflow.backend.dto.EchoRequest;
import com.teamflow.backend.dto.EchoResponse;
import com.teamflow.backend.service.EchoService;

@RestController
@RequestMapping("/api")
public class EchoController {
	private final EchoService echoService;
	
	public EchoController(EchoService echoService) {
		this.echoService = echoService;
		
	}
	
	@PostMapping("/echo")
	public EchoResponse echo(@RequestBody EchoRequest request) {
		return echoService.echo(request);
	}
}
