package com.mikeycaine.reactiveposts.client;

import com.mikeycaine.reactiveposts.client.content.ThreadsIndexContent;
import com.mikeycaine.reactiveposts.client.content.PostsPageContent;
import com.mikeycaine.reactiveposts.client.content.parsed.PostsPage;
import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Post;
import com.mikeycaine.reactiveposts.model.Thread;
import com.mikeycaine.reactiveposts.service.ImageFindingService;
import com.mikeycaine.reactiveposts.testdata.IndexPageSpec;
import com.mikeycaine.reactiveposts.testdata.ThreadPageSpec;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class ClientTest extends ClientTestUtils {

	private final Client client = new ReactiveSAClient(new WebClientConfig().webClient());

	@Test
	public void testRetrieveForums() {
		StepVerifier.create(client.retrieveMainForumIndex())
			.expectNextMatches(mainForumIndex -> mainForumIndex.getForums().size() == 29)
			.verifyComplete();
	}

	@Test
	public void testPrintForums() {
		logMainForumsIndex(client.retrieveMainForumIndex());
	}

	@Test
	public void testRetrieveThreadsIndexForForum() {
		Forum gbs = new Forum(273, "General Bullshit");
		StepVerifier.create(client.retrieveThreads(gbs, 1))
			.expectNextCount(1L)
			.verifyComplete();
	}

	@Test
	public void testRetrieveThreadsIndexForForumMultiplePages() {
		Forum gbs = new Forum(273, "General Bullshit");
		StepVerifier.create(client.retrieveThreads(gbs, 1, 3))
			.expectNextCount(3L)
			.verifyComplete();
	}

	@Test
	public void testParseThreadsIndex() {
		Forum goonsWithSpoons = new Forum(161, "Goons with spoons");
		StepVerifier.create(
			IndexPageSpec.of(goonsWithSpoons, 4).cachedContentMono().flatMapMany(ThreadsIndexContent::parseToThreadsFlux)
		)
			.expectNextMatches(thread -> thread.getName().equals("ICSA 69: Breakfast Voting Thread"))
			.expectNextCount(29L) // this forum has 30 posts per index page for some reason???
			.verifyComplete();
	}

	@Test
	public void testParseThreadsIndex2() {
		Forum iyg = new Forum(192, "Inspect your gadgets");
		StepVerifier.create(
			IndexPageSpec.of(iyg, 5).cachedContentMono().flatMapMany(ThreadsIndexContent::parseToThreadsFlux)
		)
			.expectNextMatches(thread -> thread.getName().equals("Like-new Huawei Watch steel link, Android/iOS watch, near-perfect condition"))
			.expectNextCount(29L)  // this forum has 30 posts per index page for some reason???
			.verifyComplete();
	}

	@Test
	public void testParseThreadsIndex3() {
		Forum iyg = new Forum(273, "GBS");
		StepVerifier.create(
			IndexPageSpec.of(iyg, 6).cachedContentMono().flatMapMany(ThreadsIndexContent::parseToThreadsFlux)
		)
			.expectNextMatches(thread -> thread.getName().equals("Other times I don't make a thread"))
			.expectNextCount(39L)
			.verifyComplete();
	}

	@Test
	public void testParsePostsPage() {
		Thread trumo = Thread.withId(3942499);
		StepVerifier.create(
			ThreadPageSpec.of(trumo, 1).cachedContentMono().flatMapMany(PostsPageContent::parseToPostsFlux)
		)
			.expectNextMatches(post -> post.getAuthor().getName().equals("Korean Boomhauer") && post.getId() == 508526226)
			.expectNextCount(39L)
			.verifyComplete();
	}

	@Test
	public void testParsePostsPage2() {
		Thread thread = Thread.withId(3913301);
		StepVerifier.create(
			ThreadPageSpec.of(thread, 10).cachedContentMono().flatMapMany(PostsPageContent::parseToPostsFlux)
		)
			.expectNextMatches(post -> post.getAuthor().getName().equals("oh but seriously I") && post.getId() == 507178153)
			.expectNextCount(39L)
			.verifyComplete();
	}

	@Test
	void testFindImageUrls() throws MalformedURLException {
		AtomicInteger count = new AtomicInteger(0);

		List<Post> posts = postsFrom(3913301, 10);
		posts.forEach(post -> {
			int idx = count.incrementAndGet();
			List<URL> images = new ImageFindingService().findImagesInPost(post);
			if (images.size() > 0) {
				log.info("Images found in post {}", idx);
				images.forEach(image -> log.info(" - " + image.toString()));
			}
		});

		Set<URL> images = posts.stream().flatMap(post -> new ImageFindingService().findImagesInPost(post).stream()).collect(Collectors.toSet());
		assertTrue(images.contains(new URL("https://i.imgur.com/TbnGK4l.gif")));
		assertTrue(images.contains(new URL("https://i.imgur.com/kXW4Wqk.jpg")));
		assertTrue(images.contains(new URL("https://fi.somethingawful.com/images/smilies/sss.gif")));
	}

	@Test
	void testFindTweets() throws MalformedURLException {
		List<Post> posts = postsFrom(3946225, 3);

		Post post = posts.get(36);
		List<URL> tweets = new ImageFindingService().findTweetsInPost(post);
		log.info(tweets.get(0).toString());
		assertTrue(tweets.contains(new URL("https://twitter.com/proudsocialist/status/1322749610503229440?s=21")));

	}
}
