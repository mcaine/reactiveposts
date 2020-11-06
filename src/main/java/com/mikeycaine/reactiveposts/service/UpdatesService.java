package com.mikeycaine.reactiveposts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdatesService {

	private final ForumsService forumsService;

	private final int MAX_CONCURRENCY = 1;

	private Optional<Disposable> threadUpdates = Optional.empty();
	private Optional<Disposable> postUpdates = Optional.empty();

	private Duration threadsUpdateInterval = Duration.ofSeconds(600);
	private Duration postsUpdateInterval = Duration.ofSeconds(60);

	public void startThreadUpdates() {
		threadUpdates.ifPresent(Disposable::dispose);
		threadUpdates = Optional.of(Flux.interval(threadsUpdateInterval).flatMapSequential(l -> Flux.defer(() -> {
			log.info("Updating threads [{}]", l);
			return forumsService.updateThreads();
		}), MAX_CONCURRENCY).subscribe(
			thread -> {},
			t -> log.error("FAILED when updating forum threads: " + t.getMessage())
		));
	}

	public void startPostUpdates() {
		postUpdates.ifPresent(Disposable::dispose);
		postUpdates = Optional.of(Flux.interval(postsUpdateInterval).flatMapSequential(l -> Flux.defer(() -> {
			log.info("Updating posts [{}]", l);
			return forumsService.updatePosts();
		}), MAX_CONCURRENCY).subscribe(
			post -> {},
			t -> log.error("FAILED when updating thread posts: " + t.getMessage())
		));
	}

	public void start() {
		startThreadUpdates();
		startPostUpdates();
	}

	public void updateForums() {
		forumsService.updateForums().collectList().subscribe(
			forums -> log.info("There are {} forums", forums.size()),
			t -> log.error("FAILED when getting list of forums: " + t.getMessage())
		);
	}
}
