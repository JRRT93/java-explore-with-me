package ru.practicum.main.subscriptionRequests.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.subscriptionRequests.dto.SubscriptionRequestDto;
import ru.practicum.main.subscriptionRequests.services.SubscriptionRequestService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{subscriberId}/subscriptions")
@Slf4j
public class SubscriptionRequestController {
    private final SubscriptionRequestService service;
    @PostMapping("/{bloggerId}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public SubscriptionRequestDto saveRequest(@PathVariable Long subscriberId, @PathVariable Long bloggerId) {
        log.info("POST request for /users/{}/subscriptions/{} received.", subscriberId, bloggerId);
        return service.saveRequest(subscriberId, bloggerId);
    }
}