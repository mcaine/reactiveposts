package com.mikeycaine.reactiveposts;

import com.mikeycaine.reactiveposts.client.Client;
import com.mikeycaine.reactiveposts.client.ClientTestUtils;
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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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

	@Autowired
	ForumsService forumsService;

	@Test
	void contextLoads() {
	}

	@Test
	void testIt() throws Exception {
		CompletableFuture<Void> x = forumsService.updateForums().thenApplyAsync(v -> {
				Optional<Forum> optCspam = forumRepository.findById(CSPAM_FORUM_ID);
				assertTrue(optCspam.isPresent(), "C-SPAM is missing WTF");
				return optCspam.get();
			}).thenAccept(cspam -> {
				forumsService.subscribeToForum(cspam);
			}).thenRun(() -> {
				forumsService.updateThreads();

				List<Thread> threads = threadRepository.findAll();
				assertTrue(threads.size() > 0);

				final Thread thread = threads.get(0);
				log.info("found a thread " + thread);

				forumsService.subscribeToThread(thread);
				forumsService.updatePosts();

				assertTrue(postRepository.count() > 0);
			});

		x.get(20, TimeUnit.SECONDS);
	}

//	@Test
//	void testCspamExists() {
//		Optional<Forum> cspam = forumRepository.findById(CSPAM_FORUM_ID);
//		assertTrue(cspam.isPresent(), "C-SPAM is missing WTF");
//	}

//	@Test
//	void test2() {
//		final int THREAD_ID = 3942499;
//		final int PAGES_TO_GET = 2;
//
//		Optional<Forum> cspam = forumRepository.findById(CSPAM_FORUM_ID);
//		if (cspam.isPresent()) {
//			List<Thread> threadList = client.retrieveThreads(cspam.get(), 1).collectList().block();
//			Thread thread = threadList.get(0);
//			int latestPage = thread.getMaxPageNumber();
//			log.info("Latest page for " + thread + " is " + latestPage);
//			int pageToStart = Math.max(1, latestPage - PAGES_TO_GET + 1);
//
//			Flux<Post> postFlux = client.retrievePosts(thread, pageToStart, latestPage);
//			logPostsFlux(postFlux);
//
//
//		} else {
//			fail();
//		}
//	}
}
