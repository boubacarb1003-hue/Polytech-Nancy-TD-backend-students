package com.example.todoapp.dto;

public record UpdateTaskDto(
        String title,
        String description,
        boolean done
) {
}