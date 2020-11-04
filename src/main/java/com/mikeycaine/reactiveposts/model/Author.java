package com.mikeycaine.reactiveposts.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Author {

	@Getter
	@Setter
	@Id
	@EqualsAndHashCode.Include
	private Integer id;

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private String titleURL;

	@Getter
	@Setter
	private String titleText;


	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Getter
	private Set<Post> posts = new HashSet<>();

//	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//	@Getter
//	private Set<Thread> threads = new HashSet<>();
}
