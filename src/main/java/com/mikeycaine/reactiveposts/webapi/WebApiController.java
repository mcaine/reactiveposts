package com.mikeycaine.reactiveposts.webapi;

import com.mikeycaine.reactiveposts.service.WebApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WebApiController {
	private final WebApiService webApiService;
}
