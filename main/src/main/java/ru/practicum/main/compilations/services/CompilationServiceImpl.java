package ru.practicum.main.compilations.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.main.compilations.dto.CompilationDto;
import ru.practicum.main.compilations.dto.NewCompilationDto;
import ru.practicum.main.compilations.dto.UpdateCompilationRequest;
import ru.practicum.main.compilations.mappers.CompilationMapper;
import ru.practicum.main.compilations.model.Compilation;
import ru.practicum.main.compilations.repositories.CompilationJpaRepository;
import ru.practicum.main.events.dto.EventShortDto;
import ru.practicum.main.events.mappers.EventMapper;
import ru.practicum.main.events.model.Event;
import ru.practicum.main.events.repositories.EventJpaRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationJpaRepository compilationRepository;
    private final EventJpaRepository eventRepository;
    private final EventMapper eventMapper;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }

        Set<Event> events = new HashSet<>();
        if (newCompilationDto.getEvents() != null) {
            events = mapEventIdsToObjects(newCompilationDto.getEvents());
        }

        Compilation compilationForSave = compilationMapper.dtoToModel(newCompilationDto);
        compilationForSave.setEvents(events);
        compilationForSave = compilationRepository.save(compilationForSave);

        CompilationDto savedCompilation = compilationMapper.modelToDto(compilationForSave);
        savedCompilation.setEvents(getShortEvents(compilationForSave));
        return savedCompilation;
    }

    @Override
    public CompilationDto findById(Long compilationId) {
        Compilation foundedCompilation = checkAndSetCompilation(compilationId);
        CompilationDto compilationDto = compilationMapper.modelToDto(foundedCompilation);
        compilationDto.setEvents(getShortEvents(foundedCompilation));
        return compilationDto;
    }

    @Override
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = checkAndSetCompilation(compilationId);

        List<Long> events = updateCompilationRequest.getEvents();
        if (events != null) {
            compilation.setEvents(mapEventIdsToObjects(events));
        }

        compilation.setPinned(updateCompilationRequest.getPinned() != null ?
                updateCompilationRequest.getPinned() : compilation.getPinned());

        compilation.setTitle(updateCompilationRequest.getTitle() != null ?
                updateCompilationRequest.getTitle() : compilation.getTitle());

        compilation = compilationRepository.save(compilation);

        CompilationDto updatedCompilation = compilationMapper.modelToDto(compilation);
        updatedCompilation.setEvents(getShortEvents(compilation));
        return updatedCompilation;
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        checkAndSetCompilation(compilationId);
        compilationRepository.deleteById(compilationId);
    }

    @Override
    public List<CompilationDto> findCompilations(Boolean pinned, Pageable pageable) {
        if (pinned != null) {
            return compilationRepository.findAllByPinned(pinned, pageable)
                    .map((compilation) -> {
                        CompilationDto dto = compilationMapper.modelToDto(compilation);
                        dto.setEvents(getShortEvents(compilation));
                        return dto;
                    })
                    .getContent();
        } else {
            return compilationRepository.findAll(pageable)
                    .map((compilation) -> {
                        CompilationDto dto = compilationMapper.modelToDto(compilation);
                        dto.setEvents(getShortEvents(compilation));
                        return dto;
                    })
                    .getContent();
        }
    }

    private Compilation checkAndSetCompilation(Long compilationId) {
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("%s with id = %d does not exist in database", "Compilation", compilationId)));
    }

    private List<EventShortDto> getShortEvents(Compilation compilation) {
        return compilation.getEvents().stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    private Set<Event> mapEventIdsToObjects(List<Long> eventIds) {
        return eventIds.stream()
                .flatMap(ids -> eventRepository.findAllById(Collections.singleton(ids)).stream())
                .collect(Collectors.toSet());
    }
}