package com.teamflow.backend.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EchoRequest(
	@NotBlank(message = "Title not noll or blank")
	@Size(max = 200, message = "Max size 200 character")
	String title,
	
	@Pattern(regexp = "LOW |MEDIUM |HIGH |URGENT",
				message = "Priority must be LOW, MEDIUM, HIGH, URGENT "
	)
	String priority,
	
	@FutureOrPresent(message = "Due date not in pass")
	LocalDate dueDate,
	
	@Min(value = 1, message = "Min 1")
	@Max(value = 200, message = "Max 200")
	Integer estimateHours
) {}
