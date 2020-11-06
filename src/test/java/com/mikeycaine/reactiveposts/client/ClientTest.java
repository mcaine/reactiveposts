package com.mikeycaine.reactiveposts.client;

import com.mikeycaine.reactiveposts.client.content.ThreadsIndexContent;
import com.mikeycaine.reactiveposts.client.content.PostsPageContent;
import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Thread;
import com.mikeycaine.reactiveposts.testdata.IndexPageSpec;
import com.mikeycaine.reactiveposts.testdata.ThreadPageSpec;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

@Slf4j
public class ClientTest extends ClientTestUtils {

	private final Client client = new ReactiveSAClient(new WebClientConfig().webClient());

	@Test
	public void testRetrieveForums() {
		StepVerifier.create(client.retrieveMainForumIndex())
			.expectNextMatches(mainForumIndex -> mainForumIndex.getForums().size() == 29)
			.verifyComplete();
	}

	@Test
	public void testPrintForums() {
		logMainForumsIndex(client.retrieveMainForumIndex());
	}

	@Test
	public void testRetrieveThreadsIndexForForum() {
		Forum gbs = new Forum(273, "General Bullshit");
		StepVerifier.create(client.retrieveThreads(gbs, 1))
			.expectNextCount(1L)
			.verifyComplete();
	}

	@Test
	public void testRetrieveThreadsIndexForForumMultiplePages() {
		Forum gbs = new Forum(273, "General Bullshit");
		StepVerifier.create(client.retrieveThreads(gbs, 1, 3))
			.expectNextCount(3L)
			.verifyComplete();
	}

	@Test
	public void testParseThreadsIndex() {
		Forum goonsWithSpoons = new Forum(161, "Goons with spoons");
		StepVerifier.create(
			IndexPageSpec.of(goonsWithSpoons, 4).cachedContentMono().flatMapMany(ThreadsIndexContent::parseToThreadsFlux)
		)
			.expectNextMatches(thread -> thread.getName().equals("ICSA 69: Breakfast Voting Thread"))
			.expectNextCount(29L) // this forum has 30 posts per index page for some reason???
			.verifyComplete();
	}

	@Test
	public void testParseThreadsIndex2() {
		Forum iyg = new Forum(192, "Inspect your gadgets");
		StepVerifier.create(
			IndexPageSpec.of(iyg, 5).cachedContentMono().flatMapMany(ThreadsIndexContent::parseToThreadsFlux)
		)
			.expectNextMatches(thread -> thread.getName().equals("Like-new Huawei Watch steel link, Android/iOS watch, near-perfect condition"))
			.expectNextCount(29L)  // this forum has 30 posts per index page for some reason???
			.verifyComplete();
	}

	@Test
	public void testParseThreadsIndex3() {
		Forum iyg = new Forum(273, "GBS");
		StepVerifier.create(
			IndexPageSpec.of(iyg, 6).cachedContentMono().flatMapMany(ThreadsIndexContent::parseToThreadsFlux)
		)
			.expectNextMatches(thread -> thread.getName().equals("Other times I don't make a thread"))
			.expectNextCount(39L)
			.verifyComplete();
	}

	@Test
	public void testParsePostsPage() {
		Thread trumo = Thread.withId(3942499);
		StepVerifier.create(
			ThreadPageSpec.of(trumo, 1).cachedContentMono().flatMapMany(PostsPageContent::parseToPostsFlux)
		)
			.expectNextMatches(post -> post.getAuthor().getName().equals("Korean Boomhauer") && post.getId() == 508526226)
			.expectNextCount(39L)
			.verifyComplete();
	}

	@Test
	public void testParsePostsPage2() {
		Thread thread = Thread.withId(3913301);
		StepVerifier.create(
			ThreadPageSpec.of(thread, 10).cachedContentMono().flatMapMany(PostsPageContent::parseToPostsFlux)
		)
			.expectNextMatches(post -> post.getAuthor().getName().equals("oh but seriously I") && post.getId() == 507178153)
			.expectNextCount(39L)
			.verifyComplete();
	}
}
