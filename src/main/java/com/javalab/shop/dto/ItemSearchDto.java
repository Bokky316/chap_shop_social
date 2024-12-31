package com.javalab.shop.dto;

import com.javalab.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * 검색 조건을 담는 DTO 클래스
 * - 검색 조건을 담는 필드들을 가지고 있습니다.
 * - all : 상품 등록일 전체 검색
 * - 1d : 상품 등록일 1일 검색
 * - 1w : 상품 등록일 1주 검색
 * - 1m : 상품 등록일 1개월 검색
 * - 6m : 상품 등록일 6개월 검색
 */
@Getter@Setter
public class ItemSearchDto {


    private String searchDateType;

    private ItemSellStatus searchSellStatus;

    private String searchBy;    // 검색 조건 : 상품명, 상품등록자

    // 조회 검색어 저장, searchBy가 itemNm이면 상품명, createdBy면 상품등록자로 검색하도록  설정
    private String searchQuery = "";
}
