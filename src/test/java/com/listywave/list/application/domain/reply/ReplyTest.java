package com.listywave.list.application.domain.reply;

import static com.listywave.list.fixture.ListFixture.가장_좋아하는_견종_TOP3;
import static com.listywave.user.fixture.UserFixture.동호;
import static com.listywave.user.fixture.UserFixture.정수;
import static java.util.Collections.EMPTY_LIST;
import static org.assertj.core.api.Assertions.assertThat;

import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.comment.CommentContent;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.user.application.domain.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Reply는 ")
class ReplyTest {

    private final User user = 동호();
    private final ListEntity list = 가장_좋아하는_견종_TOP3(user, List.of());
    private final CommentContent content = new CommentContent("댓글");
    private final Comment comment = new Comment(list, user, content, EMPTY_LIST);
    private final Reply reply = new Reply(comment, user, content);

    @Test
    void 작성자가_같은지_판별한다() {
        User other = 정수();

        assertThat(reply.isOwner(user)).isTrue();
        assertThat(reply.isOwner(other)).isFalse();
    }

    @Test
    void 내용을_수정한다() {
        String newContent = "수정!";
        CommentContent newCommentContent = new CommentContent(newContent);

        reply.update(newCommentContent);

        assertThat(reply.getCommentContent()).isEqualTo(newContent);
    }
}
