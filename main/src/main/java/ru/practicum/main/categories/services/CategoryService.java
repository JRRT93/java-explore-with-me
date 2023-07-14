package ru.practicum.main.categories.services;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.categories.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto findById(Long categoryId);

    CategoryDto update(Long categoryId, CategoryDto categoryDto);

    void deleteById(Long catId);

    List<CategoryDto> findCategories(Pageable pageable);
}