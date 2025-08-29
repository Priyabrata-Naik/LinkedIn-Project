package com.shark.linkedInProject.postsService.repository;

import com.shark.linkedInProject.postsService.dto.PostDto;
import com.shark.linkedInProject.postsService.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);
}
