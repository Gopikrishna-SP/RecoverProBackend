    package com.nimis.chatbot.service;

    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;

    @SpringBootTest
    class EmailServiceTest {

        @Autowired
        private EmailService emailService;

        @Test
        void testSendOtp() {
            emailService.sendOtp("krish.official365@gmail.com", "123456", 10);
            System.out.println("Email sent!");
        }
    }