package com.listywave.collection.application.domain;

import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS;
import static com.listywave.user.fixture.UserFixture.동호;
import static com.listywave.user.fixture.UserFixture.정수;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.listywave.common.exception.CustomException;
import com.listywave.user.application.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Folder는 ")
class FolderTest {

    private final User user = 정수();
    private final FolderName name = new FolderName("맛집");
    private final Folder folder = Folder.create(user.getOauthId(), name);

    @Test
    void 폴더의_생성자가_동일한지_검증한다(){
        User other = 동호();

        CustomException exception = assertThrows(CustomException.class, () -> folder.validateOwner(other.getOauthId()));
        assertThat(exception.getErrorCode()).isEqualTo(INVALID_ACCESS);

        assertThatNoException().isThrownBy(() -> folder.validateOwner(user.getOauthId()));
    }

    @Test
    void 소유자만_폴더명을_수정할_수_있다(){
        FolderName newName = new FolderName("예카");

        folder.update(newName, 정수().getOauthId());

        assertThat(folder.getFolderName()).isEqualTo(newName.getValue());
    }

    @Test
    void 소유자가_아니면_폴더명을_수정할_수_없다(){
        FolderName newName = new FolderName("예카");

        CustomException exception = assertThrows(CustomException.class, () -> folder.update(newName, 동호().getOauthId()));

        assertThat(exception.getErrorCode()).isEqualTo(INVALID_ACCESS);
    }
}
