package com.crossfit.service;

import com.crossfit.domain.Notice;
import com.crossfit.domain.User;
import com.crossfit.repo.NoticeRepository;
import com.crossfit.web.dto.NoticeDtos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @Transactional
    public Notice create(User creator, NoticeDtos.CreateNoticeRequest req) {
        Notice notice = new Notice(req.title, req.content, creator);
        return noticeRepository.save(notice);
    }

    @Transactional
    public Notice update(Long id, NoticeDtos.CreateNoticeRequest req) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found"));
        notice.setTitle(req.title);
        notice.setContent(req.content);
        return notice;
    }

    public List<Notice> list() {
        return noticeRepository.findAllByOrderByCreatedAtDesc();
    }

    public Notice get(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found"));
    }
}
