package com.mikeycaine.reactiveposts.model;

import lombok.*;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Forum {

	public Forum(Integer forumId, String forumName, Set<Forum> subForums) {
		this.id = forumId;
		this.name = forumName;
		this.topLevelForum = true;
		this.subForums = subForums;
	}

	public Forum(Integer forumId, String forumName) {
		this.id = forumId;
		this.name = forumName;
		this.topLevelForum = false;
	}

	@Getter @Setter
	@Id
	@EqualsAndHashCode.Include
	@ToString.Include
	private Integer id;

	@Getter @Setter
	@Column
	@ToString.Include
	private String name;

	@Getter @Setter
	@Column
	private boolean subscribed;

	@Getter @Setter
	@Column
	private boolean topLevelForum;

	@Getter
	@OneToMany(
		mappedBy = "forum",
		cascade = CascadeType.ALL,
		orphanRemoval = true,
		fetch = FetchType.LAZY
	)
	private Set<Thread> threads = new HashSet<>();

	@Getter
	@OneToMany(
		cascade = CascadeType.ALL,
		orphanRemoval = true
	)
	private Set<Forum> subForums = new HashSet<>();

	public String desc() {
		return String.format("FORUM %3d %s", this.id, this.name);
	}

	public String prettyPrint() {
		return Stream.concat(
			Stream.of(desc()),
			this.subForums.stream().map(Forum::desc))
			.collect(Collectors.joining("\n - "));
	}
}
