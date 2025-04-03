package com.shopnova.kr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopnova.kr.domain.Products;
import com.shopnova.kr.domain.ProductsStatus;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {

    // 이름으로 검색
    List<Products> findByName(String name);

    // 이름, 설명, 카테고리로 검색 (복합 검색)
    @Query("SELECT p FROM Products p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword% OR p.category LIKE %:keyword%")
    List<Products> search(@Param("keyword") String keyword);

    // 상태로 검색
    List<Products> findByStatus(ProductsStatus status);

    // 페이징 처리된 상품 조회
    @Query("SELECT p FROM Products p")
    List<Products> findAllWithPaging(int offset, int limit);

    // 가격 정렬
    @Query("SELECT p FROM Products p ORDER BY p.price ASC")
    List<Products> findAllSortedByPriceAsc();

    @Query("SELECT p FROM Products p ORDER BY p.price DESC")
    List<Products> findAllSortedByPriceDesc();

    // 카테고리별 상품 조회
    List<Products> findByCategory(String category);

    // 장바구니에 추가된 상품들 조회
    @Query("SELECT p FROM Products p WHERE p.inCart = true")
    List<Products> getCartItems();

    // 카테고리별 상품 조회 + 가격 정렬
    @Query("SELECT p FROM Products p WHERE p.category = :category ORDER BY p.price ASC")
    List<Products> findByCategoryAndSortAsc(@Param("category") String category);

    @Query("SELECT p FROM Products p WHERE p.category = :category ORDER BY p.price DESC")
    List<Products> findByCategoryAndSortDesc(@Param("category") String category);

    List<Products> findByIdIn(List<Long> ids);
}
