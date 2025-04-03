package com.shopnova.kr.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopnova.kr.domain.User;
import com.shopnova.kr.service.NaverLoginService;
import com.shopnova.kr.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class NaverLoginController {

    @Autowired
    private NaverLoginService naverLoginService;

    private final String clientId = "lB3HLLJuvztYmJr_soDB";  // 네이버 클라이언트 ID
    private final String clientSecret = "4IDF2ASNh7";  // 네이버 클라이언트 시크릿
    private final String redirectUri = "http://localhost:8081/users/naverLoginCallback";  // 네이버 로그인 리디렉션 URI
    private final String USER_INFO_URL = "https://openapi.naver.com/v1/nid/me";  // 네이버 사용자 정보 API URL

    @GetMapping("/naverLoginCallback")
    public String naverLoginCallback(@RequestParam("code") String code, 
                                     @RequestParam("state") String state, 
                                     HttpSession session,
                                     HttpServletResponse response, 
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        try {
            // 네이버 로그인 후 사용자 정보 가져오기
            User currentUser = naverLoginService.getUserInfo(code, state, session, response, redirectAttributes);

            if (currentUser != null) {
                // 세션에 사용자 정보 저장
                session.setAttribute("currentUser", currentUser);

                // 모델에 currentUser 추가
                model.addAttribute("currentUser", currentUser);

                // 네이버 로그인 후 회원가입 성공 메시지를 전달
                model.addAttribute("signupMessage", "네이버 아이디로 회원 가입 완료 되었습니다.");

                // 회원가입 완료 페이지로 이동
                return "signupComplete";  // 이 페이지에서 메시지를 출력
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 로그인 실패 시 로그인 페이지로 리디렉션
        return "redirect:/users/login"; 
    }

}

