package com.shark.linkedInProject.postsService.event;

import lombok.Builder;
import lombok.Data;

@Data
public class PostLiked {

    private Long postId;

    private Long likedByUserId;

    private Long ownerUserId;

}
