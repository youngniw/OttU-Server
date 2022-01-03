package com.tave8.ottu.service;

import com.tave8.ottu.entity.Notice;
import com.tave8.ottu.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class NoticeService {
    private final NoticeRepository noticeRepository;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public Notice save(Notice notice) {
        return noticeRepository.save(notice);
    }

    public List<Notice> findAllByUserIdx(Long userIdx) {
        return noticeRepository.findAllByUserIdx(userIdx);
    }
}
