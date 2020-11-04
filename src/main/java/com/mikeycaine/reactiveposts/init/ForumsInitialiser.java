package com.mikeycaine.reactiveposts.init;

import com.mikeycaine.reactiveposts.client.Client;
import com.mikeycaine.reactiveposts.model.*;
import com.mikeycaine.reactiveposts.model.Thread;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

//@Profile("initialise")
@Slf4j
@Component
@RequiredArgsConstructor
public class ForumsInitialiser implements ApplicationListener<ApplicationReadyEvent> {
	private final ForumRepository forumRepository;
	private final PostRepository postRepository;
	private final ThreadRepository threadRepository;
	private final AuthorRepository authorRepository;
	private final Client client;

	@Override
	public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
		log.info("Getting list of Forums...");
		/** Block here since we want to make sure this gets done before anything else */
		List<Forum> forums = client.retrieveForums().collectList().block();
		forums.forEach(forum -> {
			forum.getSubForums().forEach(forumRepository::save);
			forumRepository.save(forum);
		});

		Forum cspam = forumRepository.findById(269).get();

		List<Thread> cspamThreads = client.retrieveThreads(cspam, 1, 3).collectList().block();
		cspamThreads.forEach(thread -> {
			authorRepository.save(thread.getAuthor());
			threadRepository.save(thread);
		});

		Thread someThread = cspamThreads.get(0);
		List<Post> posts = client.retrievePosts(someThread, 1, 2).collectList().block();
		posts.forEach(post ->  {
			authorRepository.save(post.getAuthor());
			postRepository.save(post);
		});
	}
}

