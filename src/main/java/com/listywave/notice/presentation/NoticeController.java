package com.listywave.notice.presentation;

import com.listywave.common.auth.Auth;
import com.listywave.notice.application.domain.NoticeType;
import com.listywave.notice.application.service.NoticeService;
import com.listywave.notice.application.service.dto.NoticeCreateRequest;
import com.listywave.notice.application.service.dto.NoticeFindAllResponseToAdmin;
import com.listywave.notice.application.service.dto.NoticeFindAllResponseToUser;
import com.listywave.notice.application.service.dto.NoticeFindResponse;
import com.listywave.notice.application.service.dto.NoticeUpdateRequest;
import com.listywave.notice.presentation.dto.NoticeCategoryFindResponse;
import com.listywave.notice.presentation.dto.NoticeCreateResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/admin/notices/categories")
    ResponseEntity<List<NoticeCategoryFindResponse>> findNoticeCategories(@Auth Long adminId) {
        List<NoticeCategoryFindResponse> result = NoticeCategoryFindResponse.toList(NoticeType.values());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/admin/notices")
    ResponseEntity<NoticeCreateResponse> create(@Auth Long adminId, @RequestBody NoticeCreateRequest request) {
        Long id = noticeService.create(adminId, request);
        NoticeCreateResponse response = new NoticeCreateResponse(id);
        return ResponseEntity.created(URI.create("/admin/notices/" + id)).body(response);
    }

    @GetMapping("/admin/notices")
    ResponseEntity<List<NoticeFindAllResponseToAdmin>> findAllToAdmin(@Auth Long adminId) {
        List<NoticeFindAllResponseToAdmin> result = noticeService.findAllToAdmin(adminId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/notices")
    ResponseEntity<List<NoticeFindAllResponseToUser>> findAllToUser() {
        List<NoticeFindAllResponseToUser> result = noticeService.findAllToUser();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/notices/{noticeId}")
    ResponseEntity<NoticeFindResponse> findOneSpecific(@PathVariable Long noticeId) {
        NoticeFindResponse result = noticeService.findOneSpecific(noticeId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/admin/notices/{noticeId}")
    ResponseEntity<Void> update(@Auth Long adminId, @RequestBody NoticeUpdateRequest request, @PathVariable Long noticeId) {
        noticeService.update(adminId, request, noticeId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/admin/notices/{noticeId}")
    ResponseEntity<Void> updateExposure(@Auth Long adminId, @PathVariable Long noticeId) {
        noticeService.updateExposure(adminId, noticeId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/notices/{noticeId}")
    ResponseEntity<Void> delete(@Auth Long adminId, @PathVariable Long noticeId) {
        noticeService.delete(adminId, noticeId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/notices/{noticeId}/alarm")
    ResponseEntity<Void> sendAlarm(@Auth Long adminId, @PathVariable Long noticeId) {
        noticeService.sendAlarm(adminId, noticeId);
        return ResponseEntity.noContent().build();
    }
}
