package com.mikeycaine.reactiveposts.client.content;

import com.mikeycaine.reactiveposts.client.content.parsed.ThreadsIndex;
import com.mikeycaine.reactiveposts.model.Author;
import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Thread;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThreadsIndexContent extends AbstractContent<ThreadsIndex> {

	private final Forum forum;
	private final int pageNum;

	public ThreadsIndexContent(String content, Forum forum, int pageNum) {
		super(content);
		this.forum = forum;
		this.pageNum = pageNum;
	}

	@Override
	public ThreadsIndex parsed() {
		ensureContentPresent();
		return new ThreadsIndex(forum, pageNum, threadStreamFromPage().collect(Collectors.toUnmodifiableList()));
	}

	public Flux<Thread> parseToThreadsFlux() {
		return Flux.fromStream(threadStreamFromPage());
	}

	private Stream<Thread> threadStreamFromPage() {
		ensureContentPresent();
		return forumElementFromResponseBody(content)
			.map(this::threadsFromForumElement)
			.orElse(Stream.empty());
	}

	private Stream<Thread> threadsFromForumElement(Element forumElement) {
		return forumElement.getElementsByClass("thread").stream()
			.flatMap(this::parseThreadElement);
	}

	private Optional<Element> forumElementFromResponseBody(String body) {
		if (null == body || body.isEmpty() || body.isBlank()) {
			return Optional.empty();
		}
		Element bodyElement = Jsoup.parse(body).body();
		Element threadElement = bodyElement.getElementById("forum");
		if (threadElement != null) {
			return Optional.of(threadElement);
		} else {
			return Optional.empty();
		}
	}

	private Stream<Thread> parseThreadElement(Element threadElement) {
		Optional<Integer> optThreadId = getThreadId(threadElement);
		if (optThreadId.isEmpty()) {
			return Stream.empty();
		}
		int threadId = optThreadId.get();

		Optional<String> optThreadTitle = threadElement
			.getElementsByClass("info")
			.stream()
			.flatMap(info -> info.getElementsByTag("a")
				.stream())
			.findFirst()
			.map(Element::text);
		if (optThreadTitle.isEmpty()) {
			return Stream.empty();
		}
		String threadTitle = optThreadTitle.get();

		// Thread page count
		Stream<Integer> pageNumbers = threadElement
			.getElementsByClass("pagenumber")
			.stream()
			.map(Element::text)
			.filter(s -> !s.startsWith("Last"))
			.flatMap(s -> {
				try {
					return Stream.of(Integer.parseInt(s));
				} catch (NumberFormatException nfe) {
					return Stream.empty();
				}
			});

		Optional<Integer>optMaxPageNumber = pageNumbers.max(Integer::compareTo);

		int maxPageNumber = optMaxPageNumber.orElse(1);

		// Thread author
		Optional<Element> authorElement = threadElement.getElementsByClass("author").stream()
			.flatMap(a -> a.getElementsByTag("a").stream()).findFirst();

		if (authorElement.isEmpty()) {
			return Stream.empty();
		}

		String authorName = authorElement.get().text();
		String authorIdHref = authorElement.get().attr("href");

		if (null == authorName || authorName.isEmpty()) {
			return Stream.empty();
		}

		if (null == authorIdHref || authorIdHref.isEmpty()) {
			return Stream.empty();
		}

		Optional<Integer> optAuthorId = userIdFromHref(authorIdHref);
		if (optAuthorId.isEmpty()) {
			return Stream.empty();
		}
		int authorId = optAuthorId.get();

		Thread thread = new Thread();
		thread.setId(threadId);
		thread.setName(threadTitle);
		thread.setMaxPageNumber(maxPageNumber);
		thread.setForum(forum);

		Author author = new Author();
		author.setId(authorId);
		author.setName(authorName);
		thread.setAuthor(author);

		return Stream.of(thread);
	}

	private Optional<Integer> getThreadId(Element threadElement) {
		String idAttr = threadElement.attr("id");
		if (null == idAttr || idAttr.length() == 0) {
			return Optional.empty();
		} else if (idAttr.startsWith("thread")) {
			String idText = idAttr.substring(6);
			return Optional.of(Integer.parseInt(idText));
		}
		return Optional.empty();
	}

	static Optional<Integer> userIdFromHref(String href) {
		Pattern pattern = Pattern.compile("(.*)userid=(\\d+)");
		Matcher matcher = pattern.matcher(href);
		return matcher.find() ? Optional.of(Integer.parseInt(matcher.group(2))) : Optional.empty();
	}
}
