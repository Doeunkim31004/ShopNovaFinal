package com.shopnova.kr.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopnova.kr.domain.Payment;
import com.shopnova.kr.domain.Products;
import com.shopnova.kr.domain.User;
import com.shopnova.kr.repository.UserRepository;
import com.shopnova.kr.service.PaymentService;
import com.shopnova.kr.service.ProductsService;
import com.shopnova.kr.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private ProductsService productsService;  // ProductsService를 자동으로 주입
	
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;

    // 관리자 페이지로 이동 (로그인된 사용자의 정보로 처리)

 // 관리자 페이지로 이동 (로그인된 사용자의 정보로 처리)
    @GetMapping("/adminpage")
    public String showAdminUserPage(HttpSession session, Model model) {
        // 세션에서 로그인된 사용자 정보 가져오기
        User sessionUser = (User) session.getAttribute("currentUser");

        // 로그인되지 않았거나 관리자 권한이 아닌 경우
        if (sessionUser == null || !"ADMIN".equals(sessionUser.getRole())) {
            return "redirect:/users/login";  // 로그인 페이지로 리디렉션
        }

        // 모든 사용자 목록을 가져와서 모델에 추가
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);

        // 관리자 페이지 템플릿 반환 (사용자 목록을 포함한 관리자 페이지)
        return "admin/adminpage"; 
    }

    // 사용자 상태 변경
    @PostMapping("/update-status")
    public String updateUserStatus(@RequestParam("userId") Long userId, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(userId);  // 사용자 상태 토글 (활성화/비활성화)
            redirectAttributes.addFlashAttribute("message", "사용자 상태가 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "사용자 상태 변경 중 오류가 발생했습니다.");
        }
        // 상태 변경 후 관리자 페이지로 리디렉션
        return "redirect:/admin/adminpage";
    }


    // 사용자 삭제
    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam("userId") Long userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("message", "사용자가 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "사용자 삭제 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/adminpage";
    }
    
    @GetMapping("/sales")
    public String showSales(Model model) {
        // 결제 데이터 조회
        List<Payment> payments = paymentService.getAllPayments();  // 모든 결제 데이터 조회

        // 총 결제 금액 계산
        BigDecimal totalSales = payments.stream()
                                        .map(payment -> {
                                            // paymentAmount를 String에서 BigDecimal로 변환
                                            if (payment.getPaymentAmount() != null) {
                                                return new BigDecimal(payment.getPaymentAmount());
                                            } else {
                                                return BigDecimal.ZERO;  // paymentAmount가 null일 경우 0으로 처리
                                            }
                                        })
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);  // 결제 금액 합산

        // 모델에 데이터 추가
        model.addAttribute("payments", payments);
        model.addAttribute("totalSales", totalSales);

        return "admin/sales";  // sales 페이지로 이동
    }
    
    
    @GetMapping("/product-register")
    public String showProductRegisterForm(Model model) {
        // 카테고리 목록을 모델에 추가
        List<String> categories = Arrays.asList("전자제품", "의류", "식품", "화장품", "스포츠");

        // 상품 객체를 모델에 추가 (상품 등록 폼에 사용할 수 있도록)
        Products product = new Products();
        model.addAttribute("categories", categories); // 카테고리 목록
        model.addAttribute("product", product); // 상품 객체

        return "/admin/product-register";  // 상품 등록 폼을 렌더링할 뷰 이름
    }

    @PostMapping("/product-register")
    public String registerProduct(@ModelAttribute("product") Products product,
                                  @RequestParam("imageFile") MultipartFile imageFile,
                                  RedirectAttributes redirectAttributes) {
        try {
            // 이미지 업로드 처리
            String imageUrl = productsService.uploadImage(imageFile);  // 이미지 URL 받아오기
            product.setImage(imageUrl);  // 상품에 이미지 URL 세팅

            // 상품 등록
            Long productId = productsService.saveProduct(product);

            // 카테고리 목록도 다시 폼에 전달하기 위해 추가
            List<String> categories = Arrays.asList("전자제품", "의류", "식품", "화장품", "스포츠");
            
            // 등록 성공 메시지와 함께 카테고리 정보도 전달
            redirectAttributes.addFlashAttribute("message", "상품이 등록되었습니다.");
            redirectAttributes.addFlashAttribute("categories", categories);  // 카테고리 목록 전달

        } catch (Exception e) {
            // 예외 처리
            redirectAttributes.addFlashAttribute("message", "상품 등록 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        // 등록 후 다시 폼 페이지로 리다이렉트
        return "redirect:/admin/product-register"; 
    }

}
