package com.mikeycaine.reactiveposts.client;

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
}
