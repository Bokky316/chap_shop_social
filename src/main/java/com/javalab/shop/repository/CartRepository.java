package com.javalab.shop.repository;

import com.javalab.shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
// jpa 리포지토리가 카트에대해 crud를 할 수 있게 제네릭타입으로 카트(키) 랑 타입을 넘겨 준거임!!!
public interface CartRepository extends JpaRepository<Cart, Long> {
    // 1. 회원 ID로 장바구니 조회
    Cart findByMemberId(Long memberId);
}
