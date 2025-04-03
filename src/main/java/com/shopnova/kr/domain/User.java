package com.shopnova.kr.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 자동 증가하는 ID (Primary Key)

    @Column(nullable = false, unique = true, length = 50)
    private String userId;  // 네이버에서 제공하는 사용자 고유 ID

    @Column(nullable = false, unique = true, length = 100)
    private String email;  // 이메일

    @Column(nullable = false, length = 255)
    private String password;  // 비밀번호 (랜덤값으로 저장)

    @Column(nullable = false, length = 50)
    private String name;  // 사용자 이름

    @Column(nullable = false, length = 20)
    private String phone;  // 전화번호

    @Column(length = 255)
    private String address;  // 주소 (nullable 가능)

    @Column(nullable = false, length = 10)
    private String role = "USER";  // 사용자 권한: USER 또는 ADMIN

    @Column(nullable = false, length = 10)
    private String status = "ACTIVE";  // 사용자 상태: ACTIVE 또는 INACTIVE
    
    

    // 관리자 여부 확인
    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }

    // 활성 사용자 여부 확인
    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }

    // 기본 생성자
    public User(String email, String password, String name, String phone) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.address = null; // 기본값 설정
        this.role = "USER"; // 기본값 설정
        this.status = "ACTIVE"; // 기본값 설정
    }
}

