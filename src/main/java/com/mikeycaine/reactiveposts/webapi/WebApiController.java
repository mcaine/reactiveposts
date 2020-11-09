package com.mikeycaine.reactiveposts.webapi;

import com.mikeycaine.reactiveposts.model.Forum;
import com.mikeycaine.reactiveposts.service.WebApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WebApiController {
	private final WebApiService webApiService;

	@GetMapping("/forums")
	public List<Forum> forums() {
		return webApiService.topLevelForums();
	}
}
