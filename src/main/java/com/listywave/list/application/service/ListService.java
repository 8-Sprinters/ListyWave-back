package com.listywave.list.application.service;

import com.listywave.collaborator.domain.Collaborator;
import com.listywave.collaborator.domain.repository.CollaboratorRepository;
import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import com.listywave.list.application.dto.ListCreateCommand;
import com.listywave.list.domain.Item;
import com.listywave.list.domain.Lists;
import com.listywave.list.presentation.dto.request.ItemCreateRequest;
import com.listywave.list.presentation.dto.response.ListCreateResponse;
import com.listywave.list.repository.ItemRepository;
import com.listywave.list.repository.ListRepository;
import com.listywave.user.domain.User;
import com.listywave.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ListService {

    private final ListRepository listRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CollaboratorRepository collaboratorRepository;
    public ListCreateResponse listCreate(
            ListCreateCommand listCreateCommand,
            List<Long> collaboratorIds,
            List<ItemCreateRequest> items
    ) {
        //TODO: 글쓰는 회원이 실제 존재하는지 검증 (security 이용해서 해야함)
        User user = userRepository.findById(listCreateCommand.ownerId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "회원이 존재하지 않습니다."));

        //TODO: 글자 길이 검증

        //TODO: 만약 hasCollaboration이 true면 colaboratorIds가 실제 있는 회원인지 검증

        //TODO: 리스트 저장
        Lists list = listRepository.save(Lists.createList(user, listCreateCommand, items));

        List<Item> itemCollect = items.stream()
                .map(v -> Item.createItem(v, list))
                .collect(Collectors.toList());

        List<User> users = userRepository.findAllById(collaboratorIds);
        List<Collaborator> collaborators = users.stream()
                .map(v -> Collaborator.createCollaborator(v, list))
                .collect(Collectors.toList());

        itemRepository.saveAll(itemCollect);
        collaboratorRepository.saveAll(collaborators);
        return ListCreateResponse.of(list.getId());
    }
}
