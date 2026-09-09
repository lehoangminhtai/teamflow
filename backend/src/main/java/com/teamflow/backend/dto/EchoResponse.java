package com.teamflow.backend.dto;

import java.time.Instant;
import java.time.LocalDate;

public record EchoResponse(
		String receivedTitle,
		String receivedPriority,
		LocalDate dueDate,
		Integer doubleEstimate,
		Instant receivedAT
		) {

}
