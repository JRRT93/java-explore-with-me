package ru.practicum.main.users.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.subscriptionRequests.dto.SubscriptionRequestDto;
import ru.practicum.main.subscriptionRequests.services.SubscriptionRequestService;
import ru.practicum.main.users.dto.UserPrivacyConfig;
import ru.practicum.main.users.dto.UserShortDto;
import ru.practicum.main.users.services.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{bloggerId}/subscriptions")
@Slf4j
public class PrivateUserController {
    private final UserService userService;
    private final SubscriptionRequestService subscriptionService;

    @PatchMapping("/requests/{requestId}")
    public List<SubscriptionRequestDto> updateStatusRequests(@PathVariable Long bloggerId,
                                                             @PathVariable Long requestId,
                                                             @RequestParam(value = "confirmed", required = true)
                                                             Boolean isConfirmed,
                                                             @RequestParam(value = "answerSubscribe", required = true)
                                                             Boolean isAnswerSubscription) {
        log.info("PATCH request for private /users/{}/subscriptions/{}/requests", bloggerId, requestId);
        return subscriptionService.updateSubscriptionRequest(bloggerId, requestId, isConfirmed, isAnswerSubscription);
    }

    @GetMapping("/requests")
    public List<SubscriptionRequestDto> findRequests(@PathVariable Long bloggerId,
                                                     @RequestParam(value = "incoming", required = false,
                                                             defaultValue = "false") Boolean isIncoming) {
        log.info("GET request for private /users/{}/subscriptions/requests?incoming={}", bloggerId, isIncoming);
        return subscriptionService.findRequests(bloggerId, isIncoming);
    }

    @PatchMapping("/{requestId}/cancel")
    public SubscriptionRequestDto cancelRequest(@PathVariable Long bloggerId, @PathVariable Long requestId) {
        log.info("PATCH request for /users/{}/subscriptions/{}/cancel received", bloggerId, requestId);
        return subscriptionService.cancelRequest(bloggerId, requestId);
    }

    @GetMapping()
    public List<UserShortDto> findSubscribes(@PathVariable Long bloggerId,
                                             @RequestParam(value = "incoming", required = false,
                                                     defaultValue = "false") Boolean isIncoming) {
        log.info("GET request for private /users/{}/subscriptions?incoming={}", bloggerId, isIncoming);
        return userService.findSubscribes(bloggerId, isIncoming);
    }

    @PatchMapping("/privacyConfig")
    public UserPrivacyConfig updatePrivaceConfiguration(@PathVariable Long bloggerId, @RequestBody UserPrivacyConfig config) {
        log.info("PATCH request for /users/{}/subscriptions/privacyConfig", bloggerId);
        return userService.updatePrivacyConfiguration(bloggerId, config);
    }

    @PatchMapping("/blacklist/{subscriberId}")
    public List<UserShortDto> putSubscriberInBlackList(@PathVariable Long bloggerId, @PathVariable Long subscriberId) {
        log.info("PATCH request for /users/{}/subscriptions/blacklist/{}", bloggerId, subscriberId);
        return userService.putSubscriberInBlackList(bloggerId, subscriberId);
    }
}