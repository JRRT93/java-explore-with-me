package ru.practicum.main.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class GetPageableUtil {
    public static Pageable getPageable(Integer from, Integer size) {
        if (size != null && from != null) {
            return PageRequest.of(from / size, size);
        } else {
            return Pageable.unpaged();
        }
    }
}