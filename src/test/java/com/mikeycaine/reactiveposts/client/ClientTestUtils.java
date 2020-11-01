package com.mikeycaine.reactiveposts.client;

import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Post;
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

	Consumer<Forum> printForum() {
		return forum -> log.info("\n" + forum.prettyPrint());
	}
	Consumer<Post> printPost() {
		return post -> log.info("\n" + post);
	}

	void logForumsFlux(Flux<Forum> forumFlux) {
		CountDownLatch finishedSignal = new CountDownLatch(1);

		forumFlux
			.subscribe(printForum(), errorHandler(finishedSignal), signalCompleteTo(finishedSignal));

		try {
			finishedSignal.await();
		} catch (InterruptedException e) {
			fail();
		}

	}

	public void logPostsFlux(Flux<Post> postsFlux) {
		CountDownLatch finishedSignal = new CountDownLatch(1);

		postsFlux
			.subscribe(printPost(), errorHandler(finishedSignal), signalCompleteTo(finishedSignal));

		try {
			finishedSignal.await();
		} catch (InterruptedException e) {
			fail();
		}

	}


}
