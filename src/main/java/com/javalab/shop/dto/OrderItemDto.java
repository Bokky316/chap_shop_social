package com.javalab.shop.dto;

import com.javalab.shop.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

/**
 * 주문 상품 정보 DTO
 * - 조회한 주문 상품 정보를 담아서 화면에 전달 할 때 사용
 */
@Getter @Setter
public class OrderItemDto {

    public OrderItemDto(OrderItem orderItem, String imgUrl){
        this.itemNm = orderItem.getItem().getItemNm();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl = imgUrl;
    }

    private String itemNm; //상품명
    private int count; //주문 수량

    private int orderPrice; //주문 금액
    private String imgUrl; //상품 이미지 경로

}