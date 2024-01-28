package com.listywave.list.presentation;

import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import com.listywave.user.vo.Nickname;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/lists")
public class ListController {

    @GetMapping
    public Nickname test(){
        Nickname nickname = new Nickname("12333333333333333333333333333");
        return nickname;
    }



}
