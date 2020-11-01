package com.mikeycaine.reactiveposts.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@ToString(onlyExplicitlyIncluded = true)
public class Thread {
	@Id
	@Getter
	@Setter
	@ToString.Include
	private Integer id;

	@Getter
	@Setter
	@ToString.Include
	private String name;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Forum forum;

	@Getter
	@Setter
	private int maxPageNumber;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(nullable = false)
	private Author author;
}
