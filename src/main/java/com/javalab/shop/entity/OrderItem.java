package com.javalab.shop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 주문 상품 엔티티
 * - 주문한 상품의 정보를 담는 엔티티
 * - 주문 엔티티와 연관관계를 맺는다.
 * - 주문 엔티티와 상품 엔티티를 연관관계로 맺는다.
 */
@Entity
@Table(name = "order_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem extends BaseEntity {

    // 주문 아이템 엔티티의 기본키 id
    @Id
    @Column(name = "order_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 자동 생성방식을 데이터베이스에 위임 AUTO_INCREMENT
    private Long id;

    // 상품 엔티티(Item)와 OrderItem 다대일(N:1) 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // 주문 엔티티와(Order)와 OrderItem이 다대일(N:1) 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문 가격
    private int count; // 주문 수량

//    private LocalDateTime regTime; // 등록시간
//    private LocalDateTime updateTime; // 수정시간

    /**
     * 주문 상품 생성 메서드
     * - Item, 수량을 받아서 OrderItem 생성
     */
    public static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = new OrderItem();  // 주문 상품 생성
        orderItem.setItem(item);    // 주문 상품 설정
        orderItem.setCount(count);  // 주문 수량
        orderItem.setOrderPrice(item.getPrice());   // 주문 가격은 상품의 가격
        item.removeStock(count);    // 주문 수량만큼 재고 감소
        return orderItem;
    }

    /**
     * 총 주문 가격 계산
     * @return
     */
    public int getTotalPrice(){
        return orderPrice * count;
    }

    /**
     * 주문 취소
     * - 주문 수량만큼 재고 증가
     */
    public void cancel(){
        this.getItem().addStock(count);
    }
}
