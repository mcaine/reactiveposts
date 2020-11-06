package com.mikeycaine.reactiveposts.client.content.parsed;

import com.mikeycaine.reactiveposts.model.Post;
import com.mikeycaine.reactiveposts.model.Thread;

import lombok.Getter;

import java.util.List;
import java.util.Optional;

public class PostsPage {

	public PostsPage(List<Post> posts, Thread thread, int pageNum, Optional<Integer> maxPageNum) {
		this.posts = posts;
		this.thread = thread;
		this.pageNum = pageNum;
		this.maxPageNum = maxPageNum;
	}

	@Getter
	private final List<Post> posts;

	@Getter
	private final Thread thread;

	@Getter
	private final int pageNum;

	@Getter
	private final Optional<Integer> maxPageNum;
}
