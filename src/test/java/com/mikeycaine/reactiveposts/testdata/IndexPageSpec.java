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
class IndexPageSpec implements Directories {
	final int forumId;

	public static IndexPageSpec of(int forumId) {
		return new IndexPageSpec(forumId);
	}

	public URL url() throws MalformedURLException {
		return new URL(String.format("https://forums.somethingawful.com/forumdisplay.php?forumid=%d", forumId));
	}

	public Path indexPagePath() {
		return Path.of(
			indexesDir,
			String.format("forumIndex_%d.html", forumId)
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
}
