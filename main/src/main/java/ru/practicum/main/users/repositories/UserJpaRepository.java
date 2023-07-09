package ru.practicum.main.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.users.model.User;

import java.util.List;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    Boolean existsByName(String name);

    List<User> findAllByIdIn(List<Long> userIds);
}