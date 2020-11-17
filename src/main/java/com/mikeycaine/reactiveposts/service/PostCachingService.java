package com.mikeycaine.reactiveposts.service;

import com.mikeycaine.reactiveposts.client.Client;
import com.mikeycaine.reactiveposts.client.content.parsed.MainForumIndex;
import com.mikeycaine.reactiveposts.client.content.parsed.PostsPage;
import com.mikeycaine.reactiveposts.client.content.parsed.ThreadsIndex;
import com.mikeycaine.reactiveposts.model.Author;
import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Post;
import com.mikeycaine.reactiveposts.model.Thread;
import com.mikeycaine.reactiveposts.service.config.PostCachingConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCachingService {

	final int MAX_CONCURRENCY = 1;

	private final ForumsService forumsService;
	private final PostCachingConfig config;
	private final Client client;

	private Optional<Disposable> threadUpdates = Optional.empty();
	private Optional<Disposable> postUpdates = Optional.empty();

	private final CountDownLatch forumListLoaded = new CountDownLatch(1);

	@EventListener(ApplicationReadyEvent.class)
	public void applicationReadyListener() {
		log.info("*********************************************************");
		log.info("UPDATES SERVICE CONFIG");
		log.info("threadsUpdateInterval = {}", config.getThreadsUpdateInterval());
		log.info("threadsUpdateMaxRetries = {}", config.getThreadsUpdateMaxRetries());
		log.info("postsUpdateInterval = {}", config.getPostsUpdateInterval());
		log.info("postsUpdateMaxRetries = {}", config.getPostsUpdateMaxRetries());
		log.info("runUpdates = {}", config.isRunUpdates());
		log.info("fixAuthors = {}", config.isFixAuthors());
		log.info("*********************************************************");

		updateForums().subscribe(
			mainForumIndex -> {
				log.info("There are {} main forums", mainForumIndex.getForums().stream().filter(Forum::isTopLevelForum).count());
			},
			t -> log.error("FAILED when getting list of forums: " + t.getMessage()),
			() -> {
				forumListLoaded.countDown();
				startUpdating();
			}
		);
	}

	public void waitForForumsListToLoad() throws InterruptedException {
		forumListLoaded.await();
	}

	public Mono<MainForumIndex> updateForums() {
		if (forumsService.getForumsCount() == 0) {
			log.info("No forums found, initialising...");
			Mono<MainForumIndex> mainForumIndexMono = client.retrieveMainForumIndex();
			return mainForumIndexMono.map(mainForumIndex -> {
				forumsService.saveAll(mainForumIndex.getForums());
				return mainForumIndex;
			});
		} else {
			return Mono.just(new MainForumIndex(forumsService.findAll()));
		}
	}

	public Flux<ThreadsIndex> retrieveThreadsForForum(Forum forum) {
		return client.retrieveThreads(forum, 1, config.getIndexDepth());
	}

	public Flux<ThreadsIndex> updateThreads() {
		List<Forum> subscribedForums = forumsService.subscribedForums();
		log.info("Updating threads for {} subscribed forum{}", subscribedForums.size(), (subscribedForums.size() == 1 ? "" : "s"));

		return Flux.fromIterable(subscribedForums)
			.flatMapSequential(this::retrieveThreadsForForum, MAX_CONCURRENCY)
			.publishOn(Schedulers.elastic())
			.map(forumsService::persistThreadsIndex);
	}

	public Flux<PostsPage> updatePosts() {
		List<Thread> subscribedThreads = forumsService.subscribedThreads();
		log.info("Updating posts for {} subscribed thread{}", subscribedThreads.size(), (subscribedThreads.size() == 1 ? "" : "s"));

		return Flux.fromIterable(subscribedThreads)
			.flatMapSequential(thread -> {
				log.debug("We are subscribed to {}...", thread.toString());
				if (thread.getPagesGot() < thread.getMaxPageNumber()) {
					final int thisPage = thread.getPagesGot() + 1;
					return client.retrievePosts(thread, thisPage)
						.publishOn(Schedulers.elastic())
						.map(forumsService::persistPostsPage);
				} else {
					return Mono.empty();
				}
			}, MAX_CONCURRENCY);
	}

	Flux<Void> fixAuthors() {
		List<Author> authorsToFix = forumsService.findFixableAuthors();
		if (authorsToFix.isEmpty()) {
			log.info("No authors need fixing!");
			return Flux.empty();
		}

		log.info(authorsToFix.size() + " to fix...");
		//IntStream.range(0, Math.min(authorsToFix.size(), 9)).forEach(i -> log.info(authorsToFix.get(i).toString()));
		boolean foundOne = false;
		do {
			int idxToFix = new Random().nextInt(authorsToFix.size());
			Author authorToFix = authorsToFix.get(idxToFix);
			log.info("Trying to fix " + authorToFix);

			List<Post> posts = forumsService.findPostsByAuthor(authorToFix);
			if (posts.size() > 0) {
				Post postToUse = posts.get(new Random().nextInt(posts.size()));
				//log.info("Trying post " + postToUse.toString());

				client.retrievePosts(postToUse.getThread(), postToUse.getPageNum())
					.publishOn(Schedulers.elastic())
					.map((PostsPage pp) -> {
						List<Post> usefulPosts = pp.getPosts().stream().filter(p -> p.getAuthor().equals(authorToFix))
							.collect(Collectors.toList());
						Post postToTry = usefulPosts.get(new Random().nextInt(usefulPosts.size()));
						log.info("Trying to fix from post " + postToTry.getId());
						String titleText = postToTry.getAuthor().getTitleText();
						String titleUrl = postToTry.getAuthor().getTitleURL();

						if (StringUtils.hasText(titleText) || StringUtils.hasText(titleUrl)) {
							forumsService.setAuthorTitleAndUrl(authorToFix.getId(), titleText, titleUrl);
						} else {
							log.info("Can't fix this author");
						}

						log.info("title text = {}", postToTry.getAuthor().getTitleText());
						log.info("title url = {}", postToTry.getAuthor().getTitleURL());
						return postToTry;

					}).subscribe();
				foundOne = true;
			} else {
				log.info("No posts for this author");
			}

		} while (!foundOne);

		return Flux.empty();
	}

	public void startUpdating() {
		if (config.isRunUpdates()) {
			startThreadUpdates();
			startPostUpdates();
		}
		if (config.isFixAuthors()) {
			startFixingAuthors();
		}
	}

	public void stopUpdating() {
		threadUpdates.ifPresent(Disposable::dispose);
		postUpdates.ifPresent(Disposable::dispose);
	}

	private void startThreadUpdates() {
		threadUpdates.ifPresent(Disposable::dispose);
		threadUpdates = Optional.of(
			runUpdates(
				this::updateThreads, "threads",
				config.getThreadsUpdateInterval(),
				config.getThreadsUpdateMaxRetries()));
	}

	private void startPostUpdates() {
		postUpdates.ifPresent(Disposable::dispose);
		postUpdates = Optional.of(
			runUpdates(
				this::updatePosts, "posts",
				config.getPostsUpdateInterval(),
				config.getPostsUpdateMaxRetries()));
	}

	private void startFixingAuthors() {
		postUpdates.ifPresent(Disposable::dispose);
		postUpdates = Optional.of(
			runUpdates(
				this::fixAuthors, "to fix authors",
				Duration.ofSeconds(20),
				5));
	}

	private Disposable runUpdates(Supplier<Flux<?>> supplier, String what, Duration interval, int maxRetries) {
		return Flux
			.interval(Duration.ofSeconds(1), interval)
			.publishOn(Schedulers.elastic())
			.flatMapSequential(l -> {
				log.info("Updating {} [{}]", what, l);
				return supplier.get();
			})
			.retry(maxRetries)
			.subscribe(
				item -> {
				},
				t -> log.error("FAILED when updating {}: {} ", what, t.getMessage())
			);
	}
}
