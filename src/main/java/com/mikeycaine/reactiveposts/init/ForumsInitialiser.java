package com.mikeycaine.reactiveposts.init;

import com.mikeycaine.reactiveposts.service.ForumsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ForumsInitialiser implements ApplicationListener<ApplicationReadyEvent> {
	private final ForumsService forumsService;

	private final int MAX_CONCURRENCY = 1;

	private Optional<Disposable> threadUpdates = Optional.empty();
	private Optional<Disposable> postUpdates = Optional.empty();

	private Duration threadUpdateInterval = Duration.ofSeconds(10);
	private Duration postUpdateInterval = Duration.ofSeconds(5);

	@Override
	public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
		forumsService.updateForums().subscribe();
		start();
	}

	public void start() {
		threadUpdates.ifPresent(Disposable::dispose);
		postUpdates.ifPresent(Disposable::dispose);

		threadUpdates = Optional.of(Flux.interval(threadUpdateInterval).flatMapSequential(l -> Flux.defer(() -> {
			log.info("Updating threads [{}]", l);
			return forumsService.updateThreads();
		}), MAX_CONCURRENCY).subscribe(
			thread -> {},
			t -> log.error("FAILED when updating threads " + t.getMessage())
		));

		postUpdates = Optional.of(Flux.interval(postUpdateInterval).flatMapSequential(l -> Flux.defer(() -> {
			log.info("Updating posts [{}]", l);
			return forumsService.updatePosts();
		}), MAX_CONCURRENCY).subscribe(
			post -> {},
			t -> log.error("FAILED when updating posts " + t.getMessage())
		));
	}
}

