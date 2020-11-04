package com.mikeycaine.reactiveposts.client;

import com.mikeycaine.reactiveposts.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class ThreadPageUtils {

	public static Optional<String> cleanBodyHtml(Optional<String> rawPostBodyHtml) {
		return rawPostBodyHtml.map(html ->
			html.replace("<!-- google_ad_section_start -->", "")
				.replace("<!-- google_ad_section_end -->", "")
				.replace("<p class=\"editedby\"> </p>", "")
				.trim());
	}

	public static Optional<Integer> getPostId(Element postElement) {
		// Post IDs look like '#post508526226'
		final String postIDPrefix = "#post";
		final int postIDPrefixLength = postIDPrefix.length();

		final Optional<String> postIdString = postElement
			.getElementsByClass("postdate")
			.stream()
			.flatMap(el -> el.getElementsByTag("a").stream())
			.map(el -> el.attr("href"))
			.filter(s -> s.startsWith(postIDPrefix))
			.map(s -> s.substring(postIDPrefixLength))
			.findFirst();

		return parsePostIdString(postIdString);
	}

	public static Optional<Integer> parsePostIdString(Optional<String> optPostIdStr) {
		try {
			return optPostIdStr.map(Integer::valueOf);
		} catch (NumberFormatException ex) {
			return Optional.empty();
		}
	}

	public static Stream<Post> parsePost(Element postElement, int threadId, int pageId) {
		final Optional<String> authorName = postElement
			.getElementsByClass("author")
			.stream()
			.map(Element::ownText)
			.findFirst();

		final Optional<String> rawPostBodyHtml = postElement
			.getElementsByClass("postbody")
			.stream()
			.map(Element::html)
			.findFirst();

		final Optional<String> postBodyHtml = cleanBodyHtml(rawPostBodyHtml);

		Optional<Integer> optPostId = getPostId(postElement);
		if (optPostId.isEmpty()) {
			return Stream.empty();
		}
		int postId = optPostId.get();

		Optional<String> postDateString = postElement
			.getElementsByClass("postdate")
			.stream().map(Element::ownText)
			.findFirst();

		// eg Feb 1, 2020 05:24
		//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm");
		Optional<LocalDateTime> postDate = Optional.empty();
		try {
			postDate = postDateString.map(pds -> LocalDateTime.parse(pds, DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm")));
		} catch (DateTimeParseException ex) {
			log.warn("Can't parse post date");
		}

		Optional<String> posterId = postElement
			.getElementsByTag("td")
			.stream()
			.filter(el -> el.hasClass("userinfo"))
			.flatMap(el -> el.classNames().stream())
			.filter(s -> s.startsWith("userid-"))
			.map(s -> s.substring(7))
			.findFirst();

		// TODO
		return Stream.empty();

//		return (postDate.isPresent()
//			&& authorName.isPresent()
//			&& posterId.isPresent()
//			&& postBodyHtml.isPresent()) ?
//			Stream.of(new Post(
//				postId,
//				pageId,
//				postDate.get(),
//				Instant.now(),
//				authorName.get(),
//				Integer.valueOf(posterId.get()),
//				postBodyHtml.get(),
//				threadId))
//			: Stream.empty();
	}

	public static void checkThreadId(Element thread, long threadId) {
		String threadIdStr = thread.classNames().stream()
			.filter(s -> s.startsWith("thread:"))
			.map(s -> s.substring(7))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Can't get Thread Id"));

		if (!String.valueOf(threadId).equals(threadIdStr)) {
			throw new RuntimeException("Different thread id, expected " + threadId + " but got " + threadIdStr);
		}
	}

	public static Flux<Post> parseToPostsFlux(PostsPageContent content, int threadId, int pageId) {
		return Flux.fromStream(postStreamFromPage(content, threadId, pageId));
	}

	public static Stream<Post> postStreamFromPage(PostsPageContent content, int threadId, int pageId) {
		return threadElementFromResponseBody(content, threadId)
			.map(threadElement -> postsFromThreadElement(threadElement, threadId, pageId))
			.orElse(Stream.empty());
	}

	public static Optional<Element> threadElementFromResponseBody(PostsPageContent content, int expectedThreadId) {
		final String body = content.content();
		if (null == body || body.isEmpty() || body.isBlank()) {
			return Optional.empty();
		}
		Element bodyElement = Jsoup.parse(body).body();
		Element threadElement = bodyElement.getElementById("thread");
		if (threadElement != null) {
			checkThreadId(threadElement, expectedThreadId);
			return Optional.of(threadElement);
		} else {
			return Optional.empty();
		}
	}

	public static Stream<Post> postsFromThreadElement(Element threadElement, int threadId, int pageId) {
		return threadElement.getElementsByClass("post").stream()
			.flatMap(postElement -> parsePost(postElement, threadId, pageId));
	}
}

