package com.javalab.shop.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

/**
 * 메인 화면에 상품을 노출하기 위한 DTO 클래스
 * - 메인 화면에 상품을 노출하기 위한 필드들을 가지고 있습니다.
 */
@Getter @Setter
public class MainItemDto {

    private Long id;

    private String itemNm;

    private String itemDetail;

    private String imgUrl;

    private Integer price;

    /**
     * 생성자
     * - QueryDSL을 사용하여 생성자를 생성하기 위해서는 @QueryProjection 어노테이션을 추가해야 합니다.
     * - QueryDSL을 사용하여 생성자를 생성하기 위해서는 필드의 순서, 타입, 이름이 일치해야 합니다.
     * - 생성자에 QueryDsl을 선언하면 QueryDsl 결과 조회시 MainItemDto로 바로 조회 결과를 받을 수 있습니다.
     * - DTO의 생성자에 @QueryProjection을 사용하면 QueryDSL이 조회 결과를 DTO의 생성자로 자동 매핑하여
     *   DTO 객체를 생성하므로, 별도로 값을 이전하는 작업이 필요하지 않는다.
     */
    @QueryProjection
    public MainItemDto(Long id, String itemNm, String itemDetail, String imgUrl, Integer price){
        this.id = id;
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
    }

}