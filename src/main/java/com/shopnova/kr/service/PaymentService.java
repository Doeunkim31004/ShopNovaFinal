package com.shopnova.kr.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopnova.kr.domain.Order;
import com.shopnova.kr.domain.Payment;
import com.shopnova.kr.repository.OrderRepository;
import com.shopnova.kr.repository.PaymentRepository;

@Service
public class PaymentService {
	
	@Autowired
    private OrderRepository orderRepository;
	
    @Autowired
    private PaymentRepository paymentRepository;

    public Payment processPayment(Long orderId, String paymentMethod, String  paymentAmount) {
    	// 주문 조회 (orderId를 통해)
        Order order = orderRepository.findById(orderId)
                                     .orElseThrow(() -> new IllegalArgumentException("Invalid Order ID"));

        
        // 결제 처리
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);  // 결제 방식 설정
        payment.setPaymentAmount(paymentAmount);  // 결제 금액 설정
        
        // 결제 정보를 저장
        paymentRepository.save(payment);
        
        return payment;
    }
    
 // 전체 결제 내역 조회
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    

    // 특정 기간에 맞는 결제 데이터 조회 (String -> LocalDate 변환)
    public List<Payment> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate) {
        // 날짜 포맷 정의 (yyyy-MM-dd 형식으로 가정)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Repository에서 날짜 범위에 맞는 결제 데이터를 조회
        return paymentRepository.findByPaymentDateBetween(startDate, endDate);
    }
	
}
