package com.example.todoapp.dto;

public record TaskDto(
        int id,
        String title,
        String description,
        boolean done
) {
}