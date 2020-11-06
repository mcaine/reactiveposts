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
public class IndexPageSpec implements Directories {
	//final int forumId;
	final Forum forum;
	final int pageNum;

	public static IndexPageSpec of(Forum forum, int pageNum) {
		return new IndexPageSpec(forum, pageNum);
	}

	public URL url() throws MalformedURLException {
		return new URL("https://forums.somethingawful.com" + Urls.forumThreadsIndexAddress(forum.getId(), pageNum));
	}

	public Path indexPagePath() {
		return Path.of(
			indexesDir,
			String.format("forumIndex_%d_page%d.html", forum.getId(), pageNum)
		);
	}

	public void cacheIndexPage() {
		Path path = this.indexPagePath();
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

	public Mono<ThreadsIndexContent> cachedContentMono() {
		try {
			Path path = this.indexPagePath();
			if (Files.exists(path)) {
				return Mono.just(new ThreadsIndexContent(Files.readString(path, StandardCharsets.UTF_8), forum, pageNum));
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
