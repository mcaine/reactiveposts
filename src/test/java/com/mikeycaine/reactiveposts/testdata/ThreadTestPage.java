package com.mikeycaine.reactiveposts.testdata;

import com.mikeycaine.reactiveposts.client.ReactiveSAClient;
import com.mikeycaine.reactiveposts.client.WebClientConfig;
import com.mikeycaine.reactiveposts.client.content.PostsPageContent;
import com.mikeycaine.reactiveposts.model.Thread;
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
public
class ThreadTestPage extends TestPage<PostsPageContent> {
	final Thread thread;
	final int pageNum;

	public static ThreadTestPage of(Thread thread, int pageNum) {
		return new ThreadTestPage(thread, pageNum);
	}

	@Override
	public URL url() throws MalformedURLException {
		return new URL(
			String.format("https://forums.somethingawful.com/showthread.php?threadid=%d&perpage=40&pagenumber=%d",
				thread.getId(), pageNum));
	}

	@Override
	public Path targetPath() {
		return Path.of(
			threadsDir,
			String.format("thread%d_page%d.html", thread.getId(), pageNum)
		);
	}

	protected PostsPageContent result(String fileContent) {
		return new PostsPageContent(fileContent, thread, pageNum);
	}
}
