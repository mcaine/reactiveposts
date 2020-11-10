package com.mikeycaine.reactiveposts.webapi;

import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.model.Post;
import com.mikeycaine.reactiveposts.model.Thread;
import com.mikeycaine.reactiveposts.service.WebApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class WebApiController {
	private final WebApiService webApiService;

	@GetMapping("/forums")
	public List<Forum> forums() {
		return webApiService.topLevelForums();
	}

	@PostMapping("/forum/{forumId}/subscribe")
	public Mono<Forum> subscribeToForum(@PathVariable int forumId, ServerWebExchange exchange) {
		return exchange.getFormData()
			.map(formData -> Boolean.valueOf(formData.getFirst("subscribe")))
			.map(status -> webApiService.updateForumSubscriptionStatus(forumId, status));
	}

	@GetMapping("/forum/{forumId}/threads")
	public List<Thread> threadsForForum(@PathVariable int forumId) {
		return webApiService.threadsForForum(forumId);
	}

	@PostMapping("/thread/{threadId}/subscribe")
	public Mono<Thread> subscribeToThread(@PathVariable int threadId, ServerWebExchange exchange) {
		return exchange.getFormData()
			.map(formData -> Boolean.valueOf(formData.getFirst("subscribe")))
			.map(status -> webApiService.updateThreadSubscriptionStatus(threadId, status));
	}

	@GetMapping("/thread/{threadId}/page/{pageId}")
	public List<Post> postsForThreadPage(@PathVariable int threadId, @PathVariable int pageId) {
		return webApiService.postsForThreadPage(threadId, pageId);
	}

}
