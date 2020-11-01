package com.mikeycaine.reactiveposts.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
public class Post {
	public Post() {

	}

	public Post(int postId, int pageNum, LocalDateTime postDate, Instant retrievedDate, String authorName, Integer authorId, String html, Thread thread) {
		this.id = postId;
		this.pageNum = pageNum;
		this.postDate = postDate;
		this.retrievedDate = retrievedDate;
		this.authorName = authorName;
		this.authorId = authorId;
		this.html = html;
		this.thread = thread;
	}

	@Id
	@Getter
	@Setter
	//@Column
	private Integer id;

	@Getter @Setter
	@Column
	private Integer pageNum;

	@Getter @Setter
	@Column
	@JsonSerialize(using= LocalDateTimeSerializer.class)
	private LocalDateTime postDate;

	@Getter @Setter
	@Column
	@JsonSerialize(using= LocalDateTimeSerializer.class)
	private Instant retrievedDate;

	@Getter @Setter
	@Column
	private String authorName;

	@Getter @Setter
	@Column
	private Integer authorId;

	@Getter @Setter
	@Column(columnDefinition = "TEXT")
	private String html;

	@Getter @Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Thread thread;



}
