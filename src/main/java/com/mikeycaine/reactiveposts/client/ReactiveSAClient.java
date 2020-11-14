package com.mikeycaine.reactiveposts.client;

import com.mikeycaine.reactiveposts.client.content.ThreadsIndexContent;
import com.mikeycaine.reactiveposts.client.content.MainForumIndexContent;
import com.mikeycaine.reactiveposts.client.content.PostsPageContent;
import com.mikeycaine.reactiveposts.client.content.parsed.MainForumIndex;
import com.mikeycaine.reactiveposts.client.content.parsed.PostsPage;
import com.mikeycaine.reactiveposts.client.content.parsed.ThreadsIndex;
import com.mikeycaine.reactiveposts.model.*;
import com.mikeycaine.reactiveposts.model.Thread;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mikeycaine.reactiveposts.client.ValidationUtils.validatePageIdParam;
import static com.mikeycaine.reactiveposts.client.ValidationUtils.validatePageRangeParams;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReactiveSAClient implements Client {
	private final static int MAX_CONCURRENT_REQUESTS = 10;

	private final WebClient webClient;

	@Override
	public Mono<MainForumIndex> retrieveMainForumIndex() {
		log.info("Retrieving list of forums");
		return retrieveBodyAsMono(Urls.mainforumIndex())
			.map(MainForumIndexContent::new)
			.map(MainForumIndexContent::parsed);
	}

	@Override
	public Mono<ThreadsIndex> retrieveThreads(Forum forum, int pageNum) {
		log.info("Retrieving threads for {}, page {}", forum.toString(), pageNum);
		return retrieveBodyAsMono(Urls.forumThreadsIndexAddress(forum.getId(), pageNum))
			.map(body -> new ThreadsIndexContent(body, forum, pageNum))
			.map(ThreadsIndexContent::parsed);
	}

	@Override
	public Flux<ThreadsIndex> retrieveThreads(Forum forum, int startPageId, int endPageId) {
		if (startPageId != endPageId) {
			log.info("Retrieving threads for {}, pages {} -> {}", forum.toString(), startPageId, endPageId );
		}
		int count = validatePageRangeParams(startPageId, endPageId);
		return Flux.range(startPageId, count)
			.flatMapSequential(pageId -> retrieveThreads(forum, pageId), MAX_CONCURRENT_REQUESTS);
	}

	@Override
	public Mono<PostsPage> retrievePosts(Thread thread, int pageId) {
		log.info("Retrieving posts for {}, page {} of {}", thread.toString(), pageId, thread.getMaxPageNumber());
		validatePageIdParam(pageId);
		return postsPageContent(thread, pageId);
	}

	@Override
	public Flux<PostsPage> retrievePosts(Thread thread, int startPageId, int endPageId) {
		if (startPageId != endPageId) {
			log.info("Retrieving posts for {}, pages {} -> {}", thread.toString(), startPageId, endPageId);
		}
		int count = validatePageRangeParams(startPageId, endPageId);
		return Flux.range(startPageId, count)
			.flatMapSequential(pageId -> retrievePosts(thread, pageId), MAX_CONCURRENT_REQUESTS);
	}

	@Override
	public Mono<Integer> latestPageId(Thread thread) {
		log.info("Getting latest page for {}", thread.toString());
		return postsPageContent(thread, 1)
			.map(PostsPage::getMaxPageNum)
			.flatMap(optPageNum -> optPageNum.isPresent() ? Mono.just(optPageNum.get()) : Mono.empty());
	}

	public Mono<String> retrieveBodyAsMono(String url) {
		return webClient
			.method(HttpMethod.GET)
			.uri(url)
			.retrieve()
			.bodyToMono(String.class);
	}

	private Mono<PostsPage> postsPageContent(Thread thread, int pageNum) {
	    return retrieveBodyAsMono(Urls.postsPageAddress(thread.getId(), pageNum))
            .map(body -> new PostsPageContent(body, thread, pageNum))
		    .map(PostsPageContent::parsed);
    }
}
