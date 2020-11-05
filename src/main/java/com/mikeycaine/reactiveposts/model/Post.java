package com.mikeycaine.reactiveposts.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@ToString
public class Post {
//	public Post() {
//
//	}
//
//	// TODO Author here
//	public Post(int postId, int pageNum, LocalDateTime postDate, Instant retrievedDate, Author author, String html, Thread thread) {
//		this.id = postId;
//		this.pageNum = pageNum;
//		this.postDate = postDate;
//		this.retrievedDate = retrievedDate;
//		this.author = author;
//		//this.authorName = authorName;
//		//this.authorId = authorId;
//		this.html = html;
//		this.thread = thread;
//	}

	@Id
	@Getter
	@Setter
	//@Column
	@EqualsAndHashCode.Include
	private Integer id;

	@Getter @Setter
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(nullable = false)
	private Thread thread;

	@Getter @Setter
	@Column
	private Integer pageNum;

	@Getter @Setter
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(nullable = false)
	private Author author;

	@Getter @Setter
	@Column
	@JsonSerialize(using= LocalDateTimeSerializer.class)
	private LocalDateTime postDate;

	@Getter @Setter
	@Column
	@JsonSerialize(using= LocalDateTimeSerializer.class)
	private Instant retrievedDate;

//	@Getter @Setter
//	@Column
//	private String authorName;

//	@Getter @Setter
//	@Column
//	private Integer authorId;

	@Getter @Setter
	@Column(columnDefinition = "MEDIUMTEXT")
	private String html;
}
