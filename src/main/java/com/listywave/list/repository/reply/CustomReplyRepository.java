package com.listywave.list.repository.reply;

import com.listywave.list.application.domain.Lists;

public interface CustomReplyRepository {

    Long countByList(Lists list);
}
