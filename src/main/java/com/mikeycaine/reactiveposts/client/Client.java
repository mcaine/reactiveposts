package com.mikeycaine.reactiveposts.client;

import com.mikeycaine.reactiveposts.client.content.PostsPageContent;
import com.mikeycaine.reactiveposts.client.content.parsed.MainForumIndex;
import com.mikeycaine.reactiveposts.client.content.parsed.PostsPage;
import com.mikeycaine.reactiveposts.client.content.parsed.ThreadsIndex;
import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Post;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.mikeycaine.reactiveposts.model.Thread;

public interface Client {

    Mono<MainForumIndex> retrieveMainForumIndex();

    Mono<ThreadsIndex> retrieveThreads(Forum forum, int pageId);
    Flux<ThreadsIndex> retrieveThreads(Forum forum, int startPageId, int endPageId);

    Mono<Integer> latestPageId(Thread thread);

    Mono<PostsPage> retrievePosts(Thread thread, int pageId);

    Flux<PostsPage>retrievePosts(Thread thread, int startPageId, int endPageId);


}
