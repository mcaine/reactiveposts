package com.mikeycaine.reactiveposts.client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Client<P,F> {

    Mono<Integer> latestPageId(int threadId);

    Flux<P> retrievePosts(int threadId, int pageId);

    Flux<P> retrievePosts(int threadId, int startPageId, int endPageId);

    Flux<F> retrieveForums();
}
