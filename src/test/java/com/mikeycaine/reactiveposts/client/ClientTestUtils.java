package com.mikeycaine.reactiveposts.client;

import com.mikeycaine.reactiveposts.model.Forum;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

@Slf4j
public class ClientTestUtils {

	protected Consumer<Throwable> errorHandler(CountDownLatch latch) {
		return t -> {
			log.error("Got an error " + t.getMessage());
			latch.countDown();
		};
	}

	protected Runnable signalCompleteTo(CountDownLatch latch) {
		return () -> {
			log.info("Signalling complete...");
			latch.countDown();
		};
	}

	protected void waitForLatch(CountDownLatch latch) throws InterruptedException {
		log.info("Waiting for latch");
		latch.await();
		log.info("Got latch");
	}

	protected Consumer<Forum> printForum() {
		return forum -> log.info("\n" + forum.prettyPrint());
	}
}
