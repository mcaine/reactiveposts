package com.mikeycaine.reactiveposts.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class Thread {
	@Id
	@Getter
	@Setter
	//@Column
	private Long id;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Forum forum;
}
