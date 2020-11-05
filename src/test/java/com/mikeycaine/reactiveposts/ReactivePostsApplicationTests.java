package com.mikeycaine.reactiveposts;

import com.mikeycaine.reactiveposts.model.*;
import com.mikeycaine.reactiveposts.model.Thread;
import com.mikeycaine.reactiveposts.repos.AuthorRepository;
import com.mikeycaine.reactiveposts.repos.ForumRepository;
import com.mikeycaine.reactiveposts.repos.PostRepository;
import com.mikeycaine.reactiveposts.repos.ThreadRepository;
import com.mikeycaine.reactiveposts.service.ForumsService;
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

	@Test
	void contextLoads() {
	}

	@Test
	void testIt() throws Exception {
		StepVerifier.create(forumsService.updateForums())
			.expectNextCount(29)
			.verifyComplete();

		Optional<Forum> optCspam = forumRepository.findById(CSPAM_FORUM_ID);
		assertTrue(optCspam.isPresent(), "C-SPAM is missing WTF");

		Forum cspam = optCspam.get();
		forumsService.subscribeToForum(cspam);

		StepVerifier.create(forumsService.updateThreads())
			.expectNextCount(40)
			.verifyComplete();

		List<Thread> threads = threadRepository.findAll();
		assertTrue(threads.size() == 40);

		final Thread thread = threads.get(0);
		log.info("found a thread " + thread);

		forumsService.subscribeToThread(thread);

		StepVerifier.create(forumsService.updatePosts())
			.consumeNextWith(post -> log.info("Heres a post: " + post))
			.expectNextCount(39)
			.verifyComplete();

		assertTrue(40 == postRepository.count());

		assertTrue(authorRepository.count() > 0);
	}
}
