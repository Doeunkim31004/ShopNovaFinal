package com.shopnova.kr.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopnova.kr.domain.Order;
import com.shopnova.kr.domain.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
	 // 로그인한 사용자의 주문 목록 가져오기
    List<Order> findByUser(User user);

    // 특정 상태를 가진 주문 목록 가져오기
    List<Order> findByUserAndStatus(User user, String status);
    
    // orderDate가 특정 날짜 범위 내에 있는 주문을 찾는 메서드 정의
    List<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);

}
