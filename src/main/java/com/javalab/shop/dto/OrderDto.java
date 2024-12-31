package com.javalab.shop.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 주문 화면에서 사용하는 DTO 클래스
 * - 화면에서 입력한 주문 관련 정보를 담아서 컨트롤러 레이어로 전달한다.
 * - Validation 어노테이션을 사용하여 입력값의 유효성을 검증한다.
 * - 상품 아이디와 주문 수량을 저장한다. .
 */
@Getter @Setter
public class OrderDto {

    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Long itemId;

    @Min(value = 1, message = "최소 주문 수량은 1개 입니다.")
    @Max(value = 999, message = "최대 주문 수량은 999개 입니다.")
    private int count;

}
