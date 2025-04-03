package com.shopnova.kr.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        // SMTP 서버 정보
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);  // TLS 포트
        mailSender.setUsername("houtae1234@gmail.com");
        mailSender.setPassword("kbilwsdojiipnnhm");  // 구글 앱 비밀번호
        
        // 프로토콜 설정
        mailSender.setProtocol("smtp");
        
        // JavaMailSender의 추가적인 속성 설정
        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.smtp.auth", "true");  // 인증 설정
        properties.put("mail.smtp.starttls.enable", "true");  // TLS 사용
        properties.put("mail.smtp.starttls.required", "true");  // TLS 필수
        properties.put("mail.smtp.socketFactory.port", "587");
        properties.put("mail.smtp.socketFactory.fallback", "false");
        
        // 인코딩 설정
        mailSender.setDefaultEncoding("UTF-8");

        return mailSender;
    }
}


