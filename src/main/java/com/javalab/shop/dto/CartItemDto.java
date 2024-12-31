package com.javalab.shop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 장바구니에 담을 상품 정보를 담는 DTO
 * - 상품 상세 페이지에서 장바구니에 담을 상품의 아이디와 수량을 전달 받을 때 사용
 */
@Getter @Setter
public class CartItemDto {

    @NotNull(message = "상품 아이디는 필수 입력 값 입니다.")
    private Long itemId;

    @Min(value = 1, message = "최소 1개 이상 담아주세요")
    private int count;

}