package dev.poncio.todolistmeisters.services;

import dev.poncio.todolistmeisters.dto.TodoCreateRequest;
import dev.poncio.todolistmeisters.dto.TodoUpdateRequest;
import dev.poncio.todolistmeisters.entities.TodoItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ITodoService {

    Long count(String filter);

    List<TodoItem> listAll(String filter, int page, int pageLength);

    TodoItem create(TodoCreateRequest todoCreateRequest);

    TodoItem update(Long id, TodoUpdateRequest todoUpdateRequest) throws Exception;

    void delete(Long id) throws Exception;

    TodoItem markAsCompleted(Long id) throws Exception;

    TodoItem markAsInProgress(Long id) throws Exception;

}
