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

	public Post(int postId, int pageId, LocalDateTime postDate, Instant retrievedDate, String authorName, Integer authorId, String postHtml, int threadId) {
	}

	@Id
	@Getter
	@Setter
	//@Column
	private Long id;

	@Getter @Setter
	@Column
	private Long pageNum;

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
	private Long authorId;

	@Getter @Setter
	@Column(columnDefinition = "TEXT")
	private String html;

	@Getter @Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "forum_thread_id", nullable = false)
	private Thread forumThread;



}
