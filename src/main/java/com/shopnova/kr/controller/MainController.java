package com.shopnova.kr.controller;

import com.shopnova.kr.domain.User;
import com.shopnova.kr.domain.Products;
import com.shopnova.kr.service.UserService;
import com.shopnova.kr.service.ProductsService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class MainController {
	
	@Autowired
    private  UserService userService;
	@Autowired
    private ProductsService productsService;

    // 기본 경로로 접속 시 home.html로 리다이렉트
    @GetMapping("/")
    public String redirectToHome(HttpSession session, Model model) {
        // 로그인 상태 확인
        boolean isLoggedIn = userService.checkLoginStatus(session);

        // 로그인 상태에 따라 모델에 데이터 추가
        if (isLoggedIn) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);  // 로그인된 사용자 정보 모델에 추가
        }

        model.addAttribute("isLoggedIn", isLoggedIn);  // 로그인 상태 정보

        // 상품 목록 페이지 (홈)
        List<Products> products = productsService.findAllProducts();
        model.addAttribute("products", products);

        return "home";  // templates/home.html로 렌더링
    }

    // 상품 목록 페이지 (홈)
    @GetMapping("/home")
    public String listProducts(HttpSession session, Model model) {
        // 로그인 상태 확인
        boolean isLoggedIn = userService.checkLoginStatus(session);

        // 로그인 상태에 따라 모델에 데이터 추가
        if (isLoggedIn) {
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);  // 로그인된 사용자 정보 모델에 추가
        }

        model.addAttribute("isLoggedIn", isLoggedIn);  // 로그인 상태 정보

        // 상품 목록 불러오기
        List<Products> products = productsService.findAllProducts();
        model.addAttribute("products", products);

        return "home";  // templates/home.html로 렌더링
    }
}

