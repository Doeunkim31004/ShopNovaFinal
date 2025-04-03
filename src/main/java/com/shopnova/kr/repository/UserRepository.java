package com.shopnova.kr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.shopnova.kr.domain.User;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    // 모든 사용자 목록을 가져오는 메서드
    List<User> findAll();
    User findByEmail(String email);
    User findByPhone(String phone);
}

