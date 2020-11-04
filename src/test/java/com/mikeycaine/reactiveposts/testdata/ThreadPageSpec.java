package com.mikeycaine.reactiveposts.testdata;

import com.mikeycaine.reactiveposts.client.ReactiveSAClient;
import com.mikeycaine.reactiveposts.client.WebClientConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
@Slf4j
class ThreadPageSpec implements Directories {
	final int threadId;
	final int pageNum;

	public static ThreadPageSpec of(int threadId, int pageNum) {
		return new ThreadPageSpec(threadId, pageNum);
	}

	public URL url() throws MalformedURLException {
		return new URL(
			String.format("https://forums.somethingawful.com/showthread.php?threadid=%d&perpage=40&pagenumber=%d",
				threadId, pageNum));
	}

	public Path threadPagePath() {
		return Path.of(
			threadsDir,
			String.format("thread%d_page%d.html", threadId, pageNum)
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
}
