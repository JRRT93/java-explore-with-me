package ru.practicum.main.subscriptionRequests.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.subscriptionRequests.dto.SubscriptionRequestDto;
import ru.practicum.main.subscriptionRequests.model.SubscriptionRequest;

@Mapper(componentModel = "spring")
public interface SubscriptionRequestMapper {
    @Mapping(target = "subscriber", source = "subscriber.id")
    @Mapping(target = "blogger", source = "blogger.id")
    SubscriptionRequestDto modelToDto(SubscriptionRequest request);
}