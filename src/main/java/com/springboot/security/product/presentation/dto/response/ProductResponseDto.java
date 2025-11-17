package com.springboot.security.product.presentation.dto.response;

import com.springboot.security.product.domain.entity.Product;
import lombok.Getter;

/**
 * 응답용 (Entity -> dto -> 클라이언트)
 * 외부에서 수정 불가 (immutable)
 * 생성 시 한 번만 값 설정
 *
 * Dto(Java Object) -> JSON 직렬화 : get으로 값을 가져와 JSON으로 만들어주기만 하므로
 *  setter불필요
 *  immutable구조.
 */
@Getter
public class ProductResponseDto {
    private final Long number;
    private final String name;
    private final int  price;
    private final int stock;

    private ProductResponseDto(Long number, String name, int price, int stock) {
        this.number = number;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    //테스트용 팩토리 메서드
    public static ProductResponseDto of(Long number, String name, int price, int stock) {
        return new ProductResponseDto(number, name, price, stock);
    }



    //Entity로부터 변환하는 메서드이므로 Entity에서 값을 받아 DTO로 변환하기
    public static ProductResponseDto from(Product product) {
        return new ProductResponseDto(
            product.getNumber(),
            product.getName(),
            product.getPrice(),
            product.getStock()
        );
    }


}
