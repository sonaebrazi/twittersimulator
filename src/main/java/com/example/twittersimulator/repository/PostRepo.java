package com.example.twittersimulator.repository;

import com.example.twittersimulator.entity.Posts;
import com.example.twittersimulator.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepo extends JpaRepository<Posts,Long> {
    List<Posts> findByUser(Users user);
}
