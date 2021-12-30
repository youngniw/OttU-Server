package com.tave8.ottu.service;

import com.tave8.ottu.entity.Platform;
import com.tave8.ottu.entity.Recruit;
import com.tave8.ottu.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
@Transactional
class RecruitServiceTest {
    @Autowired
    private RecruitService recruitService;

    @Test
    void upload() throws Exception {
        Recruit recruit = new Recruit();
        Platform platform = new Platform();
        platform.setPlatformIdx(2);
        recruit.setPlatform(platform);
        User writer = new User();
        writer.setUserIdx(1L);
        recruit.setWriter(writer);
        recruit.setHeadcount(2);

        Recruit uploadRecruit = recruitService.upload(recruit);

        System.out.println(uploadRecruit.toString());
        Assertions.assertThat(uploadRecruit.getPlatform().getPlatformIdx()).isEqualTo(2);
        Assertions.assertThat(uploadRecruit.getWriter().getUserIdx()).isEqualTo(1L);
        Assertions.assertThat(uploadRecruit.getHeadcount()).isEqualTo(2);
    }

    @Test
    void findAllByPlatform() throws Exception {
        List<Recruit> recruitByPlatform = recruitService.findAllByPlatform(1);
        System.out.println(recruitByPlatform.get(0).toString());
        System.out.println(recruitByPlatform.size());
    }

    @Test
    void findAllByWriter() throws Exception {
        List<Recruit> recruitByWriter = recruitService.findAllByWriter(1L);
        recruitByWriter.forEach(recruit -> recruit.setChoiceNum(recruitService.findRecruitChoiceNum(recruit.getRecruitIdx())));
        System.out.println(recruitByWriter);
        System.out.println(recruitByWriter.size());
    }
}
