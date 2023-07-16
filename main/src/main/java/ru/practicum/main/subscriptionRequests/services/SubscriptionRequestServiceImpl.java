package ru.practicum.main.subscriptionRequests.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.main.subscriptionRequests.dto.SubscriptionRequestDto;
import ru.practicum.main.subscriptionRequests.enums.SubscriprionRequestStatus;
import ru.practicum.main.subscriptionRequests.mappers.SubscriptionRequestMapper;
import ru.practicum.main.subscriptionRequests.model.SubscriptionRequest;
import ru.practicum.main.subscriptionRequests.repositories.SubscriptionRequestJpaRepository;
import ru.practicum.main.users.model.User;
import ru.practicum.main.users.repositories.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionRequestServiceImpl implements SubscriptionRequestService {
    private final SubscriptionRequestJpaRepository repository;
    private final UserJpaRepository userRepository;
    private final SubscriptionRequestMapper mapper;

    @Override
    public SubscriptionRequestDto saveRequest(Long subscriberId, Long bloggerId) {
        User subscriber = checkAndGetUser(subscriberId);
        User blogger = checkAndGetUser(bloggerId);
        if (repository.existsByBloggerIdAndSubscriberId(bloggerId, subscriberId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Request is already exists");
        }

        SubscriptionRequest subscriptionRequest = SubscriptionRequest.builder()
                .created(LocalDateTime.now())
                .subscriber(subscriber)
                .blogger(blogger)
                .status(SubscriprionRequestStatus.PENDING)
                .build();

        subscriptionRequest = repository.save(subscriptionRequest);
        return mapper.modelToDto(subscriptionRequest);
    }

    @Override
    public List<SubscriptionRequestDto> updateSubscriptionRequest(Long bloggerId, Long requestId, Boolean isConfirmed,
                                                                  Boolean isAnswerSubscription) {
        User blogger = checkAndGetUser(bloggerId);
        SubscriptionRequest subscriptionRequest = checkAndGetSubscriptionRequest(requestId);
        User originallySubscriber = subscriptionRequest.getSubscriber();
        List<SubscriptionRequestDto> requestDtoList = new ArrayList<>();

        if (!subscriptionRequest.getBlogger().equals(blogger)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not Blogger for that SubscriptionRequest.");
        }

        if (isConfirmed) {
            subscriptionRequest.setStatus(SubscriprionRequestStatus.CONFIRMED);
            repository.save(subscriptionRequest);
            blogger.getSubscribers().add(originallySubscriber);
            userRepository.save(blogger);
            requestDtoList.add(mapper.modelToDto(subscriptionRequest));
        } else {
            subscriptionRequest.setStatus(SubscriprionRequestStatus.REJECTED);
            repository.save(subscriptionRequest);
            requestDtoList.add(mapper.modelToDto(subscriptionRequest));
        }

        if (isAnswerSubscription) {
            SubscriptionRequestDto answerRequest = saveRequest(bloggerId, originallySubscriber.getId());
            requestDtoList.add(answerRequest);
        }
        return requestDtoList;
    }

    @Override
    public List<SubscriptionRequestDto> findRequests(Long bloggerId, Boolean isIncoming) {
        checkAndGetUser(bloggerId);
        List<SubscriptionRequest> requestList;

        if (isIncoming) {
            requestList = repository.findByBloggerId(bloggerId);
        } else {
            requestList = repository.findBySubscriberId(bloggerId);
        }
        return requestList.stream()
                .map(mapper::modelToDto)
                .collect(Collectors.toList());
    }

    private User checkAndGetUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("%s with id = %d does not exist in database", "User", userId)));
    }

    private SubscriptionRequest checkAndGetSubscriptionRequest(Long requestId) {
        return repository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("%s with id = %d does not exist in database", "SubscriptionRequest", requestId)));
    }
}