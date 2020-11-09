package com.mikeycaine.reactiveposts.testdata;

import com.mikeycaine.reactiveposts.client.ReactiveSAClient;
import com.mikeycaine.reactiveposts.client.WebClientConfig;
import com.mikeycaine.reactiveposts.client.content.ThreadsIndexContent;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Slf4j
abstract class TestPage<T> implements TestDirectories {
	abstract protected URL url() throws MalformedURLException;
	abstract protected Path targetPath();
	abstract protected T result(String fileContent);

	public void cachePage() {
		Path path = this.targetPath();
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

	public Mono<T> cachedContentMono() {
		try {
			Path path = this.targetPath();
			if (Files.exists(path)) {
				String fileContent = Files.readString(targetPath(), StandardCharsets.UTF_8);
				return Mono.just(result(fileContent));
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
