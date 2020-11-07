package com.mikeycaine.reactiveposts.service;

import com.mikeycaine.reactiveposts.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ImageFindingService {

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
		return findLinksInPost(post).stream()
			.filter(url -> url.getHost().equals("twitter.com"))
			.collect(Collectors.toUnmodifiableList());
	}
}
