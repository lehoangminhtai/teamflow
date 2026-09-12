package com.teamflow.backend.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teamflow.backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmailIgnoreCase(String email);
	
	boolean existsByEmailIgnoreCase(String email);
	
	List<User> findByFullNameContainingIgnoreCase(String keyword);
	
	long countByCreatedAtAfter(Instant since);
}
