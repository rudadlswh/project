package com.crossfit.web;

import com.crossfit.domain.Notice;
import com.crossfit.domain.User;
import com.crossfit.service.NoticeService;
import com.crossfit.service.UserService;
import com.crossfit.web.dto.NoticeDtos;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notices")
public class NoticeController {
    private final NoticeService noticeService;
    private final UserService userService;

    public NoticeController(NoticeService noticeService, UserService userService) {
        this.noticeService = noticeService;
        this.userService = userService;
    }

    @GetMapping
    public List<NoticeDtos.NoticeResponse> list() {
        return noticeService.list().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public NoticeDtos.NoticeResponse get(@PathVariable Long id) {
        return toResponse(noticeService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public NoticeDtos.NoticeResponse create(@Valid @RequestBody NoticeDtos.CreateNoticeRequest req) {
        User user = userService.getCurrentUser();
        Notice notice = noticeService.create(user, req);
        return toResponse(notice);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public NoticeDtos.NoticeResponse update(@PathVariable Long id,
                                            @Valid @RequestBody NoticeDtos.CreateNoticeRequest req) {
        return toResponse(noticeService.update(id, req));
    }

    private NoticeDtos.NoticeResponse toResponse(Notice notice) {
        NoticeDtos.NoticeResponse res = new NoticeDtos.NoticeResponse();
        res.id = notice.getId();
        res.title = notice.getTitle();
        res.content = notice.getContent();
        res.createdBy = notice.getCreatedBy().getDisplayName();
        return res;
    }
}
