package com.listywave.common;

import static com.listywave.user.fixture.UserFixture.동호;
import static com.listywave.user.fixture.UserFixture.서영;
import static com.listywave.user.fixture.UserFixture.유진;
import static com.listywave.user.fixture.UserFixture.정수;

import com.listywave.alarm.application.service.AlarmService;
import com.listywave.alarm.repository.AlarmRepository;
import com.listywave.auth.application.domain.kakao.KakaoOauthClient;
import com.listywave.auth.application.service.AuthService;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.service.CommentService;
import com.listywave.list.application.service.ReplyService;
import com.listywave.list.fixture.ListFixture;
import com.listywave.list.repository.ItemRepository;
import com.listywave.list.repository.comment.CommentRepository;
import com.listywave.list.repository.label.LabelRepository;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.list.repository.reply.ReplyRepository;
import com.listywave.mention.MentionRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.application.service.UserService;
import com.listywave.user.repository.user.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public abstract class IntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(IntegrationTest.class);

    @MockBean
    protected KakaoOauthClient kakaoOauthClient;
    @Autowired
    protected AuthService authService;
    @Autowired
    protected UserService userService;
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
    @Autowired
    protected AlarmService alarmService;
    @Autowired
    protected AlarmRepository alarmRepository;
    @Autowired
    protected ItemRepository itemRepository;
    @Autowired
    protected LabelRepository labelRepository;
    @Autowired
    protected EntityManager em;

    protected User dh, js, ej, sy;
    protected ListEntity list;

    @BeforeEach
    void setUp() {
        dh = userRepository.save(동호());
        js = userRepository.save(정수());
        ej = userRepository.save(유진());
        sy = userRepository.save(서영());
        list = listRepository.save(ListFixture.가장_좋아하는_견종_TOP3(dh, List.of()));
        log.info("=============================테스트 데이터 셋 생성=============================");
    }
}
