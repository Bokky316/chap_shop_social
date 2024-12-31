package com.javalab.shop.service;

import com.javalab.shop.dto.OrderDto;
import com.javalab.shop.dto.OrderHistDto;
import com.javalab.shop.dto.OrderItemDto;
import com.javalab.shop.entity.*;
import com.javalab.shop.repository.ItemImgRepository;
import com.javalab.shop.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import com.javalab.shop.repository.MemberRepository;
import com.javalab.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    // 의존성 주입
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final ItemImgRepository itemImgRepository;

    // 주문
    public Long order(OrderDto orderDto, String email) {
        // 1. 주문할 상품 조회(영속화)
        Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        // 2. 주문자 조회, 영속화 상태로 만들기
        Member member = memberRepository.findByEmail(email);
        // 3. 주문 생성
        // 3.1. 주문 상품 생성
        List<OrderItem> orderItemList = new ArrayList<>();
        // 3.2. 주문 아이템 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        // 3.3. 주문 아이템 리스트를 주문에 추가
        orderItemList.add(orderItem);
        // 3.4. 주문 생성(주문자, 주문 상품 리스트)
        Order order = Order.createOrder(member, orderItemList);
        // 3.5. 주문 저장, 영속화 상태로 만들기, 주문 저장 쿼리문 실행
        orderRepository.save(order);

        return order.getId();
    }


    /**
     * 주문 목록 조회
     * - 주문 목록을 조회하기 위해서는 회원의 이메일이 필요하다.
     * - 주문 목록은 페이징 처리가 되어야 한다.
     * - 주문 목록은 주문 번호, 주문 일자, 주문 상품 리스트, 주문 상태로 구성된다.
     * - 주문 상품 리스트는 주문 상품 번호, 상품명, 상품 가격, 상품 이미지로 구성된다.
     * - 주문 목록은 주문 일자를 기준으로 내림차순 정렬한다.
     * @param email 회원 이메일
     * @param pageable 페이징 처리
     * @return
     */
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {

        // 1. 주문 목록 조회
        List<Order> orders = orderRepository.findOrders(email, pageable);
        // 2. 주문 목록 총 개수 조회
        Long totalCount = orderRepository.countOrder(email);
        // 3. 주문 목록 저장용 ArrayList 생성
        List<OrderHistDto> orderHistDtos = new ArrayList<>();
        // 3.1. 주문 목록을 순회하면서 주문 정보를 DTO로 변환
        for (Order order : orders) {
            // 3.1.1. 주문 한 건에 대한 주문 DTO 생성
            OrderHistDto orderHistDto = new OrderHistDto(order);
            // 3.1.2. 주문 정보에서 주문 상품 리스트 조회
            List<OrderItem> orderItems = order.getOrderItems();
            // 3.2. 하나의 주문에서 여러 상품을 주문할 수 있으므로 상품 리스트를 순회하면서 상품 정보를 DTO로 변환
            for (OrderItem orderItem : orderItems) {
                // 3.2.1 주문 상품 리스트에서 각 상품의 대표 이미지만 조회해서 ItemImg 객체 생성
                ItemImg itemImg = itemImgRepository
                        .findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");
                // 3.2.2. 주문 상품 DTO 생성
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                // 3.2.3 주문 DTO에 주문 상품 추가
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            // 3.3. 주문 목록에 주문 DTO 추가, 한 건의 주문 정보를 전체 주문 목록에 추가
            orderHistDtos.add(orderHistDto);
        }
        // 4. 주문 목록 반환
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }

    /**
     * 주문 검증 : 주문 취소 권한 확인
     * - 주문 취소를 위해서는 주문 번호와 주문자 이메일이 필요하다.
     * - 주문 번호로 주문을 조회한다.
     * @param orderId
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){
        // 현재 로그인 한 회원 정보 조회
        Member curMember = memberRepository.findByEmail(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        // 주문을 낸 회원 정보 조회
        Member savedMember = order.getMember();
        // 주문을 낸 회원과 현재 로그인한 회원이 같은지 확인
        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }

        return true;
    }

    /**
     * 주문 취소
     * - 주문 취소를 위해서는 주문 번호가 필요하다.
     * - 주문 번호로 주문을 조회한다.
     * - 주문 취소를 한다.
     * @param orderId
     */
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }

    /**
     * 장바구니에서 주문을 여러개 선택해서 주문
     * - 장바구니에서 여러 상품을 선택해서 주문할 수 있다.
     * - 주문할 상품 리스트와 회원 이메일을 인자로 받는다.
     * - 주문할 회원을 조회한다.
     * - 전달받은 주문 상품 리스트를 순회하면서 장바구니에서 전달받은 dto를 이용해서 주문 상품 엔티티를 생성한다.
     * @param orderDtoList
     * @param email
     * @return
     */
    public Long orders(List<OrderDto> orderDtoList, String email){
        // 1. 주문자 조회
        Member member = memberRepository.findByEmail(email);
        // 2. 주문 상품 리스트 저장을 위한 ArrayList 생성
        List<OrderItem> orderItemList = new ArrayList<>();
        // 3. 장바구니에서 전달받은 dto를 순회하면서 주문 상품 엔티티 생성 후 리스트에 추가
        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }
        // 4. 위에서 생성한 주문 상품 리스트와 주문자를 이용해서 주문 엔티티 생성
        Order order = Order.createOrder(member, orderItemList);
        // 5. 주문 저장(영속화)
        orderRepository.save(order);
        // 6. 주문 번호 반환
        return order.getId();
    }




}
