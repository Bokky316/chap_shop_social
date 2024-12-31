package com.javalab.shop.service;

import com.javalab.shop.dto.*;
import com.javalab.shop.entity.Cart;
import com.javalab.shop.entity.CartItem;
import com.javalab.shop.entity.Item;
import com.javalab.shop.entity.Member;
import com.javalab.shop.repository.CartItemRepository;
import com.javalab.shop.repository.CartRepository;
import com.javalab.shop.repository.ItemRepository;
import com.javalab.shop.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 장바구니 서비스 클래스
 * - 장바구니 추가, 조회, 수정, 삭제 기능을 제공한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    /**
     * 장바구니에 상품 추가
     *
     * @param cartItemDto
     * @param email
     */
    public Long addCart(CartItemDto cartItemDto, String email) {
        // 1. 장바구니에 담을 상품 조회(상품이 영속 영역에 저장)
        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        // 2. 로그인 한 회원엔티티 조회
        Member member = memberRepository.findByEmail(email);

        // 3. 회원의 장바구니가 이미 만들어져 있는지 조회
        Cart cart = cartRepository.findByMemberId(member.getId());

        // 4. 장바구니가 없다면 새로 생성(최초로 장바구니 생성 회원)
        if (cart == null) {
            // 4.1. 장바구니 생성
            cart = Cart.createCart(member);
            // 4.2. 장바구니 저장(영속화)
            cartRepository.save(cart);
        }

        // 5. 장바구니에 상품이 이미 담겨있는지 조회
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        // 6. 장바구니에 상품이 이미 담겨있다면 수량만 추가
        if (savedCartItem != null) {
            // 6.1. 기존 장바구니 상품 수량 추가
            savedCartItem.addCount(cartItemDto.getCount());
            // 6.2. 장바구니 상품 ID 반환
            return savedCartItem.getId();
        } else {    // 7. 장바구니에 상품이 없다면 새로 장바구니 아이템 생성
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            // 7.1. 장바구니 상품 저장(영속화)
            cartItemRepository.save(cartItem);
            // 7.2. 장바구니 상품 ID 반환
            return cartItem.getId();
        }
    }

    /**
     * 장바구니 목록 조회
     * - 현재 로그인한 회원의 정보를 바탕으로 장바구니에 들어있는 상품 목록을 조회한다.
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email) {
        // 1. 장바구니 상세 정보를 담을 리스트 생성
        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();
        // 2. 현재 로그인한 회원의 정보를 바탕으로 회원 엔티티 조회
        Member member = memberRepository.findByEmail(email);
        // 3. 회원의 장바구니가 있는지 조회
        Cart cart = cartRepository.findByMemberId(member.getId());
        // 4. 장바구니가 없다면 빈 리스트 반환
        if (cart == null) {
            return cartDetailDtoList;
        }
        // 5. 장바구니에 담긴 상품 목록 조회
        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }

    /**
     * 현재 로그인 한 회원과 장바구니 상품의 소유자가 일치하는지 확인
     *
     * @param cartItemId
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email) {
        Member curMember = memberRepository.findByEmail(email);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember();

        if (!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) {
            return false;
        }

        return true;
    }

    /**
     * 장바구니 상품 수량 수정
     *
     * @param cartItemId
     * @param count
     */
    public void updateCartItemCount(Long cartItemId, int count) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }

    /**
     * 장바구니 상품 삭제
     *
     * @param cartItemId
     */
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    /**
     * 장바구니 상품 주문
     * - 컨트롤러 레이어에서 전달받은 장바구니 상품 리스트를 이용해서 주문을 생성한다.
     * - 주문 생성 후 장바구니 상품을 삭제한다.
     * @return
     */
//    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){
//        // 1. 주문할 상품 리스트를 담을 리스트 생성
//        List<OrderDto> orderDtoList = new ArrayList<>();
//        // 2. 장바구니 상품 리스트를 순회하면서 주문할 상품 리스트 생성
//        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
//            // 2.1. 장바구니 상품 조회해서 상품 ID와 수량을 이용해서 주문할 상품 리스트 생성
//            // cartOrderDto -> OrderDto 변환
//            CartItem cartItem = cartItemRepository
//                    .findById(cartOrderDto.getCartItemId())
//                    .orElseThrow(EntityNotFoundException::new);
//
//            OrderDto orderDto = new OrderDto();
//            orderDto.setItemId(cartItem.getItem().getId());
//            orderDto.setCount(cartItem.getCount());
//
//            orderDtoList.add(orderDto);
//        }
//
//        Long orderId = orderService.orders(orderDtoList, email);
//        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
//            CartItem cartItem = cartItemRepository
//                    .findById(cartOrderDto.getCartItemId())
//                    .orElseThrow(EntityNotFoundException::new);
//            cartItemRepository.delete(cartItem);
//        }
//
//        return orderId;
//    }
    public Long orderCartItem(List<CartOrderItemDto> cartOrderItems, String email) {
        // 1. 주문할 상품 리스트를 담을 리스트 생성
        List<OrderDto> orderDtoList = new ArrayList<>();

        // 2. CartOrderRequestDto 내부의 cartOrderItems를 순회하며 OrderDto 리스트 생성
        for (CartOrderItemDto cartOrderItemDto : cartOrderItems) {
            // 2.1. 장바구니 상품 조회
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderItemDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);

            // 2.2. OrderDto 객체 생성 및 값 설정
            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartOrderItemDto.getCount()); // 요청받은 수량 사용

            orderDtoList.add(orderDto);
        }

        // 3. 주문 서비스 호출하여 주문 생성
        Long orderId = orderService.orders(orderDtoList, email);

        // 4. 주문 완료 후 장바구니 항목 삭제
        for (CartOrderItemDto cartOrderItemDto : cartOrderItems) {
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderItemDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }

        // 5. 주문 ID 반환
        return orderId;
    }
}
