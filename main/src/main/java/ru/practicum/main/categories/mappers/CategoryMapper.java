package ru.practicum.main.categories.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.categories.dto.CategoryDto;
import ru.practicum.main.categories.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "name", source = "dto.name")
    Category dtoToModel(CategoryDto dto);

    @Mapping(target = "id", source = "model.id")
    @Mapping(target = "name", source = "model.name")
    CategoryDto modelToDto(Category model);
}