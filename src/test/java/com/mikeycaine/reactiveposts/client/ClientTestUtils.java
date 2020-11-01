package com.mikeycaine.reactiveposts.client;

import com.mikeycaine.reactiveposts.model.Forum;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
public class ClientTestUtils {

	Consumer<Throwable> errorHandler(CountDownLatch latch) {
		return t -> {
			log.error("Got an error " + t.getMessage());
			latch.countDown();
		};
	}

	Runnable signalCompleteTo(CountDownLatch latch) {
		return () -> {
			log.info("Signalling complete...");
			latch.countDown();
		};
	}

	void waitForLatch(CountDownLatch latch) throws InterruptedException {
		log.info("Waiting for latch");
		latch.await();
		log.info("Got latch");
	}

	Consumer<Forum> printForum() {
		return forum -> log.info("\n" + forum.prettyPrint());
	}

	void logForumsFlux(Flux<Forum> forumFlux) {
		CountDownLatch finishedSignal = new CountDownLatch(1);

		forumFlux
			.subscribe(printForum(), errorHandler(finishedSignal), signalCompleteTo(finishedSignal));

		try {
			waitForLatch(finishedSignal);
		} catch (InterruptedException e) {
			fail();
		}

	}


}
