package com.mikeycaine.reactiveposts.client;

import com.mikeycaine.reactiveposts.client.content.MainForumIndexContent;
import com.mikeycaine.reactiveposts.model.Forum;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ForumPageUtils {

//	public static Mono<Integer> parseLatestPageId(String bodyText) {
//		return lastPageNumber(Jsoup.parse(bodyText).body())
//			.map(Mono::just)
//			.orElse(Mono.empty());
//	}
//
//	public static Optional<Integer> lastPageNumber(@NotNull Element body) {
//		Optional<String> optLinkToLastPage = body
//			.getElementsByClass("pages")
//			.stream()
//			.flatMap(links ->
//				links.getElementsByAttributeValue("title", "Last page")
//					.stream()
//					.map(el -> el.attr("href")))
//			.findFirst();
//
//		log.debug("Link to last page: " + optLinkToLastPage);
//
//		return optLinkToLastPage.flatMap(link -> {
//			Matcher matcher = Pattern.compile("(.*)pagenumber=(\\d+)").matcher(link);
//			return matcher.find() ?
//				Optional.of(Integer.valueOf(matcher.group(2))) :
//				Optional.empty();
//		});
//	}
}
