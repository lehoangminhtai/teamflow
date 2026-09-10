package com.teamflow.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InfoService {
	
	private final String name;
	private final String version;
	private final String environment;
	
	
	
	public InfoService(
		@Value("${app.name}")String name,
		@Value("${app.version}") String version,
		@Value("${app.environment}") String environment) {
		this.name = name;
		this.version = version;
		this.environment = environment;
	}

	public String appName() {
		return name;
	}
	
	public String version() {
		return version;
	}
	
	public String environment() {
		return environment;
	}
}
