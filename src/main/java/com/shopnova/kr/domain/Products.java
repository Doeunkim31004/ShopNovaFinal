package com.shopnova.kr.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter @Setter
public class Products {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Long id;
	
	 @Column(name = "name", nullable = false) 
	 private String name;
	 
	 @Column(name = "price", nullable = false)
	 private double price;
	 
	 @Column(name = "description")
	 private String description;
	 
	 @Column(name = "discount_price")
	 private BigDecimal discountPrice;

	 @Column(name = "shipping_cost")
	 private BigDecimal shippingCost;
	 
	 @Column(name="status")
	 @Enumerated(EnumType.STRING)
	 private ProductsStatus status;

	 @Column(name = "created_at", updatable = false) 
	 private LocalDateTime createdAt;

	 @Column(length = 3000, name = "updated_at")
	 private LocalDateTime updatedAt;
	
	 @Column(length = 3000, name = "image")
	 private String image;
	 
	 @Column(name = "category")
	 private String category;
	 
	 @Column(name = "rating")
	 private Integer rating;
	 
	 @Column(name = "in_cart", nullable = false)
	 private boolean inCart = false;
}
