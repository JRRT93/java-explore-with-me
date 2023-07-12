package ru.practicum.main.compilations.services;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.compilations.dto.CompilationDto;
import ru.practicum.main.compilations.dto.NewCompilationDto;
import ru.practicum.main.compilations.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto saveCompilation(NewCompilationDto newCompilationDto);

    CompilationDto findById(Long compilationId);

    CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest updateCompilationRequest);

    void deleteCompilation(Long compilationId);

    List<CompilationDto> findCompilations(Boolean pinned, Pageable pageable);
}