package com.mikeycaine.reactiveposts.service;

import com.mikeycaine.reactiveposts.client.content.parsed.ThreadsIndex;
import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Post;
import com.mikeycaine.reactiveposts.model.Thread;
import com.mikeycaine.reactiveposts.repos.AuthorRepository;
import com.mikeycaine.reactiveposts.repos.ForumRepository;
import com.mikeycaine.reactiveposts.repos.PostRepository;
import com.mikeycaine.reactiveposts.repos.ThreadRepository;
import com.mikeycaine.reactiveposts.webapi.ForumNotFoundException;
import com.mikeycaine.reactiveposts.webapi.PostNotFoundException;
import com.mikeycaine.reactiveposts.webapi.ThreadNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.transaction.Transactional;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WebApiService {
	private final ForumRepository forumRepository;
	private final PostRepository postRepository;
	private final ThreadRepository threadRepository;
	private final AuthorRepository authorRepository;
	private final ForumsService forumsService;

	public List<Forum> topLevelForums() {
		return forumRepository.topLevelForums();
	}

	public Forum updateForumSubscriptionStatus(int forumId, boolean newStatus) {
		log.info("Updating forum subscription status forum={} newStatus={}", forumId, newStatus);
		int result = forumRepository.updateForumSubscriptionStatus(forumId, newStatus);
		Optional<Forum> optForum = forumRepository.findById(forumId);
		if (result == 1) {
			log.info("...updated forum subscription status for forum={} to new status {}", forumId, newStatus);
			if (newStatus) {
				optForum.ifPresent(forum -> retrieveThreadsForForum(forum).subscribe(threadsIndex -> threadsIndex.getThreads().forEach(forumsService::mergeThreadInfo)));
			}
		} else {
			log.error("FAILED to update forum subscription status for forum={}", forumId);
		}

		return optForum.orElseThrow(() -> new ForumNotFoundException(forumId));
	}

	public List<Thread> threadsForForum(int forumId) {
		return forumRepository.findById(forumId)
				.map(threadRepository::threadsForForum)
				.orElseGet(() -> Collections.emptyList());
	}

	public Flux<ThreadsIndex> retrieveThreadsForForum(Forum forum) {
		return forumsService.retrieveThreadsForForum(forum);
	}

	public Thread updateThreadSubscriptionStatus(int threadId, boolean newStatus) {
		log.info("Updating thread subscription status forum={} newStatus={}", threadId, newStatus);
		int result = threadRepository.updateThreadSubscriptionStatus(threadId, newStatus);
		log.info("Got result {}", result);
		Optional<Thread> thread = threadRepository.findById(threadId);
		return thread.orElseThrow(() -> new ThreadNotFoundException(threadId));
	}

	public List<Post> postsForThreadPage(int threadId, int pageId) {
		return threadRepository.findById(threadId)
			.map(thread -> postRepository.getPostsForThreadPage(thread, pageId))
			.orElseThrow(() -> new ThreadNotFoundException(threadId));
	}

	public URI fixPostURL(int postId) {
		return postRepository.findById(postId).map(post -> {
			int threadId = post.getThread().getId();
			int pageNum = post.getPageNum();
			try {
				URI uri = new URI(String.format("/thread/%d/page/%d?foo=bar#post%d", threadId, pageNum, postId));
				log.info("URI for postId " + postId + " is " + uri);
				return uri;
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}}).orElseThrow(()-> new PostNotFoundException(postId));
	}
}
