package com.example.twittersimulator.service;

import com.example.twittersimulator.dto.PostRequestDto;
import com.example.twittersimulator.dto.PostResponseDto;
import com.example.twittersimulator.dto.UserPassDto;
import com.example.twittersimulator.entity.Posts;
import com.example.twittersimulator.entity.Token;
import com.example.twittersimulator.entity.Users;
import com.example.twittersimulator.repository.PostRepo;
import com.example.twittersimulator.repository.TokenRepo;
import com.example.twittersimulator.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TwitterService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TokenRepo tokenRepo;

    @Autowired
    private PostRepo postRepo;

    //register or save a new user
    public void saveUser(UserPassDto request) {
        Users user = new Users();
        user.setUserName(request.getUserName());
        // Encode the password before saving
        String encodedPassword = new BCryptPasswordEncoder().encode(request.getPassWord());
        user.setPassWord(encodedPassword);
        userRepo.save(user);
    }

    //validate user credentials and generate token
    public Optional<String> userValidationAndTokenGeneration(UserPassDto request) {
        String userName=request.getUserName();
        Users existingUser= userRepo.findByUserName(userName);
        if(existingUser!=null && ( new BCryptPasswordEncoder().matches(request.getPassWord(),existingUser.getPassWord()))){
            String token = UUID.randomUUID().toString();
            Token tokenEntity = new Token();
            tokenEntity.setUser(existingUser);
            tokenEntity.setToken(token);
            tokenEntity.setCreatedAt(LocalDateTime.now());
            tokenRepo.save(tokenEntity);
            return Optional.of(token);
        }
        return  Optional.empty();
    }

    public Boolean createPost(String token, PostRequestDto request){
        Token tokenEntity = tokenRepo.findByToken(token);
        if(tokenEntity == null) {
            return false;
        }
        else{
            Posts post = new Posts();
            post.setUser(tokenEntity.getUser());
            post.setContent(request.getContent());
            post.setCreatedAt(LocalDateTime.now());
            postRepo.save(post);
            return true;
        }
    }

    //get posts of a valid token
    public List<PostResponseDto> getPosts(String token) {
        Token tokenEntity = tokenRepo.findByToken(token);
        if(tokenEntity == null){
            return List.of();
        } else {
            List<Posts> posts= postRepo.findByUser(tokenEntity.getUser());
            List<PostResponseDto> response = new ArrayList<>();
            for(Posts post : posts){
                PostResponseDto dto=new PostResponseDto(post.getContent(),post.getCreatedAt());
                response.add(dto);
            }
            return response;
        }
    }

}
