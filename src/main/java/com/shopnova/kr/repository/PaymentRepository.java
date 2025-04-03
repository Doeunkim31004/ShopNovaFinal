package com.shopnova.kr.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopnova.kr.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	  // 특정 기간의 결제 내역 조회
    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    
}
