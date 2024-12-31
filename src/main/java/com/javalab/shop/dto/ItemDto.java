package com.javalab.shop.dto;

import com.javalab.shop.constant.ItemSellStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 상품 Dto
 * - 상품과 관련된 데이터 전송을 위한 Dto(Data Transfer Object) 클래스
 * - Item 엔티티와 매핑되는 DTO 클래스
 * - Item 엔티티의 필드를 그대로 사용하거나 필요한 필드만 사용할 수 있다.
 * - Item 엔티티와 DTO 클래스를 분리하여 엔티티의 변경이 DTO에 영향을 주지 않도록 한다.
 * - 상품 등록시 화면에서 입력된 값을 ItemDto로 받아서 Item 엔티티로 변환하여 저장한다.
 * 이제 엔티티와 dto 영역이 나눠진다 !! 원래는 엔티티가 테이블을 뭐 만들어서 직접 만들고 데이터를 싣고 소통했나?? 그랬나봄
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {


    private Long id;        //상품 코드
    private String itemNm; // 상품명, 이름을 지정하지 않음 item_nm
    private int price;  // 가격
    private int stockNumber; // 재고 수량
    private String itemDetail; // 상품 상세 설명
    private ItemSellStatus itemSellStatus; // 상품 판매 상태
    private LocalDateTime regTime; // 등록시간
    private LocalDateTime updateTime; // 수정시간
}
