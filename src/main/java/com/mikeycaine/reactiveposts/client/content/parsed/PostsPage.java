package com.mikeycaine.reactiveposts.client.content.parsed;

import com.mikeycaine.reactiveposts.model.Post;
import com.mikeycaine.reactiveposts.model.Thread;

import lombok.Getter;

import java.util.List;

public class PostsPage {

	public PostsPage(List<Post> posts, Thread thread, int pageNum, int maxPageNum) {
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
	private final int maxPageNum;
}
