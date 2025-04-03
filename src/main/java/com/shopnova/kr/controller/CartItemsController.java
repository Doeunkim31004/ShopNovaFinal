package com.shopnova.kr.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shopnova.kr.domain.CartItems;
import com.shopnova.kr.domain.User;
import com.shopnova.kr.repository.CartItemsRepository;
import com.shopnova.kr.service.CartItemsService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart-items")
public class CartItemsController {
	
	@Autowired
    private CartItemsService cartItemsService;
	@Autowired
    private CartItemsRepository cartItemsRepository;
	
	@PostMapping("/cart/remove/{cartItemId}")
	public ResponseEntity<String> removeCartItem(@PathVariable("cartItemId") Long cartItemId) {
	    try {
	        Optional<CartItems> cartItem = cartItemsRepository.findById(cartItemId);
	        if (!cartItem.isPresent()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("장바구니 항목을 찾을 수 없습니다.");
	        }
	        cartItemsService.removeCartItem(cartItemId);
	        return ResponseEntity.ok("삭제 성공");
	    } catch (Exception e) {
	        e.printStackTrace(); // 로그 확인을 위해 추가
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패: " + e.getMessage());
	    }
	}
	
	@GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
		 System.out.println("✅ /cart 요청 도착");
        User currentUser = (User) session.getAttribute("currentUser");
        System.out.println(currentUser);

        if (currentUser == null) {
            return "redirect:/users/login";
        }

        // DB에서 현재 사용자의 장바구니 항목 가져오기
        List<CartItems> cartItems = cartItemsRepository.findByUserAndInCartTrue(currentUser);

        // 총 금액 계산
        double totalAmount = 0;
        
        for (CartItems item : cartItems) {
            totalAmount += item.getPrice() * item.getQuantity(); // 가격 * 수량을 더해줍니다.
        }
        // 모델에 데이터를 추가
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("totalAmount", totalAmount); // 총 금액을 모델에 추가
        System.out.println("조회된 장바구니 항목 수: " + cartItems.size());
        System.out.println("장바구니 항목 개수: " + cartItems.size());
        return "cart";  // cart.html 렌더링
    }
}
