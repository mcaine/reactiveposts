package com.mikeycaine.reactiveposts.client;

import com.mikeycaine.reactiveposts.client.content.parsed.MainForumIndex;
import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Post;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

	Consumer<MainForumIndex> printMainForumIndex() {
		return mainForumIndex ->
			log.info(mainForumIndex
						.getForums().stream()
						.map(Forum::prettyPrint)
						.collect(Collectors.joining("\n")));
	}

	Consumer<Post> printPost() {
		return post -> log.info("\n" + post);
	}

	public void logMainForumsIndex(Mono<MainForumIndex> mainForumIndexMono) {
		CountDownLatch finishedSignal = new CountDownLatch(1);

		mainForumIndexMono
			.subscribe(printMainForumIndex(), errorHandler(finishedSignal), signalCompleteTo(finishedSignal));

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
