package com.shark.linkedInProject.postsService.service;

import com.shark.linkedInProject.postsService.auth.AuthContextHolder;
import com.shark.linkedInProject.postsService.entity.Post;
import com.shark.linkedInProject.postsService.entity.PostLike;
import com.shark.linkedInProject.postsService.event.PostLiked;
import com.shark.linkedInProject.postsService.exception.BadRequestException;
import com.shark.linkedInProject.postsService.exception.ResourceNotFoundException;
import com.shark.linkedInProject.postsService.repository.PostLikeRepository;
import com.shark.linkedInProject.postsService.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostLikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<Long, PostLiked> postLikedKafkaTemplate;

    @Transactional
    public void likePost(Long postId) {
        Long userId = AuthContextHolder.getCurrentUserId();
        log.info("User with id: {} liking the post with id: {}", userId, postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        boolean hasAlreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);

        if (hasAlreadyLiked) throw new BadRequestException("You can't like the post again");

        PostLike postLike = new PostLike();
        postLike.setUserId(userId);
        postLike.setPostId(postId);
        postLikeRepository.save(postLike);

//        Todo: Send notification to the owner of the post
        PostLiked postLiked = PostLiked.builder()
                .likedByUserId(userId)
                .ownerUserId(post.getUserId())
                .postId(postId)
                .build();

        postLikedKafkaTemplate.send("post_liked_topic", postLiked);

    }

    @Transactional
    public void unlikePost(Long postId) {
        Long userId = AuthContextHolder.getCurrentUserId();
        log.info("User with id: {} unliking the post with id: {}", userId, postId);
        postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        boolean hasAlreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);

        if (!hasAlreadyLiked) throw new BadRequestException("You can't unlike the post that you haven't liked");

        postLikeRepository.deleteByUserIdAndPostId(userId, postId);
    }

}
