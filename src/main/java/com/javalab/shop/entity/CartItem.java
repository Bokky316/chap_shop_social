package com.javalab.shop.entity;

import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "cart_item")
public class CartItem extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 자동생성, 마리아디비는, MySQL은 AUTO_INCREMENT 아까는 이거 안했음 시퀀스만들었음 // 오토는 스프링 jpa가 만들어주는거고 아이텐티티는 디비에 직접 명령하는거임
    @Column(name="cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // 다대일 관계, 카트 아이템 입장에서 바라보는 카트는 하나이다.!!! , 기본전략이 EAGER 생략되어있는거임 있는데 안보이는거라 생각
    @JoinColumn(name="cart_id") // 외래키 지정 / 이렇게 테이블 만들면 카트에 카트 아이디가 왜래키로 이 테이블에 들어온다는 이야기
    private Cart cart; // 카트를 통으로 넣어주고 저 위에 카트언더바 아이디만 제대로 맞춰줘야 함

    @ManyToOne(fetch = FetchType.LAZY)  // 다대일 관계!!, 아이템(상품) 입장에서도 바라보는 상품은 하나!!, 기본전략이 EAGER
    @JoinColumn(name="item_id")
    private Item item;

    private int count; // 수량 이건 퀀티리임(장바구니 수량)

    /**
     * 장바구니에 담을 상품 엔티티를 생성하는 메소드
     * 장바구니에 담을 수량을 증가시켜주는 메소드
     * @param cart : CartItem의 상위 엔티티
     * @param item
     * @param count
     * @return
     */
    public static CartItem createCartItem(Cart cart, Item item, int count) {
        CartItem cartItem = new CartItem(); // 카트 아이템 엔티티 생성
        cartItem.setCart(cart); // 카트 아이템에 카트 설정, 카트 아이템과 카트가 연결
        cartItem.setItem(item); // 카트 아이템에 상품 설정, 카트 아이템과 상품이 연결
        cartItem.setCount(count); // 카트 아이템에 수량 설정
        return cartItem;
    }

    /**
     * 장바구니에 기존에 담겨있는 상품을 추가로 또 담았을 경우 수량을 증가시켜주는 메서드
     * @param count
     */
    public void addCount(int count){
        this.count += count;
    }

    /**
     * 장바구니에 담긴 상품의 수량을 수정하는 메소드
     * @param count
     */
    public void updateCount(int count){
        this.count = count;
    }
}
