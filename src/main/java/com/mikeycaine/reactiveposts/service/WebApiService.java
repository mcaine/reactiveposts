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
		return forumsService.updateForumSubscriptionStatus(forumId, newStatus);
	}

	public Thread updateThreadSubscriptionStatus(int threadId, boolean newStatus) {
		return forumsService.updateThreadSubscriptionStatus(threadId, newStatus);
	}

	public List<Thread> threadsForForum(int forumId) {
		return forumRepository.findById(forumId)
			.map(threadRepository::threadsForForum)
			.orElseThrow(() -> new ForumNotFoundException(forumId));
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
