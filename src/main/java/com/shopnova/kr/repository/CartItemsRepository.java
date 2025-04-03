package com.shopnova.kr.repository;


import com.shopnova.kr.domain.CartItems;
import com.shopnova.kr.domain.User;
import com.shopnova.kr.domain.Products;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, Long> {

    // 특정 사용자의 장바구니 목록 조회
    List<CartItems> findByUser(User user);
    
    // 특정 사용자와 장바구니에 있는 상품을 찾는 메서드
    List<CartItems> findByUserAndInCartTrue(User user);

    // 특정 사용자의 장바구니에서 특정 상품을 찾기
    CartItems findByUserAndProduct(User user, Products product);

    // 장바구니에 상품이 있는지 확인
    boolean existsByUserAndProduct(User user, Optional<Products> product);
    CartItems findByProduct(Products product);

    List<CartItems> findByUserId(Long userId); 
    
    Optional<CartItems> findById(Long id);
    
    public List<CartItems> findByCartItemIdInAndUser(List<Long> cartItemIds, User user);
    
    // User와 여러 Product ID에 해당하는 CartItems 삭제
    void deleteByUserAndProductIdIn(User user, List<Long> productIds);
}

