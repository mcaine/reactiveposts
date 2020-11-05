package com.mikeycaine.reactiveposts.init;

import com.mikeycaine.reactiveposts.service.ForumsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;

//@Profile("initialise")
@Slf4j
@Component
@RequiredArgsConstructor
public class ForumsInitialiser implements ApplicationListener<ApplicationReadyEvent> {

	final ForumsService forumsService;

	@Override
	public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
		forumsService.updateForums();

		Flux.interval(Duration.ofSeconds(10)).flatMap( l -> {
			log.info("Update threads [{}]", l);
			return forumsService.updateThreads();
		}).subscribe(System.out::println);

		Flux.interval(Duration.ofSeconds(10)).flatMap(l -> {
			log.info("Update posts [{}]", l);
			return forumsService.updatePosts();
		}).subscribe(System.out::println);


//		Forum cspam = forumRepository.findById(269).get();
//
//		List<Thread> cspamThreads = client.retrieveThreads(cspam, 1, 13).collectList().block();
//		cspamThreads.forEach(threadRepository::save);
//
//		Thread someThread = cspamThreads.get(0);
//		List<Post> posts = client.retrievePosts(someThread, 1, 3).collectList().block();
//		posts.forEach(postRepository::save);
	}
}

