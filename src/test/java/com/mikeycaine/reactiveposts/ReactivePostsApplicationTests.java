package com.mikeycaine.reactiveposts;

import com.mikeycaine.reactiveposts.client.Client;
import com.mikeycaine.reactiveposts.client.ClientTestUtils;
import com.mikeycaine.reactiveposts.model.*;
import com.mikeycaine.reactiveposts.model.Thread;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Slf4j
class ReactivePostsApplicationTests extends ClientTestUtils  {

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
	Client client;

	@Test
	void contextLoads() {
	}

	@Test
	void testCspamExists() {
		Optional<Forum> cspam = forumRepository.findById(CSPAM_FORUM_ID);
		assertTrue(cspam.isPresent(), "C-SPAM is missing WTF");
	}

	@Test
	void test2() {
		final int THREAD_ID = 3942499;
		final int PAGES_TO_GET = 2;

		Optional<Forum> cspam = forumRepository.findById(CSPAM_FORUM_ID);
		if (cspam.isPresent()) {
			List<Thread> threadList = client.retrieveThreads(cspam.get(), 1).collectList().block();
			Thread thread = threadList.get(0);
			int latestPage = thread.getMaxPageNumber();
			log.info("Latest page for " + thread + " is " + latestPage);
			int pageToStart = Math.max(1, latestPage - PAGES_TO_GET + 1);

			Flux<Post> postFlux = client.retrievePosts(thread, pageToStart, latestPage);
			logPostsFlux(postFlux);


		} else {
			fail();
		}
	}
}
