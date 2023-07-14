package ru.practicum.main.events.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;
import ru.practicum.main.events.dto.EventSimpleFieldsForUpdate;
import ru.practicum.main.events.model.Event;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public class EventUpdateUtil {
    public static void simpleUpdateEvent(EventSimpleFieldsForUpdate eventDto, Event event) {
        Set<String> nullProperties = getNullProperties(eventDto);
        copyNonNullProperties(eventDto, event, nullProperties);
    }

    private static Set<String> getNullProperties(Object object) {
        Set<String> nullProperties = new HashSet<>();
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(object.getClass());
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String propertyName = propertyDescriptor.getName();
            Object propertyValue = ReflectionUtils.invokeMethod(propertyDescriptor.getReadMethod(), object);
            if (propertyValue == null) {
                nullProperties.add(propertyName);
            }
        }
        return nullProperties;
    }

    private static void copyNonNullProperties(Object source, Object target, Set<String> nullProperties) {
        BeanUtils.copyProperties(source, target, nullProperties.toArray(new String[0]));
    }
}