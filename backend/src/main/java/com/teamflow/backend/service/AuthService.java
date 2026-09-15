package com.teamflow.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.teamflow.backend.dto.auth.LoginRequest;
import com.teamflow.backend.dto.auth.LoginResponse;
import com.teamflow.backend.dto.auth.RegisterRequest;
import com.teamflow.backend.dto.user.UserResponse;
import com.teamflow.backend.entity.User;
import com.teamflow.backend.exception.ConflictException;
import com.teamflow.backend.exception.UnauthorizedException;
import com.teamflow.backend.mapper.UserMapper;
import com.teamflow.backend.repository.UserRepository;
import com.teamflow.backend.security.JwtService;

@Service
public class AuthService {
	private static final Logger log = LoggerFactory.getLogger(AuthService.class);
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;
	private final JwtService jwtService;
	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userMapper = userMapper;
		this.jwtService = jwtService;
	}
	
	public UserResponse register(RegisterRequest request) {
		String email = request.email().trim().toLowerCase();
		
		if(userRepository.existsByEmailIgnoreCase(email)) {
			throw new ConflictException("Email used");
		}
		
		User user = new User(email, passwordEncoder.encode(request.password()), request.fullname().trim());
		 
		User saved;
		
		try {
			saved = userRepository.save(user);
		} catch (DataIntegrityViolationException ex) {
			log.warn("conflict email at database layer");
			throw new ConflictException("Email used");
		}
		
		log.info("User registered: id = {}", saved.getId());
		return userMapper.toResponse(saved);
	}
	
	public LoginResponse login(LoginRequest request) {
		String email = request.email().trim().toLowerCase();
		User user = userRepository.findByEmailIgnoreCase(email)
				.orElseThrow(this::invalidCredentials);
		if(!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw invalidCredentials();
		}
		
		String token = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getFullName());
		log.info("User logged in: id={}",user.getId());
		
		return new LoginResponse(
				token,
				jwtService.getAccessTokenMinutes()*60,
				userMapper.toResponse(user));
	}
	
	private UnauthorizedException invalidCredentials() {
		return new UnauthorizedException("INVALID_CREDENTIALS", "Email or password not correct");
	}
	
}
