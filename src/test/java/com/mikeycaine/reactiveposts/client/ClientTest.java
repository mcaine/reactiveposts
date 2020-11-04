package com.mikeycaine.reactiveposts.client;

import com.mikeycaine.reactiveposts.client.content.ForumThreadsIndexContent;
import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.testdata.IndexPageSpec;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

@Slf4j
public class ClientTest extends ClientTestUtils {

	private final Client client = new ReactiveSAClient(new WebClientConfig().webClient());

	@Test
	public void testRetrieveForums() {
		StepVerifier.create(client.retrieveForums())
			.expectNextMatches(forum -> forum.getId() == 273
				&& forum.getName().equals("General Bullshit")
				&& forum.getSubForums().size() == 3)
			.expectNextCount(28L)
			.verifyComplete();
	}

	@Test
	public void testPrintForums() throws InterruptedException {
		logForumsFlux(client.retrieveForums());
	}

	@Test
	public void testRetrieveThreadsIndexForForum() {
		Forum gbs = new Forum(273, "General Bullshit");
		StepVerifier.create(client.retrieveThreads(gbs, 1))
			.expectNextCount(40L)
			.verifyComplete();
	}

	@Test
	public void testParseThreadsIndex() {
		Forum goonsWithSpoons = new Forum(161, "Goons with spoons");
		StepVerifier.create(
			IndexPageSpec.of(goonsWithSpoons, 4).cachedContentMono().flatMapMany(ForumThreadsIndexContent::parseToThreadsFlux)
		)
			.expectNextMatches(thread -> thread.getName().equals("ICSA 69: Breakfast Voting Thread"))
			.expectNextCount(29L) // this forum has 30 posts per index page for some reason???
			.verifyComplete();
	}

	@Test
	public void testParseThreadsIndex2() {
		Forum iyg = new Forum(192, "Inspect your gadgets");
		StepVerifier.create(
			IndexPageSpec.of(iyg, 5).cachedContentMono().flatMapMany(ForumThreadsIndexContent::parseToThreadsFlux)
		)
			.expectNextMatches(thread -> thread.getName().equals("Like-new Huawei Watch steel link, Android/iOS watch, near-perfect condition"))
			.expectNextCount(29L)  // this forum has 30 posts per index page for some reason???
			.verifyComplete();
	}

	@Test
	public void testParseThreadsIndex3() {
		Forum iyg = new Forum(273, "GBS");
		StepVerifier.create(
			IndexPageSpec.of(iyg, 6).cachedContentMono().flatMapMany(ForumThreadsIndexContent::parseToThreadsFlux)
		)
			.expectNextMatches(thread -> thread.getName().equals("Other times I don't make a thread"))
			.expectNextCount(39L)
			.verifyComplete();
	}
}
