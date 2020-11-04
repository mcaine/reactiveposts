package com.mikeycaine.reactiveposts.client;

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

//	@Test
//	public void testParseThreadsIndex() {
//		IndexPageSpec.of(273, 3).cachedContentMono().flatMapMany(content -> parseToThreadsFlux(content, forum, pageId));
//	}
}
