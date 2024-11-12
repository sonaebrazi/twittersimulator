package com.example.twittersimulator.repository;

import com.example.twittersimulator.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepo extends JpaRepository<Comments,Long> {
}
