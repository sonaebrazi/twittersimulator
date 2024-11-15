package com.example.twittersimulator.service;

import com.example.twittersimulator.dto.*;
import com.example.twittersimulator.entity.Comments;
import com.example.twittersimulator.entity.Posts;
import com.example.twittersimulator.entity.Token;
import com.example.twittersimulator.entity.Users;
import com.example.twittersimulator.repository.CommentRepo;
import com.example.twittersimulator.repository.PostRepo;
import com.example.twittersimulator.repository.TokenRepo;
import com.example.twittersimulator.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    private CommentRepo commentRepo;

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
            String userName=tokenEntity.getUser().getUserName();
            List<Posts> posts= postRepo.findByUser(tokenEntity.getUser());
            List<PostResponseDto> response = new ArrayList<>();
            for(Posts post : posts){
                PostResponseDto dto=new PostResponseDto(post.getId(), post.getContent(),post.getCreatedAt(),userName);
                response.add(dto);
            }
            return response;
        }
    }

    public CommentResponseDto addComment(Long postId, String comment, String token) {
        //retrieve the post by id
        Posts post=postRepo.findById(postId).orElseThrow(() -> new RuntimeException("post not found"));

        //retrieve the token and related user
        Token foundToken=tokenRepo.findByToken(token);
        if(token==null){
            throw new RuntimeException("invalid token");
        }

        Users user=foundToken.getUser();

        //create a new comment
        Comments newComment=new Comments();
        newComment.setComment(comment);
        newComment.setCreatedAt(LocalDateTime.now());
        newComment.setPost(post);
        newComment.setUser(user);

        //save the comment in database
        Comments savedComment=commentRepo.save(newComment);

        // Create and populate the response DTO
        CommentResponseDto response= new CommentResponseDto();
        response.setId(savedComment.getId());
        response.setComment(savedComment.getComment());
        response.setCreatedAt(savedComment.getCreatedAt());

        // Constructing user DTO to return
        UserResponseDto userResponse= new UserResponseDto();
        userResponse.setId(user.getId());
        userResponse.setUserName(user.getUserName());
        response.setUser(userResponse);

        //constructing post DTO to return
        PostResponseDto postResponse= new PostResponseDto();
        postResponse.setId(post.getId());
        postResponse.setContent(post.getContent());
        postResponse.setCreatedAt(post.getCreatedAt());
        postResponse.setUserName(post.getUser().getUserName());
        response.setPost(postResponse);

        return response;
    }

}
