package ru.practicum.main.events.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.events.enums.PublicStatus;
import ru.practicum.main.events.model.Event;

@Repository
public interface EventJpaRepository extends JpaRepository<Event, Long> {
    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Page<Event> findAllByInitiatorIdAndState(Long userId, PublicStatus status, Pageable pageable);

    Page<Event> findAll(Specification<Event> specification, Pageable pageable);
}