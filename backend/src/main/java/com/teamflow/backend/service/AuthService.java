package com.teamflow.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.teamflow.backend.dto.auth.RegisterRequest;
import com.teamflow.backend.dto.user.UserResponse;
import com.teamflow.backend.entity.User;
import com.teamflow.backend.exception.ConflictException;
import com.teamflow.backend.mapper.UserMapper;
import com.teamflow.backend.repository.UserRepository;

@Service
public class AuthService {
	private static final Logger log = LoggerFactory.getLogger(AuthService.class);
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;
	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userMapper = userMapper;
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
	
}
