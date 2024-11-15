package com.example.twittersimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private String userName;
}
