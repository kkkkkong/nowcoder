package com.coder.community.utils;

import com.coder.community.NowcoderApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ContextConfiguration(classes = NowcoderApplication.class)
class MailClientTest {
//    @Autowired
    @Resource
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    void sendMail() {
        mailClient.sendMail("376634352@qq.com", "test", "welcome.");
    }
    @Test
    void sendMailHtml() {
        Context context = new Context();
        context.setVariable("username", "kong");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);
        mailClient.sendMail("376634352@qq.com", "HTML", content);

    }
}