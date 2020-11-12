package com.mikeycaine.reactiveposts.service;

import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.service.config.UpdatesConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
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
public class UpdatesService implements InitializingBean {

	private final ForumsService forumsService;

	private Optional<Disposable> threadUpdates = Optional.empty();
	private Optional<Disposable> postUpdates = Optional.empty();

	private final UpdatesConfig config;

	private Duration threadsUpdateInitialDelay;
	private Duration threadsUpdateInterval;
	private int threadsUpdateMaxRetries;

	private Duration postsUpdateInitialDelay;
	private Duration postsUpdateInterval;
	private int postsUpdateMaxRetries;

	@Override
	public void afterPropertiesSet() {
		threadsUpdateInitialDelay = config.getThreadsUpdateInitialDelay();
		threadsUpdateInterval = config.getThreadsUpdateInterval();
		threadsUpdateMaxRetries = config.getThreadsUpdateMaxRetries();

		postsUpdateInitialDelay = config.getPostsUpdateInitialDelay();
		postsUpdateInterval = config.getPostsUpdateInterval();
		postsUpdateMaxRetries = config.getPostsUpdateMaxRetries();

		log.info("*********************************************************");
		log.info("UPDATES SERVICE CONFIG");
		log.info("threadsUpdateInitialDelay = {}", threadsUpdateInitialDelay);
		log.info("threadsUpdateInterval = {}", threadsUpdateInterval);
		log.info("threadsUpdateMaxRetries = {}", threadsUpdateMaxRetries);
		log.info("postsUpdateInitialDelay = {}", postsUpdateInitialDelay);
		log.info("postsUpdateInterval = {}", postsUpdateInterval);
		log.info("postsUpdateMaxRetries = {}", postsUpdateMaxRetries);
		log.info("*********************************************************");

	}

	public void startThreadUpdates() {
		threadUpdates.ifPresent(Disposable::dispose);
		threadUpdates = Optional.of(
			runUpdates(
				forumsService::updateThreads,"threads",
				threadsUpdateInitialDelay, threadsUpdateInterval, threadsUpdateMaxRetries)
		);
	}

	public void startPostUpdates() {
		postUpdates.ifPresent(Disposable::dispose);
		postUpdates = Optional.of(
			runUpdates(
				forumsService::updatePosts,"posts",
				postsUpdateInitialDelay, postsUpdateInterval, postsUpdateMaxRetries)
		);
	}

	public void startUpdating() {
		startThreadUpdates();
		startPostUpdates();
	}

	public void stopUpdating() {
		threadUpdates.ifPresent(Disposable::dispose);
		postUpdates.ifPresent(Disposable::dispose);
	}

	public void updateForums() {
		forumsService.updateForums().subscribe(
			mainForumIndex -> log.info("There are {} main forums", mainForumIndex.getForums().stream().filter(Forum::isTopLevelForum).count()),
			t -> log.error("FAILED when getting list of forums: " + t.getMessage())
		);
	}

	Disposable runUpdates (Supplier<Flux<?>> supplier, String what, Duration initialDelay, Duration interval, int maxRetries) {
		return Flux
			.interval(initialDelay, interval)
			.publishOn(Schedulers.elastic())
			.flatMapSequential(l -> {
				log.info("Updating {} [{}]", what,  l);
				return supplier.get();
			})
			.retry(maxRetries)
			.subscribe(
				post -> {},
				t -> log.error("FAILED when updating {}: {} ", what, t.getMessage())
			);
	}
}
