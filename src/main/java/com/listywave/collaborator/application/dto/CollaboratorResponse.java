package com.listywave.collaborator.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CollaboratorResponse {
    Long id;
    String nickname;
    String profileImageUrl;
}
