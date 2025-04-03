package com.shopnova.kr.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopnova.kr.domain.CartItems;
import com.shopnova.kr.domain.Order;
import com.shopnova.kr.domain.User;
import com.shopnova.kr.repository.CartItemsRepository;
import com.shopnova.kr.service.OrderService;
import com.shopnova.kr.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UserController {
	
	
	@Autowired
	private CartItemsRepository cartItemsRepository;
	@Autowired
    private UserService userService;
	@Autowired
    private OrderService orderService;

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        RedirectAttributes redirectAttributes,
                        HttpSession session) {
        try {
            // 사용자 인증 처리
            User user = userService.authenticate(email, password, session);

            // 인증된 사용자 정보가 null 인 경우 로그인 실패 처리
            if (user == null) {
                redirectAttributes.addFlashAttribute("message", "아이디 또는 비밀번호가 잘못되었습니다.");
                return "redirect:/users/login";
            }

            // 비활성화된 사용자 체크
            if ("INACTIVE".equals(user.getStatus())) {
                // 비활성화된 계정이므로 로그인 실패 처리
                redirectAttributes.addFlashAttribute("message", "비활성화된 회원입니다. 관리자에게 문의하세요.");
                return "redirect:/users/login"; // 로그인 페이지로 리디렉션
            }

            // 활성화된 사용자라면 세션에 저장
            session.setAttribute("currentUser", user);

            // 관리자인 경우 관리자 페이지 접근 허용
            if (user.getRole() != null && user.getRole().equals("ROLE_ADMIN")) {
                return "redirect:/admin/adminpage";  // 관리자 페이지로 리디렉션
            }

            // 관리자가 아닌 경우 일반 사용자 홈 페이지로 리디렉션
            return "redirect:/";  // 홈 페이지로 리디렉션

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "로그인 처리 중 오류가 발생했습니다.");
            return "redirect:/users/login";  // 로그인 페이지로 리디렉션
        }
    }

    
    // 회원가입 페이지
    @GetMapping("/signUp")
    public String signupPage() {
        return "signUp";
    }

    // 이메일 중복 체크
    @PostMapping("/checkEmail")
    @ResponseBody
    public ResponseEntity<Map<String, String>> checkEmail(@RequestParam("email") String email) {
        Map<String, String> response = new HashMap<>();
        if (userService.isEmailExist(email)) {
            response.put("message", "이미 사용 중인 이메일입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        response.put("message", "사용 가능한 이메일입니다.");
        return ResponseEntity.ok(response);
    }

    // 회원가입 처리
    @PostMapping("/signUp")
    public String signup(@ModelAttribute User user, HttpSession session, Model model) {
        try {
            // 이메일 유효성 검사
            if (!isValidEmail(user.getEmail())) {
                model.addAttribute("error", "유효하지 않은 이메일 형식입니다.");
                return "signUp";
            }

            // 비밀번호 유효성 검사
            if (!isValidPassword(user.getPassword())) {
                model.addAttribute("error", "비밀번호는 최소 6자 이상이어야 합니다.");
                return "signUp";
            }

            // 이메일 중복 검사
            if (userService.isEmailExist(user.getEmail())) {
                model.addAttribute("error", "이미 사용 중인 이메일입니다.");
                return "signUp";
            }

            // 회원가입 처리
            User newUser = userService.signUp(user, session);
   
            // 회원가입 성공 시 세션에 사용자 정보 저장
            session.setAttribute("currentUser", newUser);

            // 회원가입 후 홈 화면으로 리다이렉트
            return "redirect:/";  // 세션에 현재 사용자 정보가 저장되므로 로그인 상태 유지됨
        } catch (Exception e) {
            model.addAttribute("error", "회원가입 처리 중 오류가 발생했습니다.");
            return "signUp";
        }
    }
    
    @GetMapping("/logout")
    public String logoutGet(HttpSession session, RedirectAttributes redirectAttributes) {
        return logout(session, redirectAttributes);  // 기존 POST 로그아웃 메서드 호출
    }
    
    // 로그아웃 처리
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            session.invalidate();
            redirectAttributes.addFlashAttribute("message", "로그아웃되었습니다.");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "로그아웃 처리 중 오류가 발생했습니다.");
            return "redirect:/";
        }
    }
    
    // 이메일로 아이디 찾기 페이지
    @GetMapping("/findUserId")
    public String findUserIdPage() {
        return "findUserId"; // 이메일을 입력하는 페이지로 이동
    }

    // 이메일로 사용자 아이디 찾기
    @PostMapping("/findUserId")
    public String findUserId(@RequestParam("phone") String phone, Model model) {
        try {
            String email = userService.findUserByPhoneNumber(phone); // 이메일로 사용자 아이디 찾기
            if (email != null) {
                model.addAttribute("email", email); // 찾은 이메일을 모델에 추가
            } else {
                model.addAttribute("error", "해당 번호에 등록된 이메일이 없습니다."); // 이메일이 없을 경우 오류 메시지
            }
        } catch (Exception e) {
            model.addAttribute("error", "이메일 찾기 중 오류가 발생했습니다."); // 예외 처리
        }
        return "findUserId"; // 아이디 찾기 페이지로 다시 이동
    }
    
 // 비밀번호 찾기 페이지
    @GetMapping("/findPassword")
    public String findPasswordPage() {
        return "findPassword";
    }

    // 비밀번호 재설정 링크 이메일로 전송
    @PostMapping("/findPassword")
    public String findPassword(@RequestParam("email") String email, Model model) {
        try {
            userService.sendPasswordResetLink(email);
            model.addAttribute("message", "비밀번호 재설정 링크가 이메일로 전송되었습니다.");
        } catch (Exception e) {
            model.addAttribute("error", "비밀번호 재설정 이메일 전송 중 오류가 발생했습니다.");
        }
        return "findPassword";
    }
   
    // 비밀번호 재설정 페이지
    @GetMapping("/resetPW")
    public String resetPasswordPage(@RequestParam(value = "token", required = false) String token, Model model) {
        if (token == null || token.isEmpty()) {
            model.addAttribute("error", "유효하지 않은 토큰입니다. 다시 시도해주세요.");
            return "resetPW";
        }
        model.addAttribute("token", token);
        return "resetPW";
    }

    @PostMapping("/resetPW")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("newPassword") String newPassword,
                                HttpSession session, Model model) {
        try {
            // 비밀번호 재설정 처리
            boolean success = userService.resetPW(token, newPassword, session);

            if (success) {
                // 비밀번호 변경 후 메시지 표시
                model.addAttribute("message", "비밀번호가 성공적으로 변경되었습니다.");
                return "redirect:/"; // 메인 페이지 또는 다른 페이지로 리디렉션 (로그인 상태로 유지)
            } else {
                // 토큰이 유효하지 않거나 비밀번호 변경 실패 시
                model.addAttribute("error", "비밀번호 변경에 실패했습니다. 토큰을 확인해주세요.");
                return "resetPW"; // 다시 비밀번호 재설정 페이지로 이동
            }
        } catch (Exception e) {
            // 예외 처리
            model.addAttribute("error", "비밀번호 변경 중 오류가 발생했습니다.");
            return "resetPW"; // 다시 비밀번호 재설정 페이지로 이동
        }
    }

    // 로그인 상태 확인 (AJAX)
    @GetMapping("/getLoginButtons")
    @ResponseBody
    public String getLoginButtons(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            return "<button class='home-btn' onclick='window.location.href=\"/users/mypage\"'>마이페이지</button>" +
                   "<button class='logout-btn' onclick='logout()'>로그아웃</button>";
        } else {
            return "<button class='naver-login-btn' id='naver-id-login'>네이버로 로그인</button>" +
                   "<button type='submit'>로그인</button>";
        }
    }

    // 마이페이지
    @GetMapping("/mypage")
    public String showUserProfile(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("currentUser");

        // 로그인되지 않았거나 관리자인 경우 로그인 페이지로 리디렉션
        if (sessionUser == null) {
            return "redirect:/users/login";  // 로그인 페이지로 리디렉션
        }

        // role 이 "USER"인 경우 마이페이지로 이동
        if ("USER".equals(sessionUser.getRole())) {
            model.addAttribute("user", sessionUser);
            return "users/mypage";  // 사용자 마이페이지 페이지로 이동
        }

        // 관리자인 경우 관리자 페이지로 리디렉션
        return "redirect:/admin/adminpage";  // 관리자 페이지로 리디렉션
    }

    // 사용자 정보 수정
    @PostMapping("/mypage")
    public String updateUser(@ModelAttribute User user, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            User sessionUser = (User) session.getAttribute("currentUser");

            // 세션에 사용자 정보가 없으면 로그인 페이지로 리디렉션
            if (sessionUser == null) {
                redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
                return "redirect:/login";
            }

            // role이 null이거나, "USER"가 아닌 경우 수정 불가능
            if (sessionUser.getRole() == null || !"USER".equals(sessionUser.getRole())) {
                redirectAttributes.addFlashAttribute("error", "회원 권한이 없습니다.");
                return "redirect:/users/mypage";
            }

            // 입력된 값 중 null 이 아닌 값만 업데이트 (이메일, 비밀번호, 주소, 전화번호만 허용)
            boolean isUpdated = false;

            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                sessionUser.setEmail(user.getEmail());
                isUpdated = true;
            }
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                sessionUser.setPassword(user.getPassword()); // 비밀번호 평문 그대로 저장
                isUpdated = true;
            }
            if (user.getAddress() != null && !user.getAddress().isEmpty()) {
                sessionUser.setAddress(user.getAddress());
                isUpdated = true;
            }
            if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                sessionUser.setPhone(user.getPhone());
                isUpdated = true;
            }

            // 하나라도 변경된 값이 있으면 저장
            if (isUpdated) {
                userService.saveUser(sessionUser);
                session.setAttribute("currentUser", sessionUser);
                redirectAttributes.addFlashAttribute("message", "정보가 수정되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("error", "수정할 정보가 없습니다.");
            }
            return "redirect:/users/mypage";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "정보 수정 중 오류가 발생했습니다.");
            return "redirect:/users/mypage";
        }
    }
    
    // GET 요청: 주문 목록 페이지
    @GetMapping("/orderList")
    public String getOrderList(HttpSession session, Model model) {
        // 로그인한 사용자 정보 가져오기
        User sessionUser = (User) session.getAttribute("currentUser");
        model.addAttribute("user", sessionUser);

        // 로그인되지 않았으면 로그인 페이지로 리디렉션
        if (sessionUser == null) {
            return "redirect:/users/login";  // 로그인 페이지로 리디렉션
        }
        List<Order> orders;
        orders = orderService.getOrdersByUser(sessionUser);  // 로그인한 사용자의 주문만 가져오기

        // 가져온 주문 목록을 모델에 추가
        model.addAttribute("orders", orders);
        
        // orderList.html로 이동
        return "orderList";  // orderList.html로 이동
    }

    // 이메일 유효성 검사
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    // 비밀번호 유효성 검사
    private boolean isValidPassword(String password) {
        return password.length() >= 6;
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
