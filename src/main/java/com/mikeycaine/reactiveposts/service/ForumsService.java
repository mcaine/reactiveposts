package com.mikeycaine.reactiveposts.service;

import com.mikeycaine.reactiveposts.client.Client;
import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Post;
import com.mikeycaine.reactiveposts.model.Thread;
import com.mikeycaine.reactiveposts.repos.ForumRepository;
import com.mikeycaine.reactiveposts.repos.PostRepository;
import com.mikeycaine.reactiveposts.repos.ThreadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.transaction.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ForumsService {
	private final ForumRepository forumRepository;
	private final PostRepository postRepository;
	private final ThreadRepository threadRepository;
	private final Client client;

	public Flux<Forum> updateForums() {
		if (forumRepository.count() == 0) {
			log.info("No forums found, initialising...");
			return client.retrieveForums().doOnNext(forumRepository::save);
		} else {
			return Flux.fromIterable(forumRepository.findAll());
		}
	}

	private void reportForumCount() {
		log.info(forumRepository.count() + " forums");
	}

	public Flux<Thread> updateThreads() {
		return Flux.fromIterable(forumRepository.subscribedForums())
			.flatMapSequential(forum -> client.retrieveThreads(forum, 1))
			.doOnNext(this::mergeThreadInfo);
	}

	public Flux<Post> updatePosts() {
		return Flux.fromIterable(threadRepository.subscribedThreads())
			.flatMapSequential(thread -> {
				log.info("I'm subscribed to " + thread);
				if (thread.getPagesGot() < thread.getMaxPageNumber()) {
					final int nextPage = thread.getPagesGot() + 1;
					return client.retrievePosts(thread, nextPage)
						.doOnNext(postRepository::save)
						.doOnComplete(() -> {
							thread.setPagesGot(nextPage);
							threadRepository.save(thread);
					});

				} else {
					return Flux.<Post>empty();
				}
			});
	}

	public Thread mergeThreadInfo(Thread thread) {
		final Optional<Thread> optDBThread = threadRepository.findById(thread.getId());
		if (optDBThread.isPresent()) {
			final Thread dbThread = optDBThread.get();
			dbThread.setMaxPageNumber(thread.getMaxPageNumber());
			dbThread.setName(thread.getName());
			return threadRepository.save(dbThread);
		} else {
			return threadRepository.save(thread);
		}
	}

	public void subscribeToForum(Forum forum) {
		updateForumSubscriptionStatus(forum, true);
	}

	public void unSubscribeFromForum(Forum forum) {
		updateForumSubscriptionStatus(forum, false);
	}

	public void subscribeToThread(Thread thread) {
		updateThreadSubscriptionStatus(thread, true);
	}

	public void unSubscribeFromThread(Thread thread) {
		updateThreadSubscriptionStatus(thread, false);
	}

	private void updateThreadSubscriptionStatus(Thread thread, boolean state) {
		threadRepository.findById(thread.getId()).ifPresent(dbThread -> {
			log.info("Subscribing to thread " + dbThread);
			dbThread.setSubscribed(state);
			threadRepository.save(dbThread);
		});
	}

	private void updateForumSubscriptionStatus(Forum forum, boolean state) {
		forumRepository.findById(forum.getId()).ifPresent(dbForum -> {
			log.info("Subscribing to forum " + dbForum);
			dbForum.setSubscribed(state);
			forumRepository.save(dbForum);
		});
	}
}
