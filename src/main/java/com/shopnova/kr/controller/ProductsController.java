package com.shopnova.kr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shopnova.kr.domain.Products;
import com.shopnova.kr.domain.User;
import com.shopnova.kr.service.CartItemsService;
import com.shopnova.kr.service.ProductsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor // 자동으로 final 필드에 대한 생성자 주입을 생성
public class ProductsController {
	
	@Autowired 
	private CartItemsService cartService;
	@Autowired
    private ProductsService productsService;
    
    // 전자제품 페이지
    @GetMapping("/electronics")
    public String electronicsProducts(Model model, HttpSession session) {
        List<Products> products = productsService.findProductsByCategory("전자제품");
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
        	model.addAttribute("currentUser", currentUser);
        }
        System.out.println(products);
        model.addAttribute("products", products);
        return "electronics"; // templates/electronics.html
    }
    
    //전자제품 가격 정렬 페이지
    @GetMapping("/electronics2")
    public String getElectronics(@RequestParam(value = "sort", required = false, defaultValue = "asc") String sort, Model model, HttpSession session) {
        // "asc"면 true (가격 낮은 순), "desc"면 false (가격 높은 순)
        boolean ascending = "asc".equalsIgnoreCase(sort);
        
        // 카테고리와 정렬 방향을 바탕으로 상품을 가져옴
        List<Products> products = productsService.findProductsByCategoryAndSort("전자제품", ascending);
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
        	model.addAttribute("currentUser", currentUser);
        }
        model.addAttribute("products", products);
        model.addAttribute("sort", sort);  // 정렬 상태를 전달
        return "electronics"; // 상품 목록을 표시할 페이지
    }

    // 의류 페이지
    @GetMapping("/clothing")
    public String clothingPage(Model model, HttpSession session) {
    	List<Products> products = productsService.findProductsByCategory("의류");
    	User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
        	model.addAttribute("currentUser", currentUser);
        }
    	System.out.println(products);
        model.addAttribute("products", products);
        return "clothing"; // src/main/resources/static/clothing.html로 연결
    }
    
    //의류 가격 정렬 페이지
    @GetMapping("/clothing2")
    public String getclothing(@RequestParam(value = "sort", required = false, defaultValue = "asc") String sort, Model model, HttpSession session) {
        // "asc"면 true (가격 낮은 순), "desc"면 false (가격 높은 순)
        boolean ascending = "asc".equalsIgnoreCase(sort);
        
        // 카테고리와 정렬 방향을 바탕으로 상품을 가져옴
        List<Products> products = productsService.findProductsByCategoryAndSort("의류", ascending);
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
        	model.addAttribute("currentUser", currentUser);
        }
        model.addAttribute("products", products);
        model.addAttribute("sort", sort);  // 정렬 상태를 전달
        return "clothing"; // 상품 목록을 표시할 페이지
    }

    // 식품 페이지
    @GetMapping("/food")
    public String foodPage(Model model, HttpSession session) {
    	List<Products> products = productsService.findProductsByCategory("식품");
    	User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
        	model.addAttribute("currentUser", currentUser);
        }
    	System.out.println(products);
        model.addAttribute("products", products);
        return "food"; // src/main/resources/static/food.html로 연결
    }
    
    //식품 가격 정렬 페이지
    @GetMapping("/food2")
    public String getfood(@RequestParam(value = "sort", required = false, defaultValue = "asc") String sort, Model model, HttpSession session) {
        // "asc"면 true (가격 낮은 순), "desc"면 false (가격 높은 순)
        boolean ascending = "asc".equalsIgnoreCase(sort);
        
        // 카테고리와 정렬 방향을 바탕으로 상품을 가져옴
        List<Products> products = productsService.findProductsByCategoryAndSort("식품", ascending);
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
        	model.addAttribute("currentUser", currentUser);
        }
        model.addAttribute("products", products);
        model.addAttribute("sort", sort);  // 정렬 상태를 전달
        return "food"; // 상품 목록을 표시할 페이지
    }

    // 화장품 페이지
    @GetMapping("/beauty")
    public String beautyPage(Model model, HttpSession session) {
    	List<Products> products = productsService.findProductsByCategory("화장품");
    	User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
        	model.addAttribute("currentUser", currentUser);
        }
    	System.out.println(products);
        model.addAttribute("products", products);
        return "beauty"; // src/main/resources/static/beauty.html로 연결
    }
    
    //화장품 가격 정렬 페이지
    @GetMapping("/beauty2")
    public String getbeauty(@RequestParam(value = "sort", required = false, defaultValue = "asc") String sort, Model model, HttpSession session) {
        // "asc"면 true (가격 낮은 순), "desc"면 false (가격 높은 순)
        boolean ascending = "asc".equalsIgnoreCase(sort);
        
        // 카테고리와 정렬 방향을 바탕으로 상품을 가져옴
        List<Products> products = productsService.findProductsByCategoryAndSort("화장품", ascending);
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
        	model.addAttribute("currentUser", currentUser);
        }
        model.addAttribute("products", products);
        model.addAttribute("sort", sort);  // 정렬 상태를 전달
        return "beauty"; // 상품 목록을 표시할 페이지
    }

    // 스포츠 페이지
    @GetMapping("/sports")
    public String sportsPage(Model model, HttpSession session) {
    	String category = "스포츠";
    	List<Products> products = productsService.findProductsByCategory("스포츠");
    	User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
        	model.addAttribute("currentUser", currentUser);
        }
    	System.out.println(products);
        model.addAttribute("products", products);
        return "sports"; // src/main/resources/static/sports.html로 연결
    }  
    
    //화장품 가격 정렬 페이지
    @GetMapping("/sports2")
    public String getsports(@RequestParam(value = "sort", required = false, defaultValue = "asc") String sort, Model model, HttpSession session) {
        // "asc"면 true (가격 낮은 순), "desc"면 false (가격 높은 순)
        boolean ascending = "asc".equalsIgnoreCase(sort);
        
        // 카테고리와 정렬 방향을 바탕으로 상품을 가져옴
        List<Products> products = productsService.findProductsByCategoryAndSort("스포츠", ascending);
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
        	model.addAttribute("currentUser", currentUser);
        }
        model.addAttribute("products", products);
        model.addAttribute("sort", sort);  // 정렬 상태를 전달
        return "sports"; // 상품 목록을 표시할 페이지
    }

    // 상품 상세 페이지
    @GetMapping("/product")
    public String getProductDetail(@RequestParam("id") Long id, Model model, HttpSession session) {
        Products product = productsService.findProductById(id);
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
        	model.addAttribute("currentUser", currentUser);
        }
        if (product == null) {
            return "error/404"; // 상품이 없으면 404 페이지로 이동
        }
        model.addAttribute("product", product);
        return "product"; // product.html로 이동
    }
    
    // 검색 결과 페이지
    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model, HttpSession session) {
        List<Products> searchResults = productsService.searchProducts(query);
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
        	model.addAttribute("currentUser", currentUser);
        }
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("query", query);
        return "search"; // 검색 결과를 출력할 페이지로 이동
    }

    @GetMapping("/product/{id}")
    public String productDetail(@RequestParam(name = "userId", required = false) Long userId,
    							@PathVariable("id") Long id, Model model, HttpSession session) {
        Products product = productsService.findProductById(id);
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
        	model.addAttribute("currentUser", currentUser);
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("product", product);
        return "product";  // product.html
    }

    @GetMapping("/product/detail/{productId}")
    public String viewProduct(@PathVariable("id") Long id, Model model, HttpSession session) {
        Products product = productsService.findProductById(id); // 상품 정보 조회
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
        	model.addAttribute("currentUser", currentUser);
        }
        model.addAttribute("product", product); // 상품 정보를 모델에 추가
        return "product"; // product.html 페이지 반환
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId, 
                            @RequestParam("quantity") int quantity,
                            HttpSession session) {
        // 세션에서 사용자 ID를 가져옴
    	User currentUser = (User) session.getAttribute("currentUser");  // 세션에서 userId를 가져옴

        // 사용자가 로그인하지 않았으면 로그인 페이지로 리디렉션
        if (currentUser == null) {
            return "redirect:/users/login";
        }
        
        if (currentUser.getId() == null) {
            // userId가 null이라면 에러 메시지 표시 또는 리디렉션 처리
            return "redirect:/error";
        }
        
        System.out.println("Current User ID: " + currentUser.getId());
        System.out.println(productId);
        System.out.println(quantity);
        
        // 제품 ID를 이용해 장바구니에 상품 추가
        cartService.addToCart(currentUser.getId(), productId, quantity);  // user.getEmail() 사용

        // 장바구니 페이지로 리디렉션
        return "redirect:/cart-items/cart";
    }

    // 장바구니에서 상품 제거
    @GetMapping("/cart/remove")
    public String removeFromCart(@RequestParam("id") Long productId, Model model, HttpSession session) {
    	User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
        	model.addAttribute("currentUser", currentUser);
        }
    	productsService.removeFromCart(productId);
        return "redirect:/cart"; // 장바구니 페이지로 리디렉션
    }
    
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        // 현재 세션 가져오기
        HttpSession session = request.getSession(false); // 세션이 없으면 null 반환
        if (session != null) {
            session.invalidate(); // 세션 무효화
        }
        
        // 로그아웃 후 홈 페이지로 리디렉션
        return "redirect:/";
    }
}
