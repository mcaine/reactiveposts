package com.mikeycaine.reactiveposts.client.content;

import com.mikeycaine.reactiveposts.model.Author;
import com.mikeycaine.reactiveposts.model.Post;
import com.mikeycaine.reactiveposts.model.Thread;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
public class PostsPageContent extends AbstractContent {
	final Thread thread;
	final int pageNum;

	public PostsPageContent(String content, Thread thread, int pageNum) {
		super(content);
		this.thread = thread;
		this.pageNum = pageNum;
	}

	public Flux<Post> parseToPostsFlux() {
		return Flux.fromStream(postStreamFromPage());
	}

	private Stream<Post> postStreamFromPage() {
		return threadElementFromContent()
			.map(threadElement -> postsFromThreadElement(threadElement))
			.orElse(Stream.empty());
	}

	private Optional<Element> threadElementFromContent() {
		if (null == content || content.isEmpty() || content.isBlank()) {
			return Optional.empty();
		}

		Element bodyElement = Jsoup.parse(content).body();
		Optional<Integer> optLastPageNumber = lastPageNumber(bodyElement);

		Element threadElement = bodyElement.getElementById("thread");
		if (threadElement != null) {
			checkThreadId(threadElement, thread.getId());
			return Optional.of(threadElement);
		} else {
			return Optional.empty();
		}
	}

	public Stream<Post> postsFromThreadElement(Element threadElement) {
		return threadElement.getElementsByClass("post").stream()
			.flatMap(this::parsePost);
	}

	private Stream<Post> parsePost(Element postElement) {
		final Optional<String> optAuthorName = postElement
			.getElementsByClass("author")
			.stream()
			.map(Element::ownText)
			.findFirst();
		if (optAuthorName.isEmpty()) {
			return Stream.empty();
		}
		String authorName = optAuthorName.get();

		final Optional<String> rawPostBodyHtml = postElement
			.getElementsByClass("postbody")
			.stream()
			.map(Element::html)
			.findFirst();

		final Optional<String> optPostBodyHtml = rawPostBodyHtml.map(this::cleanBodyHtml);
		if (optPostBodyHtml.isEmpty()) {
			return Stream.empty();
		}
		final String postBodyHtml = optPostBodyHtml.get();

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
		Optional<LocalDateTime> optPostDate = Optional.empty();
		try {
			optPostDate = postDateString.map(pds -> LocalDateTime.parse(pds, DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm")));
		} catch (DateTimeParseException ex) {
			log.warn("Can't parse post date");
		}
		if (optPostDate.isEmpty()) {
			return Stream.empty();
		}
		LocalDateTime postDate = optPostDate.get();

		Optional<String> optAuthorId = postElement
			.getElementsByTag("td")
			.stream()
			.filter(el -> el.hasClass("userinfo"))
			.flatMap(el -> el.classNames().stream())
			.filter(s -> s.startsWith("userid-"))
			.map(s -> s.substring(7))
			.findFirst();
		if (optAuthorId.isEmpty()) {
			return Stream.empty();
		}

		Optional<Integer> optAuthorIdInt;
		try {
			optAuthorIdInt = Optional.of(Integer.parseInt(optAuthorId.get()));
		} catch (NumberFormatException nfe) {
			return Stream.empty();
		}
		int authorId = optAuthorIdInt.get();

		Author author = new Author();
		author.setId(authorId);
		author.setName(authorName);

		Post post = new Post();
		post.setId(postId);
		post.setAuthor(author);
		post.setThread(thread);
		post.setPostDate(postDate);
		post.setRetrievedDate(Instant.now());
		post.setHtml(postBodyHtml);

		author.getPosts().add(post);
		return Stream.of(post);
	}


	public String cleanBodyHtml(String rawPostBodyHtml) {
		return rawPostBodyHtml
				.replace("<!-- google_ad_section_start -->", "")
				.replace("<!-- google_ad_section_end -->", "")
				.replace("<p class=\"editedby\"> </p>", "")
				.trim();
	}

	private void checkThreadId(Element thread, long threadId) {
		String threadIdStr = thread.classNames().stream()
			.filter(s -> s.startsWith("thread:"))
			.map(s -> s.substring(7))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Can't get Thread Id"));

		if (!String.valueOf(threadId).equals(threadIdStr)) {
			throw new RuntimeException("Different thread id, expected " + threadId + " but got " + threadIdStr);
		}
	}

	private Optional<Integer> getPostId(Element postElement) {
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

	private Optional<Integer> parsePostIdString(Optional<String> optPostIdStr) {
		try {
			return optPostIdStr.map(Integer::valueOf);
		} catch (NumberFormatException ex) {
			return Optional.empty();
		}
	}

	/////////////

	public Mono<Integer> parseLatestPageId() {
		return lastPageNumber(Jsoup.parse(content).body())
			.map(Mono::just)
			.orElse(Mono.empty());
	}

	public Optional<Integer> lastPageNumber(@NotNull Element body) {
		Optional<String> optLinkToLastPage = body
			.getElementsByClass("pages")
			.stream()
			.flatMap(links ->
				links.getElementsByAttributeValue("title", "Last page")
					.stream()
					.map(el -> el.attr("href")))
			.findFirst();

		log.debug("Link to last page: " + optLinkToLastPage);

		return optLinkToLastPage.flatMap(link -> {
			Matcher matcher = Pattern.compile("(.*)pagenumber=(\\d+)").matcher(link);
			return matcher.find() ?
				Optional.of(Integer.valueOf(matcher.group(2))) :
				Optional.empty();
		});
	}
}