package dev.poncio.todolistmeisters.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Table(name = "todoitem")
@Entity
@Data
public class TodoItem {

    public static enum TodoItemStatus {
        PENDING, IN_PROGRESS, COMPLETED, DELETED;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private TodoItemStatus status;

}