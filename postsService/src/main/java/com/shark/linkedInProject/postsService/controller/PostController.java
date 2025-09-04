package com.shark.linkedInProject.postsService.controller;

import com.shark.linkedInProject.postsService.auth.AuthContextHolder;
import com.shark.linkedInProject.postsService.dto.PostCreateRequestDto;
import com.shark.linkedInProject.postsService.dto.PostDto;
import com.shark.linkedInProject.postsService.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(path = "/core")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDto> createPost(
            @RequestPart("post") PostCreateRequestDto postCreateRequestDto,
            @RequestPart("file")MultipartFile file
            ) {
        PostDto postDto = postService.createPost(postCreateRequestDto, file);

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
