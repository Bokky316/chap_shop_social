package com.javalab.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 장바구니 엔티티
 */
@Entity
@Table(name = "cart")
@Getter @Setter
@ToString
public class Cart extends BaseEntity {

    // 카트 번호(ID)
    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /*
        * @OneToOne: 일대일 관계 설정
        * @JoinColumn: 외래 키를 매핑 설정(member_id : Member 엔티티의 테이블 컬럼명)
        * fetch=FetchType.EAGER : 즉시 로딩 설정, 연관된 엔티티를 조인해서 함께 조회
        * fetch=FetchType.LAZY : 지연 로딩 설정, 연관된 엔티티를 조회하지 않고, 실제로 사용할 때 조회
     */
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    /**
     * 카트 생성
     * - 회원 정보를 받아서 카트를 생성한다.
     * - 회원 1명당 1개의 카트를 가질 수 있으므로 처음 장바구니에 상품을 담을 때는 카트를 생성해줘야 한다.
     * @param member
     * @return
     */
    public static Cart createCart(Member member){
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }

}
