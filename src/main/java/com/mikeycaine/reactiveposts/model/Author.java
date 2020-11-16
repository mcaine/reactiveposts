package com.mikeycaine.reactiveposts.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Author {

	@Getter
	@Setter
	@Id
	@EqualsAndHashCode.Include
	private Integer id;

	@Getter
	@Setter
	@Column
	private String name;

	@Getter
	@Setter
	@Column
	private String titleURL;

	@Getter
	@Setter
	@Column(columnDefinition = "TEXT")
	private String titleText;
}
