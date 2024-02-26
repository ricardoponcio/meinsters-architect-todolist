package dev.poncio.todolistmeisters.services.impl;

import dev.poncio.todolistmeisters.dto.TodoCreateRequest;
import dev.poncio.todolistmeisters.dto.TodoUpdateRequest;
import dev.poncio.todolistmeisters.entities.TodoItem;
import dev.poncio.todolistmeisters.exceptions.BusinessException;
import dev.poncio.todolistmeisters.repositories.TodoItemRepository;
import dev.poncio.todolistmeisters.services.ITodoService;
import dev.poncio.todolistmeisters.utils.DateUtils;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class TodoServiceImpl implements ITodoService {

    @Autowired
    private TodoItemRepository repository;

    @Override
    public Long count(String filter) {
        return this.repository.countByStatusAndContainingText(TodoItem.TodoItemStatus.DELETED, processFilter(filter));
    }

    @Override
    public List<TodoItem> listAll(String filter, int pageNumber, int pageSize) {
        return this.repository.findByStatusAndContainingText(TodoItem.TodoItemStatus.DELETED, processFilter(filter), PageRequest.of(pageNumber, pageSize));
    }

    @Override
    public TodoItem create(TodoCreateRequest todoCreateRequest) {
        if (DateUtils.todayIsWeekEnd()) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Tasks can be created only on weekdays");
        }
        TodoItem todoItem = new TodoItem();
        todoItem.setTitle(todoCreateRequest.getTitle());
        todoItem.setDescription(todoCreateRequest.getDescription());
        todoItem.setCreatedAt(LocalDateTime.now());
        todoItem.setStatus(TodoItem.TodoItemStatus.PENDING);
        return this.repository.save(todoItem);
    }

    @Override
    public TodoItem update(Long id, TodoUpdateRequest todoUpdateRequest) throws Exception {
        statusCheck(id, TodoItem.TodoItemStatus.PENDING);
        Optional<TodoItem> todoItem = this.repository.findById(id);
        if (todoItem.isPresent()) {
            TodoItem item = todoItem.get();
            if (Strings.isNotEmpty(todoUpdateRequest.getTitle()))
                item.setTitle(todoUpdateRequest.getTitle());
            if (Strings.isNotEmpty(todoUpdateRequest.getDescription()))
                item.setDescription(todoUpdateRequest.getDescription());
            return this.repository.save(item);
        }
        return null;
    }

    @Override
    public void delete(Long id) throws Exception {
        statusCheck(id, TodoItem.TodoItemStatus.PENDING);
        lifeTimeCheck(id, 5);
        setStatus(id, TodoItem.TodoItemStatus.DELETED);
    }

    @Override
    public TodoItem markAsCompleted(Long id) throws Exception {
        statusCheck(id, TodoItem.TodoItemStatus.IN_PROGRESS);
        return setStatus(id, TodoItem.TodoItemStatus.COMPLETED);
    }

    @Override
    public TodoItem markAsInProgress(Long id) throws Exception {
        statusCheck(id, TodoItem.TodoItemStatus.PENDING);
        return setStatus(id, TodoItem.TodoItemStatus.IN_PROGRESS);
    }

    private String processFilter(String filter) {
        if (Strings.isEmpty(filter))
            return "%";
        else if (!filter.contains("%"))
            return "%" + filter + "%";
        return filter;
    }

    private void statusCheck(Long id, TodoItem.TodoItemStatus status) throws Exception {
        Optional<TodoItem> todoItem = this.repository.findById(id);
        if (todoItem.isPresent() && todoItem.get().getStatus() != status)
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Current status does not allow this operation");
        else if (!todoItem.isPresent()) throw new EntityNotFoundException();
    }

    private void lifeTimeCheck(Long id, int minDaysAlive) throws Exception {
        Optional<TodoItem> todoItem = this.repository.findById(id);
        if (todoItem.isPresent()) {
            int days = Period.between(todoItem.get().getCreatedAt().toLocalDate(), LocalDateTime.now().toLocalDate()).getDays();
            if (days < minDaysAlive)
                throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Record must exist at least " + minDaysAlive + " days before trying this operation");
        } else throw new EntityNotFoundException();
    }

    private TodoItem setStatus(Long id, TodoItem.TodoItemStatus status) {
        Optional<TodoItem> todoItem = this.repository.findById(id);
        if (todoItem.isPresent()) {
            TodoItem item = todoItem.get();
            item.setStatus(status);
            return this.repository.save(item);
        }
        return null;
    }
}
