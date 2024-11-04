package com.example.twittersimulator.controller;

import com.example.twittersimulator.dto.PostRequestDto;
import com.example.twittersimulator.dto.PostResponseDto;
import com.example.twittersimulator.dto.UserPassDto;
import com.example.twittersimulator.entity.Posts;
import com.example.twittersimulator.entity.Users;
import com.example.twittersimulator.service.TwitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class TwitterController {
    @Autowired
    private TwitterService service;

    // Register or save new user
    @PostMapping("register")
    public ResponseEntity<String> userRegistration(@RequestBody UserPassDto request){
        service.saveUser(request);
        return ResponseEntity.status(201).body("User registered successfully");
    }

    //Authenticate the user credentials
    @PostMapping("login")
    public ResponseEntity<Map<String,String>> userAuthentication(@RequestBody UserPassDto request){
        Optional<String> token = service.userValidationAndTokenGeneration(request);
        if (token.isPresent()) {
            Map<String,String> response = new HashMap<>();
            response.put("token",token.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(null);
        }
    }

    //show posts of a user
    @GetMapping("posts")
    public ResponseEntity<List<PostResponseDto>> getUserPosts(@RequestHeader("Authorization") String authorizationHeader){
        String token = authorizationHeader.replace("Bearer ","");
        List<PostResponseDto> posts = service.getPosts(token);
        return ResponseEntity.ok(posts); // Return the posts directly without a conditional check
    }

    //create a new post for a user
    @PostMapping("posts")
    public ResponseEntity<Map<String,String>> createPost(
            @RequestHeader("Authorization") String authorizationHeader, @RequestBody PostRequestDto request){

        String token = authorizationHeader.replace("Bearer ","");
        Boolean postCreated = service.createPost(token,request);
        Map<String,String> response=new HashMap<>();
        if(postCreated){
            response.put("message","post created successfully");
            return ResponseEntity.status(201).body(response);
        } else{
            response.put("error","invalid token");
            return ResponseEntity.status(401).body(response);
        }
    }
}
