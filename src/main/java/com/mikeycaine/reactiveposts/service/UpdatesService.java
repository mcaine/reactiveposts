package com.mikeycaine.reactiveposts.service;

import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.service.config.UpdatesConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdatesService /*implements InitializingBean */{

	private final ForumsService forumsService;
	private final UpdatesConfig config;

	private Optional<Disposable> threadUpdates = Optional.empty();
	private Optional<Disposable> postUpdates = Optional.empty();

	@EventListener(ApplicationReadyEvent.class)
	public void applicationReadyListener() {
		log.info("*********************************************************");
		log.info("UPDATES SERVICE CONFIG");
		log.info("threadsUpdateInterval = {}",      config.getThreadsUpdateInterval());
		log.info("threadsUpdateMaxRetries = {}",    config.getThreadsUpdateMaxRetries());
		log.info("postsUpdateInterval = {}",        config.getPostsUpdateInterval());
		log.info("postsUpdateMaxRetries = {}",      config.getPostsUpdateMaxRetries());
		log.info("*********************************************************");

		forumsService.updateForums().subscribe(
			mainForumIndex -> {
				log.info("There are {} main forums", mainForumIndex.getForums().stream().filter(Forum::isTopLevelForum).count());
				startUpdating();
			},
			t -> log.error("FAILED when getting list of forums: " + t.getMessage())
		);
	}

	void startThreadUpdates() {
		threadUpdates.ifPresent(Disposable::dispose);
		threadUpdates = Optional.of(
			runUpdates(
				forumsService::updateThreads,"threads",
				config.getThreadsUpdateInterval(),
				config.getThreadsUpdateMaxRetries()));
	}

	void startPostUpdates() {
		postUpdates.ifPresent(Disposable::dispose);
		postUpdates = Optional.of(
			runUpdates(
				forumsService::updatePosts,"posts",
				config.getPostsUpdateInterval(),
				config.getPostsUpdateMaxRetries()));
	}

	public void startUpdating() {
		startThreadUpdates();
		startPostUpdates();
	}

	public void stopUpdating() {
		threadUpdates.ifPresent(Disposable::dispose);
		postUpdates.ifPresent(Disposable::dispose);
	}

	Disposable runUpdates (Supplier<Flux<?>> supplier, String what, Duration interval, int maxRetries) {
		return Flux
			.interval(interval)
			.publishOn(Schedulers.elastic())
			.flatMapSequential(l -> {
				log.info("Updating {} [{}]", what,  l);
				return supplier.get();
			})
			.retry(maxRetries)
			.subscribe(
				item -> {},
				t -> log.error("FAILED when updating {}: {} ", what, t.getMessage())
			);
	}
}
