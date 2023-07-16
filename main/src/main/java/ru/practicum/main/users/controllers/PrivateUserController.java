package ru.practicum.main.users.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.eventRequests.model.EventRequestStatusUpdateRequest;
import ru.practicum.main.eventRequests.model.EventRequestStatusUpdateResult;
import ru.practicum.main.subscriptionRequests.dto.SubscriptionRequestDto;
import ru.practicum.main.subscriptionRequests.services.SubscriptionRequestService;
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
                                                             @RequestParam (value = "confirmed", required = true)
                                                                   Boolean isConfirmed,
                                                             @RequestParam (value = "answerSubscribe", required = true)
                                                                   Boolean isAnswerSubscription) {
        log.info("PATCH request for private /users/{}/subscriptions/{}/requests", bloggerId, requestId);
        return subscriptionService.updateSubscriptionRequest(bloggerId, requestId, isConfirmed, isAnswerSubscription);
    }

    @GetMapping("/requests")
    public List<SubscriptionRequestDto> findRequests(@PathVariable Long bloggerId,
                                                             @RequestParam (value = "incoming", required = false,
                                                                     defaultValue = "false") Boolean isIncoming) {
        log.info("GET request for private /users/{}/subscriptions/requests?incoming={}", bloggerId, isIncoming);
        return subscriptionService.findRequests(bloggerId, isIncoming);
    }
}