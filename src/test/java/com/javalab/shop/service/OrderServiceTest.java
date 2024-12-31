package com.javalab.shop.service;

import com.javalab.shop.constant.ItemSellStatus;
import com.javalab.shop.constant.OrderStatus;
import com.javalab.shop.dto.OrderDto;
import com.javalab.shop.entity.Item;
import com.javalab.shop.entity.Member;
import com.javalab.shop.entity.Order;
import com.javalab.shop.entity.OrderItem;
import com.javalab.shop.repository.ItemRepository;
import com.javalab.shop.repository.MemberRepository;
import com.javalab.shop.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    /**
     * 상품 저장
     * @return
     */
    public Item saveItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    /**
     * 회원 저장
     * @return
     */
    public Member saveMember(){
        Member member = new Member();
        member.setEmail("test2@test.com");  // 실제로 데이터베이스에 있는 이메일을 넣으면 중복 에러 발생
        return memberRepository.save(member);
    }

    @Test
    @DisplayName("주문 테스트")
    //@Commit
    public void order(){
        // 1. given (상품 저장, 회원 저장)
        Item item = saveItem(); // 상품 저장
        Member member = saveMember();   // 회원 저장

        // 2. when (주문 생성)
        OrderDto orderDto = new OrderDto(); // 주문 DTO 생성
        // 주문 수량 세팅
        orderDto.setCount(10);  // 주문 수량 세팅 (10개)
        // 상품 ID 세팅
        orderDto.setItemId(item.getId());   // 상품 ID 세팅, 이시점에는 영속화 상태가 아니므로 getId()로 조회해도 실제 DB에 존재하지 않는다.

        // 주문 생성
        Long orderId = orderService.order(orderDto, member.getEmail());
        // 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);

        // 3. then (주문 생성 확인)
        List<OrderItem> orderItems = order.getOrderItems();
        // 주문 총 가격 계산
        int totalPrice = orderDto.getCount()*item.getPrice();
        // 주문 총 가격과 주문의 총 가격이 같은지 확인
        assertEquals(totalPrice, order.getTotalPrice());
    }

    @Test
    @DisplayName("주문 취소 테스트")
    @Commit
    public void cancelOrder(){
        // 1. given (상품 저장, 회원 저장)
        Item item = saveItem();
        Member member = saveMember();   // [주의]회원 이메일은 실제 데이터베이스에 없는 이메일을 넣어야 한다. 중복 에러 발생

        // 2. when (주문 생성, 주문 취소)
        OrderDto orderDto = new OrderDto();
        // 주문 수량 세팅
        orderDto.setCount(10);
        // 상품 ID 세팅
        orderDto.setItemId(item.getId());
        // 주문 생성(영속화 상태로 만들기)
        Long orderId = orderService.order(orderDto, member.getEmail());
        // 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        // 주문 취소
        orderService.cancelOrder(orderId);


        // 3. then (주문 취소 확인)
        assertEquals(OrderStatus.CANCEL, order.getOrderStatus());
        // 주문 취소 후 상품 재고 확인
        assertEquals(100, item.getStockNumber());
    }
}