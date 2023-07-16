package ru.practicum.main.subscriptionRequests.services;

import ru.practicum.main.subscriptionRequests.dto.SubscriptionRequestDto;

import java.util.List;

public interface SubscriptionRequestService {
    SubscriptionRequestDto saveRequest(Long subscriberId, Long bloggerId);
    List<SubscriptionRequestDto> updateSubscriptionRequest(Long bloggerId, Long requestId, Boolean isConfirmed,
                                                           Boolean isAnswerSubscription);

    List<SubscriptionRequestDto> findRequests(Long bloggerId, Boolean isIncoming);
}