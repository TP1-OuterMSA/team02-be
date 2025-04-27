package com.example.community_cr.comment_like.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentHeartId implements Serializable {

    private long user;
    private long comment;

    public static CommentHeartId of(long commentId, long userId) {
        return CommentHeartId.builder()
                .comment(commentId)
                .user(userId)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CommentHeartId)) return false;
        CommentHeartId that = (CommentHeartId) o;
        return user == that.user && comment == that.comment;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, comment);
    }
}
