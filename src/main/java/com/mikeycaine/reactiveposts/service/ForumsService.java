package com.mikeycaine.reactiveposts.service;

import com.mikeycaine.reactiveposts.client.Client;
import com.mikeycaine.reactiveposts.client.content.parsed.MainForumIndex;
import com.mikeycaine.reactiveposts.client.content.parsed.PostsPage;
import com.mikeycaine.reactiveposts.client.content.parsed.ThreadsIndex;
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
import reactor.core.publisher.Mono;

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

	final int MAX_CONCURRENCY = 1;

	public Mono<MainForumIndex> updateForums() {
		if (forumRepository.count() == 0) {
			log.info("No forums found, initialising...");
			Mono<MainForumIndex> mainForumIndexMono = client.retrieveMainForumIndex();
			return mainForumIndexMono.doOnNext(mainForumIndex -> forumRepository.saveAll(mainForumIndex.getForums()));
		} else {
			return Mono.just(new MainForumIndex(forumRepository.findAll()));
		}
	}

	public void reportForumCount() {
		log.info(forumRepository.count() + " forums");
	}

	public Flux<ThreadsIndex> updateThreads() {
		return Flux.fromIterable(forumRepository.subscribedForums())
			.flatMapSequential(forum -> client.retrieveThreads(forum, 1, 1), MAX_CONCURRENCY)
			.doOnNext(threadsIndex -> {
				threadRepository.saveAll(threadsIndex.getThreads());
			});
	}

	public Flux<PostsPage> updatePosts() {
		return Flux.fromIterable(threadRepository.subscribedThreads())
			.flatMapSequential(thread -> {
				log.info("I'm subscribed to " + thread);
				if (thread.getPagesGot() < thread.getMaxPageNumber()) {
					final int nextPage = thread.getPagesGot() + 1;
					return client.retrievePosts(thread, nextPage)
						.doOnNext(postsPage -> {
							postRepository.saveAll(postsPage.getPosts());
							thread.setPagesGot(postsPage.getMaxPageNum());
							threadRepository.save(postsPage.getThread());
						});
				} else {
					return Mono.empty();
				}
			}, MAX_CONCURRENCY);
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
