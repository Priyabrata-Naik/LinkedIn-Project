package com.shark.linkedInProject.postsService.controller;

import com.shark.linkedInProject.postsService.auth.AuthContextHolder;
import com.shark.linkedInProject.postsService.dto.PostCreateRequestDto;
import com.shark.linkedInProject.postsService.dto.PostDto;
import com.shark.linkedInProject.postsService.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/core")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @RequestBody PostCreateRequestDto postCreateRequestDto,
            HttpServletRequest httpServletRequest
    ) {
        PostDto postDto = postService.createPost(postCreateRequestDto, 1L);

        return new ResponseEntity<>(postDto, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long postId) {
        Long userId = AuthContextHolder.getCurrentUserId();
        PostDto postDto = postService.getPostById(postId);

        return ResponseEntity.ok(postDto);
    }

    @GetMapping("/users/{userId}/allPosts")
    public ResponseEntity<List<PostDto>> getAllPostsOfUser(@PathVariable Long userId) {
        List<PostDto> posts = postService.getAllPostsOfUser(userId);

        return ResponseEntity.ok(posts);
    }

}
