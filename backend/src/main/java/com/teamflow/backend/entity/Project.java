package com.teamflow.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "projects")
public class Project extends BaseEntity{
	
	@Column(nullable = false, length = 150)
	private String name;
	
	@Column(columnDefinition = "text")
	private String description;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;
	
	@Column(nullable = false)
	private boolean archived = false;
	
	protected Project() {
		
	}

	public Project(String name, String description, User owner) {
		this.name = name;
		this.description = description;
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public User getOwner() {
		return owner;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}
	
	
}
