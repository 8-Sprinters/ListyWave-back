package com.listywave.list.presentation.dto.response;

public record ListCreateResponse(Long id){

    public static ListCreateResponse of(Long id){
        return new ListCreateResponse(
                id
        );
    }
}
