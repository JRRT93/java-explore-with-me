package ru.practicum.main.compilations.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.compilations.dto.CompilationDto;
import ru.practicum.main.compilations.dto.NewCompilationDto;
import ru.practicum.main.compilations.model.Compilation;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    @Mapping(target = "events", ignore = true)
    Compilation dtoToModel(NewCompilationDto dto);

    @Mapping(target = "events", ignore = true)
    CompilationDto modelToDto(Compilation model);
}