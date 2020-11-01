package com.mikeycaine.reactiveposts.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Author {

	@Getter
	@Setter
	@Id
	private Integer id;

	@Getter
	@Setter
	private String name;
}
