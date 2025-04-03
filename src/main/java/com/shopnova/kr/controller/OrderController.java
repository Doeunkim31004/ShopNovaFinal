package com.shopnova.kr.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shopnova.kr.domain.Order;
import com.shopnova.kr.domain.Products;
import com.shopnova.kr.domain.User;
import com.shopnova.kr.repository.CartItemsRepository;
import com.shopnova.kr.repository.ProductsRepository;
import com.shopnova.kr.service.OrderService;
import com.shopnova.kr.service.PaymentService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;


@Controller
@RequestMapping("/order")
public class OrderController {
	
    @Autowired
    private ProductsRepository productsRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private CartItemsRepository cartItemsRepository;

    // 주문 폼 화면 표시
    @PostMapping("/orderPayment")
    public String orderPayment(HttpSession session,
                                @RequestParam("selectedItemIds") List<Long> selectedItemIds, 
                                @RequestParam("quantities") List<Integer> quantities, 
                                Model model) {
    	
    	System.out.println("selectedItemIds" + selectedItemIds);
    	System.out.println("quantities" + quantities);

        // 세션에서 현재 사용자 정보 가져오기
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            return "redirect:/users/login"; // 로그인 안 되어 있으면 로그인 페이지로
        }

        // 상품 정보 가져오기
        List<Products> products = productsRepository.findByIdIn(selectedItemIds);

        // 총 금액 계산
        double totalAmount = 0;

        // 상품과 수량을 매칭하여 모델에 전달할 Map 만들기
        Map<Products, Integer> selectedProducts = new HashMap<>();
        for (int i = 0; i < products.size(); i++) {
            Products product = products.get(i);
            Integer quantity = quantities.get(i);

            // 상품과 수량을 Map에 저장
            selectedProducts.put(product, quantity);

            // 총 금액 계산 (가격 * 수량)
            totalAmount += product.getPrice() * quantity;
        }

        // 모델에 상품과 수량, 총 금액을 추가
        model.addAttribute("selectedProducts", selectedProducts);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("currentUser", currentUser);

        return "orderPayment"; // 결제 화면으로 이동
    }
    
    @PostMapping("/buyNow")
    public String buyNow(HttpSession session,
                         @RequestParam("productId") Long productId, 
                         @RequestParam("quantity") Integer quantity, 
                         Model model) {
        
        System.out.println("바로구매 - productId: " + productId);
        System.out.println("바로구매 - quantity: " + quantity);

        // 현재 사용자 확인
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/users/login"; // 로그인 안 되어 있으면 로그인 페이지로 이동
        }

        // 상품 정보 가져오기
        Products product = productsRepository.findById(productId).orElse(null);
        if (product == null) {
            model.addAttribute("errorMessage", "해당 상품을 찾을 수 없습니다.");
            return "productDetail"; // 상품 상세 페이지로 다시 이동
        }

        // 상품과 수량을 모델에 추가
        Map<Products, Integer> selectedProducts = new HashMap<>();
        selectedProducts.put(product, quantity);

        // 총 결제 금액 계산
        double totalAmount = product.getPrice() * quantity;

        // 모델에 주문 정보 추가
        model.addAttribute("selectedProducts", selectedProducts);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("currentUser", currentUser);

        return "orderPayment"; // 결제 페이지로 이동
    }
    
    @Transactional
    @PostMapping("/orderCompleted")
    public String orderCompleted(@RequestParam("productIds") List<Long> productIds,
    							  @RequestParam("recipientName") String recipientName,
                                  @RequestParam("address") String address,
                                  @RequestParam("phoneNumber") String phoneNumber,
                                  @RequestParam("productNames") List<String> productNames,
                                  @RequestParam("productQuantities") List<String> productQuantities,
                                  @RequestParam("productPrices") List<String> productPrices,
                                  HttpSession session, Model model) {
    	ResponseEntity.ok("결제 완료 처리 성공");
        // 현재 사용자 정보 가져오기
        User currentUser = (User) session.getAttribute("currentUser");
        String paymentMethod = "KG_Inicis";

        // 받은 데이터 출력 (디버깅용)
        System.out.println("상품 IDs: " + productIds);
        System.out.println("받는 사람: " + recipientName);
        System.out.println("주소: " + address);
        System.out.println("전화번호: " + phoneNumber);
        System.out.println("결제 방식: " + paymentMethod);
        System.out.println("상품 이름: " + productNames);
        System.out.println("상품 수량: " + productQuantities);
        System.out.println("상품 가격: " + productPrices);
        
        // 주문 처리 서비스 호출 (주문 저장)
        List<Order> orders =  orderService.processOrder(currentUser, recipientName, address, phoneNumber, 
                                  productNames, productQuantities, productPrices);

        // 각 주문의 orderId와 결제 금액으로 결제 처리
        for (Order order : orders) {
            Long orderId = order.getId();  // 각 주문 객체에서 orderId 가져오기
            String totalAmount = order.getTotalProductPrice();  // 각 주문의 총 금액 가져오기

            // 결제 처리
            paymentService.processPayment(orderId, paymentMethod, totalAmount);  // 각 주문의 orderId로 결제 처리
        }

        // 장바구니에서 상품 삭제 처리
        cartItemsRepository.deleteByUserAndProductIdIn(currentUser, productIds);  // 해당 상품들을 삭제
        // 주문 완료 화면으로 리다이렉트
        return "orderCompleted";  // 주문 완료 화면으로 이동
    }
}

