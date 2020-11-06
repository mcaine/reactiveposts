package com.mikeycaine.reactiveposts.client.content;

import com.mikeycaine.reactiveposts.client.content.AbstractContent;
import com.mikeycaine.reactiveposts.client.content.parsed.MainForumIndex;
import com.mikeycaine.reactiveposts.model.Forum;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainForumIndexContent extends AbstractContent<MainForumIndex> {
	public MainForumIndexContent(String content) {
		super(content);
	}

	@Override
	public MainForumIndex parsed() {
		return new MainForumIndex(forumStreamFromPage().collect(Collectors.toUnmodifiableList()));
	}

	public Flux<Forum> parseMainForumIndexPage() {
		return Flux.fromStream(forumStreamFromPage());
	}

	private Stream<Forum> forumStreamFromPage() {
		ensureContentPresent();
		Element body = Jsoup.parse(content).body();
		Elements forumElements = body.getElementById("forums").getElementsByClass("forum");

		return forumElements.stream().flatMap(element ->
			element.getElementsByClass("title").stream()).flatMap(titleElement -> {

			Element forumElement = titleElement.getElementsByClass("forum").first();
			String forumName = forumElement.text();
			return forumIdFromHref(forumElement.attr("href")).stream()
				.map(fid -> new Forum(fid, forumName, subForums(titleElement)));
		});
	}

	private static Set<Forum> subForums(Element titleElement) {
		return titleElement.getElementsByClass("subforums").stream().flatMap(subforumsElement ->
			subforumsElement.getElementsByTag("a").stream().flatMap(subforumLinkElement ->
				forumIdFromHref(subforumLinkElement.attr("href")).stream()
					.map(subForumId -> new Forum(subForumId, subforumLinkElement.text()))
			)
		).collect(Collectors.toSet());
	}

	private static Optional<Integer> forumIdFromHref(String href) {
		Pattern pattern = Pattern.compile("(.*)forumid=(\\d+)");
		Matcher matcher = pattern.matcher(href);
		return matcher.find() ? Optional.of(Integer.parseInt(matcher.group(2))) : Optional.empty();
	}
}
