package ru.practicum.main.categories.mappers;

import org.mapstruct.Mapper;
import ru.practicum.main.categories.dto.CategoryDto;
import ru.practicum.main.categories.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category dtoToModel(CategoryDto dto);

    CategoryDto modelToDto(Category model);
}