package com.listywave.list.repository.reply;

import com.listywave.list.application.domain.list.ListEntity;

public interface CustomReplyRepository {

    Long countByList(ListEntity list);
}
