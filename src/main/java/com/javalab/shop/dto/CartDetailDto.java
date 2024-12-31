package com.javalab.shop.dto;

import lombok.Getter;
import lombok.Setter;

/**
 *  장바구니 상세 정보를 담는 DTO
 *  - 장바구니 페이지에서 상품의 상세 정보를 보여줄 때 사용
 */
@Getter @Setter
public class CartDetailDto {

    private Long cartItemId; //장바구니 상품 아이디

    private String itemNm; //상품명

    private int price; //상품 금액

    private int count; //수량

    private String imgUrl; //상품 이미지 경로

    /**
     * 생성자
     * - JPQL 쿼리의 결과를 DTO 객체로 바로 매핑하기 위해 생성자를 사용
     * - JPA는 기본적으로 엔티티를 반환하지만, DTO로 반환하려면 new 키워드와 함께 생성자를 호출해야 합니다.
     * - 생성자의 매개변수 순서와 타입이 JPQL 쿼리에서 반환되는 필드 순서 및 타입과 정확히 일치해야 합니다.
     */
    public CartDetailDto(Long cartItemId, String itemNm, int price, int count, String imgUrl){
        this.cartItemId = cartItemId;
        this.itemNm = itemNm;
        this.price = price;
        this.count = count;
        this.imgUrl = imgUrl;
    }

}