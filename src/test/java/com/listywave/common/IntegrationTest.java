package com.listywave.common;

import static com.listywave.user.fixture.UserFixture.동호;
import static com.listywave.user.fixture.UserFixture.유진;
import static com.listywave.user.fixture.UserFixture.정수;

import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.service.CommentService;
import com.listywave.list.application.service.ReplyService;
import com.listywave.list.fixture.ListFixture;
import com.listywave.list.repository.comment.CommentRepository;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.list.repository.reply.ReplyRepository;
import com.listywave.mention.MentionRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public abstract class IntegrationTest {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ListRepository listRepository;
    @Autowired
    protected CommentService commentService;
    @Autowired
    protected CommentRepository commentRepository;
    @Autowired
    protected MentionRepository mentionRepository;
    @Autowired
    protected ReplyService replyService;
    @Autowired
    protected ReplyRepository replyRepository;

    protected User dh, js, ej;
    protected ListEntity list;

    @BeforeEach
    void setUp() {
        dh = userRepository.save(동호());
        js = userRepository.save(정수());
        ej = userRepository.save(유진());
        list = listRepository.save(ListFixture.가장_좋아하는_견종_TOP3(dh, List.of()));
    }
}
