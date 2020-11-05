package com.mikeycaine.reactiveposts.init;

import com.mikeycaine.reactiveposts.client.Client;
import com.mikeycaine.reactiveposts.model.*;
import com.mikeycaine.reactiveposts.model.Thread;
import com.mikeycaine.reactiveposts.repos.ForumRepository;
import com.mikeycaine.reactiveposts.repos.PostRepository;
import com.mikeycaine.reactiveposts.repos.ThreadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

//@Profile("initialise")
@Slf4j
@Component
@RequiredArgsConstructor
public class ForumsInitialiser implements ApplicationListener<ApplicationReadyEvent> {
	private final ForumRepository forumRepository;
	private final PostRepository postRepository;
	private final ThreadRepository threadRepository;
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

		List<Thread> cspamThreads = client.retrieveThreads(cspam, 1, 13).collectList().block();
		cspamThreads.forEach(thread -> {
			threadRepository.save(thread);
		});

		Thread someThread = cspamThreads.get(0);
		List<Post> posts = client.retrievePosts(someThread, 1, 3).collectList().block();
		posts.forEach(post ->  {
			postRepository.save(post);
		});
	}
}

