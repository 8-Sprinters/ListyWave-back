package com.listywave.notice.presentation;

import com.listywave.admin.AdminService;
import com.listywave.common.auth.Auth;
import com.listywave.notice.application.domain.NoticeType;
import com.listywave.notice.presentation.dto.NoticeCategoryFindResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final AdminService adminService;

    @GetMapping("/admin/notices/categories")
    ResponseEntity<List<NoticeCategoryFindResponse>> findNoticeCategories(@Auth Long adminId) {
        adminService.validateExist(adminId);
        List<NoticeCategoryFindResponse> result = NoticeCategoryFindResponse.toList(NoticeType.values());
        return ResponseEntity.ok(result);
    }
}
