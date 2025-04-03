package com.shopnova.kr.domain;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;  // 필드명을 'id'로 변경
    
    @Column(name = "recipient_name", nullable = false)
    private String recipientName;
    
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;  // LocalDateTime으로 변경

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @Column(name = "total_product_price", nullable = false)
    private String totalProductPrice;
    
    @Column(name = "product_name", nullable = false)
    private String productName;
    
    @Column(name = "product_Quantity", nullable = false)
    private String productQuantity;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    // setUser() 메서드 추가
    public void setUser(User user) {
        this.user = user;
    }
    
    // 생성자 또는 메서드를 통해 orderDate 값 설정
    @PrePersist
    public void prePersist() {
        if (this.orderDate == null) {
            this.orderDate = LocalDateTime.now();  // 주문 날짜가 없으면 현재 시간으로 설정
        }
    }
	
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)  // Payment와 1:N 관계 설정
    private List<Payment> payments;
}

