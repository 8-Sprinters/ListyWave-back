package com.listywave.list.application.domain.comment;

import static com.listywave.list.fixture.ListFixture.가장_좋아하는_견종_TOP3;
import static com.listywave.user.fixture.UserFixture.동호;
import static com.listywave.user.fixture.UserFixture.정수;
import static java.util.Collections.EMPTY_LIST;
import static org.assertj.core.api.Assertions.assertThat;

import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.user.application.domain.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Comment는 ")
class CommentTest {

    private final User user = 동호();
    private final ListEntity list = 가장_좋아하는_견종_TOP3(user, List.of());
    private final CommentContent content = new CommentContent("댓글");
    private final Comment comment = new Comment(list, user, content, EMPTY_LIST);

    @Test
    void 초기화하면_isDelete는_false이다() {
        assertThat(comment.isDeleted()).isFalse();
    }

    @Test
    void 작성자_여부를_판별한다() {
        User other = 정수();

        assertThat(comment.isOwner(user)).isTrue();
        assertThat(comment.isOwner(other)).isFalse();
    }

    @Test
    void 삭제_처리를_할_수_있다() {
        comment.softDelete();

        assertThat(comment.isDeleted()).isTrue();
    }

    @Test
    void 댓글_내용을_수정할_수_있다() {
        CommentContent newContent = new CommentContent("댓글 수정");

        comment.update(newContent);

        assertThat(comment.getCommentContent()).isEqualTo("댓글 수정");
    }
}
