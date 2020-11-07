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
	@Id
	@Getter
	@Setter
	@EqualsAndHashCode.Include
	private Integer id;

	@Getter @Setter
	@ManyToOne
	@JoinColumn(nullable = false)
	private Thread thread;

	@Getter @Setter
	@Column
	private Integer pageNum;

	@Getter @Setter
	@ManyToOne
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

	@Getter @Setter
	@Column(columnDefinition = "TEXT")
	private String html;
}
