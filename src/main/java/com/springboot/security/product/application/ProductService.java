package com.springboot.security.product.application;


// ========== Domain ==========
import com.springboot.security.product.domain.entity.Product;
import com.springboot.security.product.domain.repository.ProductRepository;

// ========== Application Layer DTOs ==========
import com.springboot.security.product.presentation.dto.request.ProductDto;
import com.springboot.security.product.presentation.dto.response.ProductResponseDto;

// ========== Framework ==========
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 애플리케이션 서비스
 * <p>
 * 역할:
 * 1. 트랜잭션 관리
 * 2. 도메인 객체 조율
 * 3. 인프라스트럭처 레이어와 연동
 * 4. DTO 변환
 * <p>
 * 특징:
 * - 비즈니스 로직은 도메인 모델에 위임
 * - 얇은 서비스 레이어 (Thin Service Layer)
 * - 도메인 이벤트는 자동으로 발행됨
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final Logger LOGGER  = LoggerFactory.getLogger(ProductService.class);

    //private final ProductRepository productRepository;
    //private final ProductJpaRepository productJpaRepository;
    private final ProductRepository productRepository;

    public ProductResponseDto getProduct(Long number) {
        LOGGER.info("[getProduct] input number {}", number);
        //Product product = productJpaRepository.findById(number).get();
        Product product = productRepository.selectProduct(number);
        LOGGER.info("[getProduct] product number : {}, name : {}", product.getName(), product.getName());

        return ProductResponseDto.from(product);
    }

    public ProductResponseDto saveProduct(ProductDto productDto) {
        Product product = productDto.toEntity();
        //Product savedProduct = productJpaRepository.save(product);
        Product savedProduct = productRepository.insertProduct(product);

        LOGGER.info("[saveProduct] saveProduct : {}", savedProduct);

        ProductResponseDto responseDto = ProductResponseDto.from(product);
        return responseDto;
    }

    public ProductResponseDto changeProductName(Long number, String name) throws Exception {
        // Product foundProduct = productJpaRepository.findById(number).get();
        //foundProduct.updateProduct(name);
        //Product changedProduct = productJpaRepository.save(foundProduct);
        Product changedProduct = productRepository.updateProduct(number, name);
        ProductResponseDto responseDto = ProductResponseDto.from(changedProduct);

        return responseDto;
    }

    public void deleteProduct(Long number) throws Exception {
        //productJpaRepository.deleteById(number);
        productRepository.deleteProduct(number);
    }
}