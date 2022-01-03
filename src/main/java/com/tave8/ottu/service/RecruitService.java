package com.tave8.ottu.service;

import com.tave8.ottu.entity.Recruit;
import com.tave8.ottu.entity.Waitlist;
import com.tave8.ottu.repository.RecruitRepository;
import com.tave8.ottu.repository.WaitlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RecruitService {
    private final RecruitRepository recruitRepository;
    private final WaitlistRepository waitlistRepository;

    @Autowired
    public RecruitService(RecruitRepository recruitRepository, WaitlistRepository waitlistRepository) {
        this.recruitRepository = recruitRepository;
        this.waitlistRepository = waitlistRepository;
    }

    public List<Recruit> findAllByPlatform(int platformIdx) {
        return recruitRepository.findAllByPlatformIdx(platformIdx);
    }

    public List<Recruit> findAllByWriter(Long writerIdx) {
        return recruitRepository.findAllByWriterIdx(writerIdx);
    }

    public Recruit upload(Recruit recruit) {
        return recruitRepository.save(recruit);     //save 시에는 연관관계 매핑에 대한 내용이 포함되지 않음!(writer의 닉네임이 들어가지 않음)
    }

    public Optional<Recruit> findRecruitById(Long recruitIdx) {
        return recruitRepository.findById(recruitIdx);
    }

    public Recruit getRecruitById(Long recruitIdx) {
        return recruitRepository.getById(recruitIdx);
    }

    public boolean deleteRecruitById(Long recruitIdx) {
        try {
            recruitRepository.deleteById(recruitIdx);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean saveRecruit(Recruit recruit) {
        try {
            recruitRepository.save(recruit);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean participate(Waitlist waitlist) {
        try {
            waitlistRepository.save(waitlist);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Waitlist> findRecruitWaitlist(Long recruitIdx) {
        return waitlistRepository.findAllByRecruitIdxOrderByIsAcceptedDesc(recruitIdx);
    }

    public List<Waitlist> findAcceptedWaitlist(Long recruitIdx) {
        return waitlistRepository.findAllByRecruitIdxAndIsAcceptedTrue(recruitIdx);
    }

    public Long findRecruitChoiceNum(Long recruitIdx) {
        return waitlistRepository.countAllByRecruit_RecruitIdxAndIsAcceptedTrue(recruitIdx) + 1;
    }

    public Waitlist getWaitlistById(Long waitlistIdx) {
        return waitlistRepository.getById(waitlistIdx);
    }

    public boolean saveWaitlistIsAccepted(Waitlist waitlist) {
        try {
            waitlistRepository.save(waitlist);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteNonAcceptedWaitlist(Long recruitIdx) {
        try {
            waitlistRepository.deleteAllByRecruitIdxAndIsAcceptedFalse(recruitIdx);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
