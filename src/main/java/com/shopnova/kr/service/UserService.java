package com.shopnova.kr.service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopnova.kr.domain.User;
import com.shopnova.kr.repository.UserRepository;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

@Service
public class UserService {
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private JavaMailSender mailSender;

    // 비밀번호 재설정 토큰을 저장하는 맵 (실제로는 DB 사용해야 함)
    private Map<String, String> passwordResetTokens = new HashMap<>();
    
    public User authenticate(String email, String password, HttpSession session) {
        return login(email, password, session);  // 기존 login 메서드를 재사용
    }
    
    public boolean checkLoginStatus(HttpSession session) {
        return session.getAttribute("user") != null;
    }
    
    // id 조회!
    public User findById(Long currentUser) {
        return userRepository.findById(currentUser).orElse(null);
    }
  //로그인 처
    public User login(String email, String password, HttpSession session) {
        User user = findUserByEmail(email);
        
        if (user != null && password.equals(user.getPassword())) {
            // 사용자가 비활성화 상태인 경우 로그인 실패 처리
            if ("Inactive".equals(user.getStatus())) {
                return null;  // 로그인 실패, null 반환
            }
            
            // 활성화된 사용자이면 세션에 사용자 정보 저장
            session.setAttribute("user", user);
            return user;
        }
        
        return null;  // 이메일 또는 비밀번호 불일치로 로그인 실패
    }

 // 회원가입 처리
    public User signUp(User user, HttpSession session) {
        user.setRole("USER"); // 문자열로 ROLE 설정
        user.setStatus("ACTIVE"); // 문자열로 상태 설정

        if (findUserByEmail(user.getEmail()) != null) {
            return null;
        }
        if (!isValidEmail(user.getEmail()) || !isValidPassword(user.getPassword())) {
            return null;
        }
        user = saveUser(user);
        session.setAttribute("user", user);
        return user;
    }

    // 로그아웃 처리
    public void logout(HttpSession session) {
        session.invalidate();
    }

    // 이메일로 사용자 찾기
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // 번호로 사용자 이메일 찾기
    public User findUserByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }
    
    public String findUserByPhoneNumber(String phone) {
    	User user = findUserByPhone(phone);
    	if (user != null) {
    		return user.getEmail();
    	}
    	return null;
    }

    // 이메일 중복 체크
    public boolean isEmailExist(String email) {
        return findUserByEmail(email) != null;
    }
    
    // 사용자 정보를 DB에 저장
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // 랜덤 비밀번호 생성 (6~8자리)
    public String generateRandomPassword() {
        int length = 6;
        String passwordCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(passwordCharacters.length());
            password.append(passwordCharacters.charAt(index));
        }
        return password.toString();
    }

    // 비밀번호 업데이트
    public boolean updatePassword(String email, String newPassword) {
        User user = findUserByEmail(email);
        if (user != null) {
            user.setPassword(newPassword);
            saveUser(user);
            return true;
        }
        return false;
    }

    // 비밀번호 재설정 링크 이메일 발송
    public void sendPasswordResetLink(String email) {
        String token = UUID.randomUUID().toString();
        passwordResetTokens.put(token, email); // 토큰 저장
        sendPasswordResetEmail(email, token);
    }

    public void sendPasswordResetEmail(String email, String token) {
        try {
            String subject = "Password Reset Request";
            
            // HTML 형식으로 이메일 본문 작성
            String content = "<html><body>" +
                             "<p>To reset your password, click the link below:</p>" +
                             "<a href=\"http://localhost:8081/users/resetPW?token=" + token + "\">Reset Password</a>" +
                             "</body></html>";

            // 이메일 메시지 생성
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // 수신자, 제목, 본문 설정
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);  // true를 설정하면 HTML 형식으로 처리됨

            // 이메일 발송
            mailSender.send(message);
            System.out.println("Password reset email sent to " + email + " with token: " + token);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to send password reset email.");
        }
    }


    public boolean resetPW(String token, String newPassword, HttpSession session) {
        // 1. 토큰을 사용해 이메일 찾기
        String email = getEmailByToken(token);
        if (email == null) {
            return false; // 토큰이 유효하지 않으면 false 반환
        }

        // 2. 이메일로 사용자 정보 찾기
        User user = userRepository.findByEmail(email);
        if (user != null) {
            // 3. 비밀번호 변경 (암호화가 필요하다면 암호화 로직 추가)
            user.setPassword(newPassword);

            // 4. 사용자 정보를 데이터베이스에 저장
            userRepository.save(user);

            // 5. 세션에 사용자 정보 업데이트
            session.setAttribute("currentUser", user);

            // 6. 사용한 토큰 삭제
            passwordResetTokens.remove(token);

            return true; // 성공적으로 비밀번호 변경 및 세션 업데이트
        }

        return false; // 사용자 정보를 찾을 수 없으면 false 반환
    }

    // 토큰을 기반으로 이메일을 찾는 메서드
    public String getEmailByToken(String token) {
        return passwordResetTokens.get(token); // 저장된 토큰에서 이메일 반환
    }

    // 네이버 로그인 후 회원가입 및 로그인 처리
    public User naverSignUpAndLogin(String email, String userId, String phone, String address, String name) {
        String fixedEmail = email;
        User existingUser = findUserByEmail(fixedEmail);
        if (existingUser != null) {
            return existingUser;
        }
        User newUser = new User();
        newUser.setEmail(fixedEmail);
        newUser.setUserId(userId);
        newUser.setPhone(phone);
        newUser.setAddress(address);
        newUser.setName(name);
        newUser.setPassword("123456");
        saveUser(newUser);
        return newUser;
    }

 // userService에서 User를 반환하도록 수정
    public User naverLogin(String code, String state, HttpSession session, Model model) {
        try {
            // 네이버 API로부터 액세스 토큰을 가져오기
            String accessToken = getAccessToken(code, "http://localhost:8080/users/naverLoginCallback");

            if (accessToken != null) {
                // 액세스 토큰으로 사용자 정보 가져오기
                User user = getUserInfoFromAccessToken(accessToken);
                if (user != null) {
                    // 네이버 로그인 후 사용자 정보가 없으면 회원가입 후 로그인
                    user = naverSignUpAndLogin(user.getEmail(), user.getUserId(), user.getPhone(), user.getAddress(), user.getName());
                    
                    // 세션에 사용자 정보 저장
                    session.setAttribute("user", user);
                    return user;  // User 객체 반환
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;  // 로그인 실패 시 null 반환
    }

    // 네이버 로그인 후 액세스 토큰 가져오기
    public String getAccessToken(String code, String redirectUri) {
        // 네이버 API를 호출하여 액세스 토큰을 반환하는 로직 구현
        // 예: HTTP 요청을 보내고 응답을 처리하여 액세스 토큰을 반환
        return "fake_access_token";  // 여기서는 더미 값을 반환
    }

    public User getUserInfoFromAccessToken(String accessToken) {
        try {
            // 네이버 사용자 정보 조회 API URL
            String apiUrl = "https://openapi.naver.com/v1/nid/me";

            // RestTemplate을 사용해 API 호출
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange("https://openapi.naver.com/v1/nid/me", HttpMethod.GET, entity, String.class);

            // 응답 JSON을 파싱하여 사용자 정보 추출
            String responseBody = response.getBody();
            JsonNode jsonNode = new ObjectMapper().readTree(responseBody);

            // "response" 객체에서 실제 사용자 정보 추출
            if (jsonNode != null && jsonNode.has("response")) {
                JsonNode responseData = jsonNode.get("response");
                
                User user = new User();
                user.setUserId(responseData.get("id").asText());  // 네이버 ID
                user.setName(responseData.get("name").asText());  // 사용자 이름
                user.setEmail(responseData.get("email").asText());  // 이메일
                user.setPhone(responseData.has("mobile") ? responseData.get("mobile").asText() : "");  // 휴대전화 (있으면 추가)
                user.setAddress("");  // 네이버 API에는 주소 정보가 없으므로 빈 값으로 설정 (필요시 추가 API 호출)

                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;  // 사용자 정보 가져오지 못하면 null 반환
    }
    
 // 기존 메서드를 수정하여 수정할 사용자 정보를 직접 받도록 변경
    public boolean updateUserProfile(Long userId, String name, String phone, String address, String email) {
        // 1. ID로 사용자 검색
        User existingUser = userRepository.findById(userId).orElse(null);
        
        if (existingUser != null) {
            // 2. 사용자 정보 수정 (새로 입력된 정보로 변경)
            
            // 이메일 변경 시 이메일 중복 체크
            if (email != null && !email.equals(existingUser.getEmail()) && isEmailExist(email)) {
                // 이미 존재하는 이메일이면 수정 불가
                return false;
            }
            
            // 이메일이 변경되었을 경우에만 수정
            if (email != null && !email.equals(existingUser.getEmail())) {
                existingUser.setEmail(email);
            }
            
            // 나머지 필드들 업데이트
            existingUser.setName(name);
            existingUser.setPhone(phone);
            existingUser.setAddress(address);

            // 3. 수정된 정보 저장
            userRepository.save(existingUser);
            return true; // 수정 성공
        }
        
        return false; // 사용자 정보가 존재하지 않으면 수정 실패
    }


    // 이메일 형식 유효성 검증
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z]{2,})?$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    // 비밀번호 형식 유효성 검증
    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }
    
    // 모든 사용자 목록 가져오기
    public List<User> getAllUsers() {
        return userRepository.findAll();  // 데이터베이스에서 모든 사용자 리스트 반환
    }

    // 회원 상태 변경 (활성화/비활성화)
 // 회원 상태 변경 (활성화/비활성화)
    public boolean updateUserStatus(Long userId, String status) {
        // 사용자 조회
        User user = userRepository.findById(userId).orElse(null);
        
        if (user != null) {
            // status 값이 "ACTIVE" 또는 "INACTIVE"인지 확인
            if ("ACTIVE".equalsIgnoreCase(status) || "INACTIVE".equalsIgnoreCase(status)) {
                user.setStatus(status);  // 상태 변경
                userRepository.save(user);  // 상태 업데이트 후 저장
                return true;
            } else {
                // 잘못된 status 값이 들어왔을 경우 예외 처리
                System.out.println("Invalid status value: " + status);
                return false;
            }
        }
        return false;
    }
    
    public void toggleUserStatus(Long userId) {
        // 사용자 정보 가져오기
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 상태를 active ↔ inactive로 토글
        if ("ACTIVE".equals(user.getStatus())) {
            user.setStatus("INACTIVE");  // active 상태 -> inactive로 변경
        } else {
            user.setStatus("ACTIVE");    // inactive 상태 -> active로 변경
        }

        // 상태 변경된 사용자 저장
        userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null); // ID로 유저 조회
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id); // 유저 삭제
    }
    
    // 예시로 사용자 정보를 가져오는 메소드
    public User getCurrentUser() {
        // 예시로 하드코딩된 사용자 정보 반환. 실제로는 DB나 세션에서 사용자 정보를 가져옴
        return new User(null, "홍길동", "123-456-7890", "서울시 강남구", "hong@example.com", null, null, null, null);
    }
    // ✅ 메서드 이름을 findUserById로 맞춤
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
    public User findByEmail(String email) {
        // 이메일로 사용자 검색 로직 구현 (예: 데이터베이스에서 이메일로 사용자 찾기)
        return userRepository.findByEmail(email);
    }
    
    

 
}


