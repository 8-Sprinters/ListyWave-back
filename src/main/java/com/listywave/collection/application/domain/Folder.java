package com.listywave.collection.application.domain;

import com.listywave.common.BaseEntity;
import com.listywave.common.exception.CustomException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "folder")
@NoArgsConstructor(access = PROTECTED)
public class Folder extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Embedded
    private FolderName name;

    public static Folder create(Long loginUserId, FolderName folderName) {
        return new Folder(loginUserId, folderName);
    }

    public void update(FolderName folderName, Long userId) {
        validateOwner(userId);
        this.name = folderName;
    }
    public String getFolderName(){
        return this.name.getValue();
    }

    public void validateOwner(Long userId) {
        if (!this.userId.equals(userId)) {
            throw new CustomException(INVALID_ACCESS);
        }
    }
}
