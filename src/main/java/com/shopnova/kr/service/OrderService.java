package com.shopnova.kr.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopnova.kr.domain.Order;
import com.shopnova.kr.domain.User;
import com.shopnova.kr.repository.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // 주문 처리 로직
    public List<Order> processOrder(User currentUser, String recipientName, String deliveryAddress, String phoneNumber, 
    							List<String> productNames, List<String> productQuantities, List<String> productPrices) {
    	List<Order> orders= new ArrayList<>();  // 주문 ID들을 저장할 리스트
    	
        // 각 상품에 대한 처리
        for (int i = 0; i < productNames.size(); i++) {
            String productName = productNames.get(i);
            String productQuantity = productQuantities.get(i);
            String productPrice = productPrices.get(i);

            // 상품의 총 가격 계산 (가격 * 수량)
            BigDecimal totalProductPrice = new BigDecimal(productPrice).multiply(new BigDecimal(productQuantity));

            // 1. Order 객체 생성
            Order order = new Order();
            order.setRecipientName(recipientName);  // 받는 사람 이름
            order.setDeliveryAddress(deliveryAddress);  // 받는 사람 주소
            order.setPhoneNumber(phoneNumber);  // 받는 사람 전화번호
            order.setStatus("PROCESSING");  // 주문 상태
            order.setProductName(productName);  // 상품 이름
            order.setProductQuantity(productQuantity);  // 상품 수량
            order.setTotalProductPrice(totalProductPrice.toString());  // 상품 총 가격
            order.setUser(currentUser);  // 사용자 정보 설정

            // 2. 주문 저장
            orderRepository.save(order);
            
            orders.add(order);
        }

        // 결제 처리 등의 추가 로직은 필요에 따라 구현하면 됩니다.
        return orders;
    }
    
    // 사용자에 맞는 주문 목록 가져오기
    public List<Order> getOrdersByUser(User user) {
        // 해당 사용자의 주문만 가져오기
        return orderRepository.findByUser(user);  // 유저에 따른 주문 목록 반환
    }
	
	public List<Order> getOrdersByDateRange(String startDate, String endDate) {
	    // 시작일과 종료일을 Date로 변환
	    LocalDate start = LocalDate.parse(startDate);
	    LocalDate end = LocalDate.parse(endDate);
	    
	    return orderRepository.findByOrderDateBetween(start, end);
	}
}
