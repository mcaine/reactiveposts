package com.mikeycaine.reactiveposts.service;

import com.mikeycaine.reactiveposts.client.Client;
import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Thread;
import com.mikeycaine.reactiveposts.repos.ForumRepository;
import com.mikeycaine.reactiveposts.repos.PostRepository;
import com.mikeycaine.reactiveposts.repos.ThreadRepository;
import jdk.jshell.SourceCodeAnalysis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ForumsService {
	private final ForumRepository forumRepository;
	private final PostRepository postRepository;
	private final ThreadRepository threadRepository;
	private final Client client;

	public CompletableFuture<Void> updateForums() {
		return CompletableFuture.runAsync(() -> {
			if (forumRepository.count() == 0) {
				CountDownLatch latch = new CountDownLatch(1);
				log.info("No forums found, initialising...");
				client.retrieveForums().subscribe(
					forumRepository::save,
					t -> {
						log.error(t.getMessage());
						latch.countDown();
					},
					() -> {
						reportForumCount();
						latch.countDown();
					}
				);

				try {
					latch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				reportForumCount();
			}
		});
	}

	private void reportForumCount() {
		log.info(forumRepository.count() + " forums");
	}

	public void updateThreads() {
		forumRepository.subscribedForums().forEach(forum -> {
			log.info("i'm subscribed to " + forum);
			client.retrieveThreads(forum, 1).subscribe(this::mergeThreadInfo);
		});
	}

	public void updatePosts() {
		threadRepository.subscribedThreads().forEach(thread -> {
			log.info("im subscribed to " + thread);

			AtomicInteger postsGot = new AtomicInteger(0);

			if (thread.getPagesGot() < thread.getMaxPageNumber()) {
				final int nextPage = thread.getPagesGot() + 1;
				client.retrievePosts(thread, nextPage).subscribe(
					post -> {
						postRepository.save(post);
						postsGot.incrementAndGet();
					},
					t -> log.error(t.getMessage()),
					() -> {
						log.info("Got " + postsGot + " from page " + nextPage + " of " + thread);
						thread.setPagesGot(nextPage);
						threadRepository.save(thread);
					}
				);
			}
		});
	}

	public void mergeThreadInfo(Thread thread) {
		final Optional<Thread> optDBThread = threadRepository.findById(thread.getId());
		if (optDBThread.isPresent()) {
			final Thread dbThread = optDBThread.get();
			dbThread.setMaxPageNumber(thread.getMaxPageNumber());
			dbThread.setName(thread.getName());
			threadRepository.save(dbThread);
		} else {
			threadRepository.save(thread);
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
