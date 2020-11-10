package com.mikeycaine.reactiveposts.service;

import com.mikeycaine.reactiveposts.client.Client;
import com.mikeycaine.reactiveposts.client.content.parsed.MainForumIndex;
import com.mikeycaine.reactiveposts.client.content.parsed.PostsPage;
import com.mikeycaine.reactiveposts.client.content.parsed.ThreadsIndex;
import com.mikeycaine.reactiveposts.model.Author;
import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Post;
import com.mikeycaine.reactiveposts.model.Thread;
import com.mikeycaine.reactiveposts.repos.AuthorRepository;
import com.mikeycaine.reactiveposts.repos.ForumRepository;
import com.mikeycaine.reactiveposts.repos.PostRepository;
import com.mikeycaine.reactiveposts.repos.ThreadRepository;
import com.mikeycaine.reactiveposts.service.config.UpdatesConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ForumsService {
	private final ForumRepository forumRepository;
	private final PostRepository postRepository;
	private final ThreadRepository threadRepository;
	private final AuthorRepository authorRepository;
	private final UpdatesConfig config;
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
		List<Forum> subscribedForums = forumRepository.subscribedForums();
		log.info("Updating threads for {} subscribed forum{}", subscribedForums.size(), (subscribedForums.size() == 1 ? "" : "s"));

		return Flux.fromIterable(subscribedForums)
			.flatMapSequential(forum -> client.retrieveThreads(forum, 1, config.getIndexDepth()), MAX_CONCURRENCY)
			.doOnNext(threadsIndex -> {
				threadsIndex.getThreads().forEach(this::mergeThreadInfo);
			});
	}

	public Flux<PostsPage> updatePosts() {
		List<Thread> subscribedThreads = threadRepository.subscribedThreads();
		log.info("Updating posts for {} subscribed thread{}", subscribedThreads.size(), (subscribedThreads.size() == 1 ? "" : "s"));

		return Flux.fromIterable(subscribedThreads)
			.flatMapSequential(thread -> {
				log.debug("We are subscribed to {}...", thread.toString());
				if (thread.getPagesGot() < thread.getMaxPageNumber()) {
					final int thisPage = thread.getPagesGot() + 1;
					return client.retrievePosts(thread, thisPage)
						.doOnNext(postsPage -> {
							postsPage.getMaxPageNum().ifPresent(thread::setMaxPageNumber);
							if (thisPage < thread.getMaxPageNumber()) {
								thread.setPagesGot(thisPage);
							}
							List<Post> posts = postsPage.getPosts();
							List<Author> postAuthors = posts.stream().map(Post::getAuthor).collect(Collectors.toList());
							log.info("Persisting {} posts for page {} of {}", posts.size(), postsPage.getPageNum(), thread.toString());
							authorRepository.saveAll(postAuthors);
							postRepository.saveAll(posts);
							threadRepository.save(thread);
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
			Optional optDBAuthor = authorRepository.findById(thread.getAuthor().getId());
			if (!optDBAuthor.isPresent()) {
				authorRepository.save(thread.getAuthor());
			}
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
