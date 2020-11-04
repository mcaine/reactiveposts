package com.mikeycaine.reactiveposts.testdata;

import com.mikeycaine.reactiveposts.client.ReactiveSAClient;
import com.mikeycaine.reactiveposts.client.WebClientConfig;
import com.mikeycaine.reactiveposts.client.content.ForumThreadsIndexContent;
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
class ThreadPageSpec implements Directories {
	final Thread thread;
	final int pageNum;

	public static ThreadPageSpec of(Thread thread, int pageNum) {
		return new ThreadPageSpec(thread, pageNum);
	}

	public URL url() throws MalformedURLException {
		return new URL(
			String.format("https://forums.somethingawful.com/showthread.php?threadid=%d&perpage=40&pagenumber=%d",
				thread.getId(), pageNum));
	}

	public Path threadPagePath() {
		return Path.of(
			threadsDir,
			String.format("thread%d_page%d.html", thread.getId(), pageNum)
		);
	}

	public void cacheThreadPage() {
		Path path = this.threadPagePath();
		if (!Files.exists(path)) {
			log.info("Retrieving " + path);
			ReactiveSAClient reactiveSAClient = new ReactiveSAClient(new WebClientConfig().webClient());
			String contents = "";
			try {
				contents = reactiveSAClient.retrieveBodyAsMono(url().toString()).block();
			} catch (MalformedURLException muex) {
				muex.printStackTrace();
			}
			if (!contents.isEmpty()) {
				try (PrintWriter pw = new PrintWriter(new FileWriter(path.toFile()))) {
					pw.print(contents);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Mono<PostsPageContent> cachedContentMono() {
		try {
			Path path = this.threadPagePath();
			if (Files.exists(path)) {
				return Mono.just(new PostsPageContent(Files.readString(path, StandardCharsets.UTF_8), thread, pageNum));
			} else {
				log.warn("Test file " + path + "not found");
				return Mono.empty();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return Mono.empty();
		}
	}
}
