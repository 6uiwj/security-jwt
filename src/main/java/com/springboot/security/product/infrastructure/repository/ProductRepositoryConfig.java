package com.springboot.security.product.infrastructure.repository;

import com.springboot.security.product.domain.repository.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * 리포지터리 설정 클래스
 *
 * DDD 원칙:
 * - 도메인 계층은 인프라스트럭처에 의존하지 않음
 * - 인프라스트럭처 계층에서 도메인 인터페이스의 구현체를 제공
 * - 의존성 역전 원칙 (DIP) 적용
 * - 어댑터 패턴으로 도메인과 인프라스트럭처 분리
 */
@Configuration
public class ProductRepositoryConfig {
    /**
     * 주문 리포지터리 어댑터 등록
     *
     * 어댑터 패턴을 사용하여:
     * - 도메인 인터페이스 (ProductRepository)와
     * - JPA 인터페이스 (ProductJpaRepository)를 연결
     */
    @Bean
    public ProductRepository productRepository(ProductJpaRepository productJpaRepository) {
        return new ProductRepositoryAdapter(productJpaRepository);
    }

}
