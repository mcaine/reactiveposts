package com.mikeycaine.reactiveposts.testdata;

import com.mikeycaine.reactiveposts.client.content.ThreadsIndexContent;
import com.mikeycaine.reactiveposts.client.ReactiveSAClient;
import com.mikeycaine.reactiveposts.client.Urls;
import com.mikeycaine.reactiveposts.client.WebClientConfig;
import com.mikeycaine.reactiveposts.model.Forum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
@Slf4j
public class ForumIndexTestPage extends TestPage<ThreadsIndexContent> {
	//final int forumId;
	final Forum forum;
	final int pageNum;

	public static ForumIndexTestPage of(Forum forum, int pageNum) {
		return new ForumIndexTestPage(forum, pageNum);
	}

	@Override
	public URL url() throws MalformedURLException {
		return new URL("https://forums.somethingawful.com" + Urls.forumThreadsIndexAddress(forum.getId(), pageNum));
	}

	@Override
	public Path targetPath() {
		return Path.of(
			indexesDir,
			String.format("forumIndex_%d_page%d.html", forum.getId(), pageNum)
		);
	}

	protected ThreadsIndexContent result(String s) {
		return new ThreadsIndexContent(s, forum, pageNum);
	}
}
