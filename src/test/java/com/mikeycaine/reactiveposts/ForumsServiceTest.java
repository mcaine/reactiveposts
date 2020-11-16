package com.mikeycaine.reactiveposts;

import com.mikeycaine.reactiveposts.client.content.parsed.PostsPage;
import com.mikeycaine.reactiveposts.client.content.parsed.ThreadsIndex;
import com.mikeycaine.reactiveposts.model.Author;
import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Thread;
import com.mikeycaine.reactiveposts.service.ForumsService;
import com.mikeycaine.reactiveposts.service.PostCachingService;
import com.mikeycaine.reactiveposts.testdata.ForumIndexTestPage;
import com.mikeycaine.reactiveposts.testdata.ThreadTestPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
public class ForumsServiceTest {

	@Autowired
	PostCachingService postCachingService;

	@Autowired
	ForumsService forumsService;

	@BeforeEach
	public void makeSureForumsArePresent() throws InterruptedException {
		postCachingService.waitForForumsListToLoad();
	}

	@Test
	void testAuthorDetails() {
		int rupertButtermilkId = 114567;

		forumsService.clean();
		forumsService.reportCounts();

		Forum gbs = new Forum(273, "GBS");
		ThreadsIndex threadsIndexForGBS = ForumIndexTestPage.of(gbs, 1).cachedContent().parsed();

		forumsService.persistThreadsIndex(threadsIndexForGBS);

		Optional<Author> optRB = forumsService.findAuthorWithId(114567);
		assertTrue(optRB.isPresent());
		Author rb = optRB.get();
		assertTrue(rb.getTitleText() == null);
		assertTrue(rb.getTitleURL() == null);

		//forumsService.reportCounts();
		//forumsService.allAuthors().forEach(author -> log.info(author.toString()));

		int xmasThreadId = 3946206;
		Thread xmasThread = new Thread(xmasThreadId, " Let's turn this Annual Awful Xmas Thread up to 11!", gbs);
		PostsPage postsPage = ThreadTestPage.of(xmasThread, 1).cachedContent().parsed();

		forumsService.persistPostsPage(postsPage);

		Optional<Author> optRB2 = forumsService.findAuthorWithId(114567);
		assertTrue(optRB2.isPresent());
		Author rb2 = optRB2.get();
		assertTrue(rb2.getTitleText() != null);
		assertTrue(rb2.getTitleURL() != null);

		//forumsService.allAuthors().forEach(author -> log.info(author.toString()));
		//log.info("----------------------------------------------------");

		forumsService.persistThreadsIndex(threadsIndexForGBS);

		//forumsService.allAuthors().forEach(author -> log.info(author.toString()));

		Optional<Author> optRB3 = forumsService.findAuthorWithId(114567);
		assertTrue(optRB3.isPresent());
		Author rb3 = optRB3.get();
		assertTrue(rb3.getTitleText() != null);
		assertTrue(rb3.getTitleURL() != null);

		//forumsService.reportCounts();
		//forumsService.allPosts().forEach(post -> log.info(post.getAuthor().toString()));

		// CLEAN UP
		forumsService.clean();
	}

	@Test
	public void testThreadDetails() {

	}
}
