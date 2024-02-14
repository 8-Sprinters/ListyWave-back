package com.listywave.list.repository.reply;

import com.listywave.list.application.domain.ListEntity;

public interface CustomReplyRepository {

    Long countByList(ListEntity list);
}
