package ru.practicum.main.categories.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.categories.dto.CategoryDto;
import ru.practicum.main.categories.services.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {
    private final CategoryService service;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CategoryDto saveCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("POST request for /admin/category received. Provided DTO: {}", categoryDto);
        return service.save(categoryDto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId, @RequestBody @Valid CategoryDto categoryDto) {
        log.info("PATCH request for /admin/category/{} received. Provided DTO: {}", catId, categoryDto);
        return service.update(catId, categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("DELETE request for /admin/category/{} received", catId);
        service.deleteById(catId);
        log.info("Category with ID = {} deleted", catId);
    }
}