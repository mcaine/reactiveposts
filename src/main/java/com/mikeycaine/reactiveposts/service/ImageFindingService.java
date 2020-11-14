package com.mikeycaine.reactiveposts.service;

import com.mikeycaine.reactiveposts.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ImageFindingService {

	final Pattern postIdPattern = Pattern.compile("(.*)postid=(\\d+)");

	public List<URL> findImagesInPost(Post post) {
		return Jsoup.parse(post.getHtml()).getElementsByTag("img").stream()
			.map(imgElement -> imgElement.attr("src"))
			.flatMap(imgsrc -> {
				try {
					return Stream.of(new URL(imgsrc));
				} catch (MalformedURLException e) {
					return Stream.empty();
				}
			})
			.collect(Collectors.toUnmodifiableList());
	}

	public List<URL> findLinksInPost(Post post) {
		return Jsoup.parse(post.getHtml()).getElementsByTag("a").stream()
			.map(linkElement -> linkElement.attr("href"))
			.flatMap(linkHref -> {
				try {
					return Stream.of(new URL(linkHref));
				} catch (MalformedURLException e) {
					return Stream.empty();
				}
			})
			.collect(Collectors.toUnmodifiableList());
	}

	public List<URL> findTweetsInPost(Post post) {
		return findLinksWithHost(post, "twitter.com");
	}

	public List<URL> findLinksWithHost(Post post, String host) {
		return findLinksInPost(post).stream()
			.filter(url -> url.getHost().equals(host))
			.collect(Collectors.toUnmodifiableList());
	}

	public List<Integer> findQuotesInPost(Post post) {
		return Jsoup.parse(post.getHtml()).getElementsByClass("quote_link").stream()
			.map(linkElement -> linkElement.attr("href"))
			.flatMap(href -> {
				Matcher matcher = postIdPattern.matcher(href);
				return matcher.find() ? Stream.of(Integer.parseInt(matcher.group(2))) : Stream.empty();
			})
			.collect(Collectors.toUnmodifiableList());
	}
}
