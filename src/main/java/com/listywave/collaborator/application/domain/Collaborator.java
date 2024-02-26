package com.listywave.collaborator.application.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.user.application.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Collaborator {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "list_id")
    private ListEntity list;

    public static Collaborator init(User user, ListEntity list) {
        return new Collaborator(null, user, list);
    }

    public boolean hasUser(User other) {
        return this.user.equals(other);
    }

    public String getUserNickname() {
        return user.getNickname();
    }

    public String getUserProfileImageUrl() {
        return user.getProfileImageUrl();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collaborator that = (Collaborator) o;
        return Objects.equals(getUser(), that.getUser()) && Objects.equals(getList(), that.getList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getList());
    }
}
