package com.mikeycaine.reactiveposts;

import com.mikeycaine.reactiveposts.client.Client;
import com.mikeycaine.reactiveposts.model.*;
import com.mikeycaine.reactiveposts.model.Thread;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Slf4j
class ReactivePostsApplicationTests {

	final int CSPAM_FORUM_ID = 269;

	@Autowired
	ForumRepository forumRepository;

	@Autowired
	ThreadRepository threadRepository;

	@Autowired
	PostRepository postRepository;

	@Autowired
	Client client;

	@Test
	void contextLoads() {
	}

	@Test
	void testCspamExists() {
		Optional<Forum> cspam = forumRepository.findById(CSPAM_FORUM_ID);
		Assertions.assertTrue(cspam.isPresent(), "C-SPAM is missing WTF");
	}

	@Test
	void test2() {
		final int THREAD_ID = 3942499;
		final int PAGES_TO_GET = 2;

		Optional<Forum> cspam = forumRepository.findById(CSPAM_FORUM_ID);
		if (cspam.isPresent()) {
			List<Thread> threadList = client.retrieveThreads(cspam.get(), 1).collectList().block();
			Thread thread = threadList.get(0);
			int latestPageId = client.latestPageId(thread).block();
			log.info("Latest page for " + thread + " is " + latestPageId);
		} else {
			fail();
		}




//
//
//		Thread thread = new Thread();
//		thread.setId(THREAD_ID);
//		thread.setName("Oct2020 trumo (INIT DATA)");
//		thread.setForum(forum);
//		forum.getThreads().add(thread);
//
//		forumRepository.save(forum);
//		threadRepository.save(thread);
//
//		log.info("Reading the last {} pages of thread {}...", PAGES_TO_GET, THREAD_ID);
//
//		AtomicInteger savedCount = new AtomicInteger(0);
//
//		forumRepository.deleteAll();
//
//		client.latestPageId(thread).flatMapMany(latestPageId -> {
//			log.info("Latest page of thread {} is {}", THREAD_ID, latestPageId);
//			int pageToStart = Math.max(1, latestPageId - PAGES_TO_GET + 1);
//			return client.retrievePosts(thread, pageToStart, latestPageId);
//		}).subscribe(postRepository::save);
	}

}
