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

	private Optional<Disposable> threadUpdates = Optional.empty();
	private Optional<Disposable> postUpdates = Optional.empty();

	private Duration threadUpdateInterval = Duration.ofMinutes(5);
	private Duration postUpdateInterval = Duration.ofSeconds(60);

	@Override
	public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
		forumsService.updateForums();
		start();
	}

	public void start() {
		threadUpdates.ifPresent(Disposable::dispose);
		postUpdates.ifPresent(Disposable::dispose);

		threadUpdates = Optional.of(Flux.interval(threadUpdateInterval).flatMap(l -> {
			log.info("Update threads [{}]", l);
			return forumsService.updateThreads();
		}).subscribe());

		postUpdates = Optional.of(Flux.interval(postUpdateInterval).flatMap(l -> {
			log.info("Update posts [{}]", l);
			return forumsService.updatePosts();
		}).subscribe());
	}
}

