package ru.practicum.main.subscriptionRequests.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.subscriptionRequests.model.SubscriptionRequest;

import java.util.List;

@Repository
public interface SubscriptionRequestJpaRepository extends JpaRepository<SubscriptionRequest, Long> {
    Boolean existsByBloggerIdAndSubscriberId(Long bloggerId, Long subscriberId);

    List<SubscriptionRequest> findBySubscriberId(Long subscriberId);

    List<SubscriptionRequest> findByBloggerId(Long bloggerId);
}