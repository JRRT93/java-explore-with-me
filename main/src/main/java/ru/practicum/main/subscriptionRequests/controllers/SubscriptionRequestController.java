package ru.practicum.main.subscriptionRequests.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.events.dto.EventShortDto;
import ru.practicum.main.events.services.EventService;
import ru.practicum.main.subscriptionRequests.dto.SubscriptionRequestDto;
import ru.practicum.main.subscriptionRequests.services.SubscriptionRequestService;
import ru.practicum.main.utils.GetPageableUtil;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{subscriberId}/subscriptions")
@Slf4j
public class SubscriptionRequestController {
    private final SubscriptionRequestService service;
    private final EventService eventService;

    @PostMapping("/{bloggerId}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public SubscriptionRequestDto saveRequest(@PathVariable Long subscriberId, @PathVariable Long bloggerId) {
        log.info("POST request for /users/{}/subscriptions/{} received.", subscriberId, bloggerId);
        return service.saveRequest(subscriberId, bloggerId);
    }

    @GetMapping("/{bloggerId}/created")
    public List<EventShortDto> findBloggersCreatedEvents(@PathVariable Long subscriberId, @PathVariable Long bloggerId,
                                                         @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                         @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("GET request for private /users/{}/subscriptions/{}/created", subscriberId, bloggerId);
        return eventService.findBloggersCreatedEvents(subscriberId, bloggerId, GetPageableUtil.getPageable(from, size));
    }

    @GetMapping("/{bloggerId}/participation")
    public List<EventShortDto> findBloggersParticipationEvents(@PathVariable Long subscriberId, @PathVariable Long bloggerId,
                                                               @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                               @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("GET request for private /users/{}/subscriptions/{}/participation", subscriberId, bloggerId);
        return eventService.findBloggersParticipationEvents(subscriberId, bloggerId, GetPageableUtil.getPageable(from, size));
    }
}