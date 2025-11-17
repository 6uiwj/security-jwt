package com.springboot.security.product.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * 예시------------
 * 주문 애그리거트 루트 (Aggregate Root)
 *
 * 핵심 개념:
 * 1. 애그리거트 내부 일관성 유지
 * 2. 비즈니스 규칙 캡슐화
 * 3. 불변식(Invariant) 보호
 * 4. 도메인 이벤트 발행
 *
 * 애그리거트 경계:
 * - Order (루트)
 * - OrderItem (내부 엔티티)
 * - Money, Quantity, OrderStatus (값 객체)
 */
@Getter
@Entity
@EqualsAndHashCode
@ToString(exclude = "name")
@NoArgsConstructor(access = AccessLevel.PROTECTED) //jpa용
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long number;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

//    private Product(Long number, String name, Integer price, Integer stock, LocalDateTime createdAt, LocalDateTime updatedAt) {
//        this.number = number;
//        this.name = name;
//        this.price = price;
//        this.stock = stock;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//    }

    // ========== 생성 메서드 ==========
    /**
     * 엔티티의 일관성과 캡슐화를 위해
     데이터 유효성과 상태 변경에 대한 책임을 엔티티 내부가 가짐
     */
    /**
     * 주문 생성 팩토리 메서드
     * 모든 비즈니스 규칙을 여기서 검증
     */
    //객체 생성
    public static Product create(String name, int price, Integer stock) {
        //비즈니스 규칙검증
        //도메인 이벤트 발행
        return new Product(null, name, price, stock, null, null);
    }

    //====================비즈니르 로직============================
    //상태 변경 (setter 대신)
    public void updateProduct(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}