package ru.practicum.main.categories.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.categories.model.Category;

@Repository
public interface CategoryJpaRepository extends JpaRepository<Category, Long> {
    Boolean existsByName(String name);
}