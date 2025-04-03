package com.shopnova.kr.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shopnova.kr.domain.Products;
import com.shopnova.kr.domain.ProductsStatus;
import com.shopnova.kr.repository.ProductsRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductsService {

   private final String UPLOAD_DIR = "src/main/resources/static/images";

    private final ProductsRepository productsRepository;

    // 상품 저장
    public Long saveProduct(Products product) {
        productsRepository.save(product);
        return product.getId();
    }

    // 특정 상품 조회
    @Transactional(readOnly = true)
    public Products findProductById(Long id) {
        return productsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    // 전체 상품 조회
    @Transactional(readOnly = true)
    public List<Products> findAllProducts() {
        return productsRepository.findAll();
    }

    // 이름으로 상품 검색
    @Transactional(readOnly = true)
    public List<Products> findProductsByName(String name) {
        return productsRepository.findByName(name);
    }

    // 상태별 상품 검색
    @Transactional(readOnly = true)
    public List<Products> findProductsByStatus(ProductsStatus status) {
        return productsRepository.findByStatus(status);
    }

    // 페이징 처리된 상품 조회
    @Transactional(readOnly = true)
    public List<Products> findProductsWithPaging(int offset, int limit) {
        return productsRepository.findAllWithPaging(offset, limit);
    }

    // 가격 정렬된 상품 조회
    @Transactional(readOnly = true)
    public List<Products> findProductsSortedByPrice(boolean ascending) {
        if (ascending) {
            return productsRepository.findAllSortedByPriceAsc();
        } else {
            return productsRepository.findAllSortedByPriceDesc();
        }
    }

    // 카테고리별 상품 조회 + 가격 정렬된 상품 조회
    @Transactional(readOnly = true)
    public List<Products> findProductsByCategoryAndSort(String category, boolean ascending) {
        if (ascending) {
            return productsRepository.findByCategoryAndSortAsc(category);
        } else {
            return productsRepository.findByCategoryAndSortDesc(category);
        }
    }

    // 상품 삭제
    public void deleteProduct(Long id) {
        Products product = findProductById(id);
        productsRepository.delete(product);
    }
    
    // 카테고리별 상품 조회
    public List<Products> findProductsByCategory(String category) {
        return productsRepository.findByCategory(category);
    }

    // 상품 수정
    public Products updateProduct(Long id, Products updatedProduct) {
        Products existingProduct = findProductById(id);
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setDiscountPrice(updatedProduct.getDiscountPrice());
        existingProduct.setShippingCost(updatedProduct.getShippingCost());
        existingProduct.setStatus(updatedProduct.getStatus());

        return productsRepository.save(existingProduct); // save()를 사용하여 업데이트 처리
    }

    // 복합 검색 (이름, 설명, 카테고리)
    public List<Products> searchProducts(String keyword) {
        return productsRepository.search(keyword);
    }

    // 장바구니 상품 조회
    public List<Products> getCartItems() {
        return productsRepository.getCartItems();
    }

    // 특정 상품을 장바구니에 추가
    public boolean addToCart(Long productId) {
        Products product = findProductById(productId);
        if (product != null) {
            if (product.isInCart()) {
                return false; // 이미 존재하면 false 반환
            }
            product.setInCart(true);
            productsRepository.save(product); // update는 save()로 대체
            return true;
        }
        return false;
    }

    // 특정 상품을 장바구니에서 제거
    public void removeFromCart(Long productId) {
        Products product = findProductById(productId);
        if (product != null) {
            product.setInCart(false);
            productsRepository.save(product); // update는 save()로 대체
        }
    }

    // 이미지 업로드 메소드
    public String uploadImage(MultipartFile imageFile) {
        // 이미지 파일을 저장할 디렉토리 생성
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();  // 디렉터리가 없으면 생성
        }

        // 파일 이름 생성 (중복 방지)
        String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, fileName);

        try {
            // 이미지 파일을 로컬 디스크에 저장
            Files.copy(imageFile.getInputStream(), filePath);

            // 저장된 파일의 URL 반환
            return "/images/" + fileName;  // static/images/ 경로로 접근 가능
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("이미지 업로드에 실패했습니다.");
        }
    }
}
