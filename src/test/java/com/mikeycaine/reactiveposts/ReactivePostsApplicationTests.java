package com.mikeycaine.reactiveposts;

import com.mikeycaine.reactiveposts.model.*;
import com.mikeycaine.reactiveposts.model.Thread;
import com.mikeycaine.reactiveposts.repos.AuthorRepository;
import com.mikeycaine.reactiveposts.repos.ForumRepository;
import com.mikeycaine.reactiveposts.repos.PostRepository;
import com.mikeycaine.reactiveposts.repos.ThreadRepository;
import com.mikeycaine.reactiveposts.service.ForumsService;
import com.mikeycaine.reactiveposts.service.PostCachingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
class ReactivePostsApplicationTests  {

	final int CSPAM_FORUM_ID = 269;

	@Autowired
	ForumRepository forumRepository;

	@Autowired
	ThreadRepository threadRepository;

	@Autowired
	PostRepository postRepository;

	@Autowired
	AuthorRepository authorRepository;

	@Autowired
	ForumsService forumsService;

	@Autowired
	PostCachingService postCachingService;

	@Test
	void contextLoads() {
	}

	@Test
	void testIt() throws Exception {
		postCachingService.waitForForumsListToLoad();

		log.info("There are " + forumsService.getForumsCount() + " forums");

		Forum cspam = forumsService.findForumById(CSPAM_FORUM_ID);

		forumsService.updateForumSubscriptionStatus(CSPAM_FORUM_ID, true);

		StepVerifier.create(postCachingService.updateThreads())
			.expectNextCount(2)  // update gets 2 pages by default
			.verifyComplete();

		assertTrue(authorRepository.count() > 0);

		List<Thread> threads = threadRepository.findAll();
		assertTrue(threads.size() == 80);

		final Thread thread = threads.get(0);
		log.info("found a thread " + thread);

		forumsService.updateThreadSubscriptionStatus(thread.getId(), true);

		StepVerifier.create(postCachingService.updatePosts())
			.consumeNextWith(post -> log.info("Heres a posts page: " + post))
			.verifyComplete();

		assertTrue(40 == postRepository.count());

		assertTrue(authorRepository.count() > 0);
	}
}
