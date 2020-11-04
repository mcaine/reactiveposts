package com.mikeycaine.reactiveposts.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Author {

	@Getter
	@Setter
	@Id
	private Integer id;

	@Getter
	@Setter
	private String name;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Getter
	private Set<Post> posts = new HashSet<>();

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Getter
	private Set<Thread> threads = new HashSet<>();
}
