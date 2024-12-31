package com.javalab.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 장바구니 주문 DTO
 * - 장바구니에 담긴 여러 상품을 주문할 때 데이터를 받아주는 DTO
 * - 장바구니에 담긴 상품의 ID와 수량을 담는다.
 *
 */
@Getter
@Setter
public class CartOrderDto {

    private Long cartItemId;

    private List<CartOrderDto> cartOrderDtoList;

}