package dev.poncio.todolistmeisters.repositories;

import dev.poncio.todolistmeisters.entities.TodoItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoItemRepository extends PagingAndSortingRepository<TodoItem, Long>, JpaRepository<TodoItem, Long> {

    @Query("SELECT t FROM TodoItem t WHERE t.status <> :status AND (t.title LIKE :text OR t.description LIKE :text)")
    List<TodoItem> findByStatusAndContainingText(@Param("status") TodoItem.TodoItemStatus status, @Param("text") String text, Pageable pageable);

    @Query("SELECT count(t) FROM TodoItem t WHERE t.status <> :status AND (t.title LIKE :text OR t.description LIKE :text)")
    Long countByStatusAndContainingText(@Param("status") TodoItem.TodoItemStatus status, @Param("text") String text);

}
