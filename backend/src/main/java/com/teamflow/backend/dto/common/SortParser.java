package com.teamflow.backend.dto.common;

import java.util.Set;

import org.springframework.data.domain.Sort;

import com.teamflow.backend.exception.ValidationException;

public final class SortParser {
	private SortParser() {
		
	}
	
	public static Sort parse(String sortParam, Set<String> allowed, Sort defaultSort) {
		if (sortParam == null || sortParam.isBlank()) {
			return defaultSort;
		}
		
		String[] parts = sortParam.split(",");
		String field = parts[0].trim();
		
		if (!allowed.contains(field)) {
			throw new ValidationException("Can not sort follow fields'" + field + "'. Allow: "+ String.join(",", allowed));
		}
		
		Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
				? Sort.Direction.DESC
				: Sort.Direction.ASC;
		return Sort.by(direction,field);
	}
}
