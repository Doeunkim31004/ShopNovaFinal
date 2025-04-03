package com.shopnova.kr.service;

import com.shopnova.kr.domain.CartItems;
import com.shopnova.kr.domain.User;
import com.shopnova.kr.domain.Products;
import com.shopnova.kr.repository.CartItemsRepository;
import com.shopnova.kr.repository.ProductsRepository;
import com.shopnova.kr.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemsService {

	@Autowired
    private ProductsRepository productsRepository;
	@Autowired
    private CartItemsRepository cartItemsRepository;
	@Autowired
    private UserRepository userRepository;
	
	public List<CartItems> getCartItems(Long userId) {
        // 로그를 찍어서 제대로 호출되는지 확인
        System.out.println("서비스에서 userId: " + userId);

        List<CartItems> cartItems = cartItemsRepository.findByUserId(userId);
        
        // 조회된 장바구니 항목 수 출력
        System.out.println("조회된 장바구니 항목 수: " + cartItems.size());
        return cartItems;
    }
	
	public void removeCartItem(Long cartItemId) {
	    try {
	        cartItemsRepository.deleteById(cartItemId);
	    } catch (EmptyResultDataAccessException e) {
	        throw new RuntimeException("장바구니 항목을 찾을 수 없습니다.");
	    } catch (Exception e) {
	        throw new RuntimeException("장바구니 항목 삭제 중 오류가 발생했습니다.");
	    }
	}
	
	// ✅ 선택한 장바구니 항목 조회 (여러 개)
	
    // 사용자의 장바구니 목록 조회
    public List<CartItems> getCartItemsByUser(User user) {
        return cartItemsRepository.findByUser(user);
    }
    
    public List<CartItems> getItemsByIds(List<Long> itemIds) {
        // itemIds에 해당하는 CartItem들 찾기
        return cartItemsRepository.findAllById(itemIds);
    }

    public CartItems addProductToCart(Long productId, int quantity) {
        Optional<Products> optionalProduct = productsRepository.findById(productId);
        if (!optionalProduct.isPresent()) {
            throw new RuntimeException("상품을 찾을 수 없습니다.");
        }

        Products product = optionalProduct.get(); // Optional에서 실제 Products 객체를 꺼냄

        // 장바구니에 해당 상품이 이미 존재하는지 확인 (user 정보 없이 productId만 사용)
        CartItems existingCartItem = cartItemsRepository.findByProduct(product);
        if (existingCartItem != null) {
            // 이미 장바구니에 존재하면 수량을 업데이트하고 중복 상태를 반환
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            existingCartItem = cartItemsRepository.save(existingCartItem);
            existingCartItem.setIsDuplicate(true); // 중복 상태를 표시하는 필드
            return existingCartItem;
        } else {
            // 새로운 상품 추가
            CartItems newCartItem = new CartItems();
            newCartItem.setProduct(product);
            newCartItem.setQuantity(quantity);
            newCartItem.setInCart(true);
            newCartItem.setIsDuplicate(false); // 새로운 상품은 중복이 아니므로 false
            return cartItemsRepository.save(newCartItem);
        }
    }



    // 장바구니에 상품이 있는지 확인
    public boolean isProductInCart(User user, Long productId) {
        Optional<Products> optionalProduct = productsRepository.findById(productId);
        if (!optionalProduct.isPresent()) {
            throw new RuntimeException("상품을 찾을 수 없습니다.");
        }

        Products product = optionalProduct.get(); // 실제 Products 객체 꺼내기
        return cartItemsRepository.existsByUserAndProduct(user, optionalProduct);
    }

    public void addToCart(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Products product = productsRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // 현재 사용자의 장바구니에 같은 상품이 있는지 확인
        CartItems existingItem = cartItemsRepository.findByUserAndProduct(user, product);
        
        if (existingItem != null) {
            // 이미 존재하는 경우 수량만 증가
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemsRepository.save(existingItem);
        } else {
            // 새로운 상품인 경우 새 CartItems 생성
            CartItems cartItems = new CartItems();
            cartItems.setUser(user);
            cartItems.setProduct(product);
            cartItems.setQuantity(quantity);
            cartItems.setInCart(true);
            cartItemsRepository.save(cartItems);
        }
    }
}
