package com.example.todoapp.service;

import com.example.todoapp.dao.TaskDao;
import com.example.todoapp.dto.CreatTaskDto;
import com.example.todoapp.dto.TaskDto;
import com.example.todoapp.dto.UpdateTaskDto;
import com.example.todoapp.model.Task;

import java.util.Collection;
import java.util.Optional;

public class TaskService {

    private final TaskDao dao = new TaskDao();

    public TaskDto save(CreatTaskDto dto) {
        Task task = new Task(
                0,
                dto.title(),
                dto.description(),
                false
        );

        Task createdTask = dao.save(task);
        return toDto(createdTask);
    }

    public Optional<TaskDto> findById(int id) {
        return dao.findById(id).map(this::toDto);
    }

    public Collection<TaskDto> findAll() {
        return dao.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public boolean update(int id, UpdateTaskDto dto) {
        Task task = new Task(
                id,
                dto.title(),
                dto.description(),
                dto.done()
        );

        return dao.update(id, task);
    }

    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }

    private TaskDto toDto(Task task) {
        return new TaskDto(
                task.id(),
                task.title(),
                task.description(),
                task.done()
        );
    }
}