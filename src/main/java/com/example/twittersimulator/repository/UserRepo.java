package com.example.twittersimulator.repository;

import com.example.twittersimulator.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<Users,Long> {
    Users findByUserName(String userName); //used for login
}
