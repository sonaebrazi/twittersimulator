package com.example.twittersimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String comment;
    private LocalDateTime createdAt;
    private UserResponseDto user;
    private PostResponseDto post;
}
