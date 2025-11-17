package com.springboot.security.product.infrastructure.repository;

import com.springboot.security.product.domain.entity.Product;
import com.springboot.security.product.domain.repository.ProductRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * 제품 리포지터리 어댑터
 *
 * 어댑터 패턴을 사용하여:
 * 1. 도메인 인터페이스 (ProductRepository)와 JPA 인터페이스 (ProductJpaRepository) 연결
 * 2. 도메인 계층과 인프라스트럭처 계층의 분리
 * 3. 의존성 역전 원칙 (DIP) 적용
 *
 * 역할:
 * - 도메인 인터페이스를 구현
 * - JPA 리포지터리를 위임하여 실제 데이터베이스 작업 수행
 */
@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {


    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product insertProduct(Product product) {
        Product savedProduct = productJpaRepository.save(product);
        return savedProduct;
    }

    @Override
    public Product selectProduct(Long number) {
        Optional<Product> selectedProduct = productJpaRepository.findById(number);
        if (selectedProduct.isPresent()) {
            Product product = selectedProduct.get();
            return product;
        } else throw new NoSuchElementException();
    }

    @Transactional
    @Override
    public Product updateProduct(Long number, String name) {
        Product product = productJpaRepository.findById(number).orElseThrow(NoSuchElementException::new);
        product.updateProduct(name);
        //@Transactional을 붙이면 엔티티의 내용이 변경되면 Dirty Checking 발생 -> 자동으로 save. 즉 save 메서드를 쓸 필요 없음
        //-> 단 @Transactional 은 서비스 레이어에 붙이는게 좋음

        //return productJpaRepository.save(product);
        return product;
    }

    @Override
    public void deleteProduct(Long number) {
        Product selectedProduct = productJpaRepository.findById(number).orElseThrow(NoSuchElementException::new);
        productJpaRepository.delete(selectedProduct);
    }

    @Override
    public Product saveAndFlushProduct(Product product) {
        return productJpaRepository.saveAndFlush(product);
    }
}