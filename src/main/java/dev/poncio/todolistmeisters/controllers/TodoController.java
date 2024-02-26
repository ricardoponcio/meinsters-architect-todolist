package dev.poncio.todolistmeisters.controllers;

import dev.poncio.todolistmeisters.dto.TodoCreateRequest;
import dev.poncio.todolistmeisters.dto.TodoUpdateRequest;
import dev.poncio.todolistmeisters.entities.TodoItem;
import dev.poncio.todolistmeisters.services.ITodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
@RequestMapping("/todo")
public class TodoController {

    @Autowired
    private ITodoService iTodoService;

    @GetMapping("/count")
    private Long count(@RequestParam(value = "filter", required = false) String filter) {
        return this.iTodoService.count(filter);
    }

    @GetMapping("/list")
    private List<TodoItem> listAll(@RequestParam(value = "filter", required = false) String filter, @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber, @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return this.iTodoService.listAll(filter, pageNumber, pageSize);
    }

    @PostMapping("/create")
    private TodoItem create(@RequestBody TodoCreateRequest todoCreateRequest) {
        return this.iTodoService.create(todoCreateRequest);
    }

    @PatchMapping("/update/{id}")
    private TodoItem update(@PathVariable("id") Long id, @RequestBody TodoUpdateRequest todoUpdateRequest) throws Exception {
        return this.iTodoService.update(id, todoUpdateRequest);
    }

    @PostMapping("/update/{id}/in_progress")
    private TodoItem markAsInProgress(@PathVariable("id") Long id) throws Exception {
        return this.iTodoService.markAsInProgress(id);
    }

    @PostMapping("/update/{id}/completed")
    private TodoItem markAsCompleted(@PathVariable("id") Long id) throws Exception {
        return this.iTodoService.markAsCompleted(id);
    }

    @DeleteMapping("/delete/{id}")
    private ResponseEntity<?> delete(@PathVariable("id") Long id) throws Exception {
        this.iTodoService.delete(id);
        return ResponseEntity.ok().build();
    }

}
