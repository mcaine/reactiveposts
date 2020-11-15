package com.mikeycaine.reactiveposts.webapi;

import com.mikeycaine.reactiveposts.service.WebApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.MOVED_PERMANENTLY;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PostLinkHandlingController {
	private final WebApiService webApiService;

	@GetMapping("/showthread.php")
	public Mono<Void> handleSALink(@RequestParam(name="postid") int postId, ServerHttpResponse response) {
		log.info("Mapping SA link to post " + postId);
		response.setStatusCode(MOVED_PERMANENTLY);
		response.getHeaders().setLocation(webApiService.fixPostURL(postId));
		return response.setComplete();
	}
}
