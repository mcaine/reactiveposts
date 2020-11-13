package com.mikeycaine.reactiveposts.service;

import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Post;
import com.mikeycaine.reactiveposts.model.Thread;
import com.mikeycaine.reactiveposts.repos.AuthorRepository;
import com.mikeycaine.reactiveposts.repos.ForumRepository;
import com.mikeycaine.reactiveposts.repos.PostRepository;
import com.mikeycaine.reactiveposts.repos.ThreadRepository;
import com.mikeycaine.reactiveposts.webapi.ForumNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

	public List<Forum> topLevelForums() {
		return forumRepository.topLevelForums();
	}

	public Forum updateForumSubscriptionStatus(int forumId, boolean newStatus) {
		log.info("Updating forum subscription status forum={} newStatus={}", forumId, newStatus);
		int result = forumRepository.updateForumSubscriptionStatus(forumId, newStatus);
		if (result == 1) {
			log.info("...updated forum subscription status for forum={} to new status {}", forumId, newStatus);
		} else {
			log.error("FAILED to update forum subscription status for forum={}", forumId);
		}

		Optional<Forum> forum = forumRepository.findById(forumId);
		return forum.orElseThrow(() -> new ForumNotFoundException(forumId));
	}

	public List<Thread> threadsForForum(int forumId) {
		return forumRepository.findById(forumId)
				.map(forum -> threadRepository.threadsForForum(forum))
				.get();
	}

	public Thread updateThreadSubscriptionStatus(int threadId, boolean newStatus) {
		log.info("Updating thread subscription status forum={} newStatus={}", threadId, newStatus);
		int result = threadRepository.updateThreadSubscriptionStatus(threadId, newStatus);
		log.info("Got result {}", result);
		Optional<Thread> thread = threadRepository.findById(threadId);

		// TODO handle the case where thread isnt found
		return thread.get();
	}

	public List<Post> postsForThreadPage(int threadId, int pageId) {
		return threadRepository.findById(threadId)
			.map(thread -> postRepository.getPostsForThreadPage(thread, pageId)).get();

	}
}
