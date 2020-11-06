package com.mikeycaine.reactiveposts.client.content.parsed;

import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Thread;
import lombok.Getter;

import java.util.List;

public class ThreadsIndex {

	public ThreadsIndex(Forum forum, int pageNum, List<Thread> threads) {
		this.forum = forum;
		this.pageNum = pageNum;
		this.threads = threads;
	}

	@Getter
	private final Forum forum;

	@Getter
	private final int pageNum;

	@Getter
	private final List<Thread> threads;
}
