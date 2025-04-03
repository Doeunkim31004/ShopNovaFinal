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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cart_items")
@Getter @Setter
public class CartItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Products product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;  // 수량

    @Column(name = "in_cart", nullable = false)
    private Boolean inCart = true;  // 장바구니에 있는 상태 (기본값은 TRUE)
    
 // 삭제 시 상태만 변경하도록 수정
    public void markAsRemoved() {
        this.inCart = false;  // inCart 상태를 false로 변경
    }

    @Transient // DB에 저장되지 않는 임시 필드
    private boolean isDuplicate = false;  // 중복 상품 여부를 확인하는 필드
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;  // 생성일시 (장바구니에 담은 시간)
    
    @Column(name = "price", nullable = false)  // 추가된 price 컬럼
    private double price; // 상품의 가격

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();  // 레코드가 생성되기 전에 현재 시간 자동 설정
        }

        // 가격을 자동으로 설정 (product에서 가격을 가져오기)
        if (this.product != null) {
            this.price = this.product.getPrice();  // 상품의 가격을 price 컬럼에 설정
        }
    }

    // 중복 상품 여부 확인을 위한 getter/setter
    public boolean getIsDuplicate() {
        return this.isDuplicate;
    }

    public void setIsDuplicate(boolean isDuplicate) {
        this.isDuplicate = isDuplicate;
    }

}
