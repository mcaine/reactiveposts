package com.mikeycaine.reactiveposts.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode
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
	@ManyToOne
	@JoinColumn(nullable = false)
	@ToString.Include
	private Forum forum;

	@Getter
	@Setter
	private int maxPageNumber;

	@Getter
	@Setter
	private int pagesGot;

	@Getter
	@Setter
	@JoinColumn
	@ManyToOne
	private Author author;

	@Getter @Setter
	@Column
	private boolean subscribed;

	public static Thread withId(int id) {
		Thread t = new Thread();
		t.setId(id);
		return t;
	}
}
