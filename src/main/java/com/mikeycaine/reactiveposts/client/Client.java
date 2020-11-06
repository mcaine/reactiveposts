package com.mikeycaine.reactiveposts.client;

import com.mikeycaine.reactiveposts.client.content.parsed.MainForumIndex;
import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Post;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.mikeycaine.reactiveposts.model.Thread;

public interface Client {

    Mono<MainForumIndex> retrieveMainForumIndex();

    Flux<Thread> retrieveThreads(Forum forum, int pageId);
    Flux<Thread> retrieveThreads(Forum forum, int startPageId, int endPageId);

    Mono<Integer> latestPageId(Thread thread);

    Flux<Post> retrievePosts(Thread thread, int pageId);

    Flux<Post> retrievePosts(Thread thread, int startPageId, int endPageId);


}
