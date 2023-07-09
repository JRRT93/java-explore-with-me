package ru.practicum.main.categories.services;

import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.main.categories.dto.CategoryDto;
import ru.practicum.main.categories.mappers.CategoryMapper;
import ru.practicum.main.categories.model.Category;
import ru.practicum.main.categories.repositories.CategoryJpaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryJpaRepository repository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findCategories(Pageable pageable) { //todo засунуть в контроллер
        return repository.findAll(pageable).stream()
                .map(categoryMapper::modelToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto findById(Long categoryId) {
        Category foundedCategory = repository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("%s with id = %d does not exist in database", "Category", categoryId)));
        return categoryMapper.modelToDto(foundedCategory);
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        try {
            return categoryMapper.modelToDto(repository.save(categoryMapper.dtoToModel(categoryDto)));
        } catch (ConstraintViolationException exception){
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("%s not saved due Constraint violations", "Category"));
        }
    }

    @Override
    public CategoryDto update(Long categoryId, CategoryDto categoryDto) {
        Category category = repository.findById(categoryId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."));
        if (repository.existsByName(categoryDto.getName()) && !categoryDto.getName().equals(category.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Name is already used.");
        }
        if (!repository.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found.");
        }
        category.setName(categoryDto.getName());
        repository.save(category);
        return categoryMapper.modelToDto(category);
    }

    @Override
    public void deleteById(Long categoryId) {
        if (repository.existsById(categoryId)) {
            repository.deleteById(categoryId);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("%s with id = %d does not exist in database", "Category", categoryId));
        }
    }
}