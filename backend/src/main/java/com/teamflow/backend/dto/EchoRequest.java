package com.teamflow.backend.dto;

import java.time.LocalDate;

public record EchoRequest(
	String title,
	String priority,
	LocalDate dueDate,
	Integer estimateHours
) {}
