package com.listywave.user.application.domain;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Follow extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "following_user_id")
    private User followingUser;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "follower_user_id")
    private User followerUser;
}
