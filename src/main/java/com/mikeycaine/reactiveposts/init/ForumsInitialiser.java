package com.mikeycaine.reactiveposts.init;

import com.mikeycaine.reactiveposts.service.UpdatesService;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ForumsInitialiser implements ApplicationListener<ApplicationReadyEvent> {
	private final UpdatesService updatesService;

	@Override
	public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
		updatesService.updateForums();
		updatesService.startUpdating();
	}
}

