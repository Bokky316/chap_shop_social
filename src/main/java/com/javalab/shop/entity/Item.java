package com.javalab.shop.entity;

import com.javalab.shop.constant.ItemSellStatus;
import com.javalab.shop.dto.ItemFormDto;
import com.javalab.shop.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item extends BaseEntity {

    /*
     * @Id :  해당 필드를 기본 키로 설정
     * @Column : 해당 필드를 컬럼으로 설정
     * @GeneratedValue : 기본 키의 값을 자동으로 생성
     * GenerationType.AUTO : JPA 구현체가 자동으로 생성 방식을 결정
     * GenerationType.IDENTITY : 데이터베이스에 위임하여 기본 키 생성
     * GenerationType.SEQUENCE : 데이터베이스 시퀀스를 사용하여 기본 키 생성
     * GenerationType.TABLE : 키 생성 전용 테이블을 사용하여 기본 키 생성
     *
     */
    @Id
    @Column(name="item_id") // 컬럼명 지정
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;        //상품 코드

    @Column(nullable = false, length = 50) // 길이 50, null 불가
    private String itemNm; // 상품명, 이름을 지정하지 않음 item_nm

    @Column(name="price", nullable = false)
    private int price;  // 가격

    @Column(nullable = false)
    private int stockNumber; // 재고 수량

    // @Lob: 데이터베이스의 BLOB, CLOB 타입과 매핑
    @Lob
    @Column(nullable = false)
    private String itemDetail; // 상품 상세 설명

    // EnumType.STRING: Enum의 이름을 DB에 저장
    // 실제 SQL : `item_sell_status` enum('SELL', 'SOLD_OUT') DEFAULT NULL,
    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; // 상품 판매 상태

    // LocalDateTime : JPA 2.2부터 지원
    // LocalDateTime : 날짜와 시간을 모두 저장, 밀리 초 단위까지 저장
//    private LocalDateTime regTime; // 등록시간
//    private LocalDateTime updateTime; // 수정시간 extends BaseEntity 했으니까 이제 자동으로 들어와서 주석처리

    /**
     * ItemFormDto를 받아서 Item 엔티티를 업데이터
     * @param itemFormDto : 상품등록, 수정시 상품 정보를 전달하는 DTO
     * - 영속화 되어 있는 Item Entity의 값을 수정하면 더티체킹에 의해서 자동으로 업데이트 쿼리 실행
     * - 주로 상품 이미지를 제외한 나머지 상품 정보를 변경할 때 사용
     */
    public void updateItem(ItemFormDto itemFormDto) {
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    /**
     * 상품의 재고를 감소시킨다.
     * @param stockNumber
     */
    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;
        if(restStock<0){
            throw new OutOfStockException("상품의 재고가 부족 합니다. (현재 재고 수량: " + this.stockNumber + ")");
        }
        this.stockNumber = restStock;
    }

    /**
     * 상품의 재고를 증가시킨다.
     * @param stockNumber
     */
    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }


}
