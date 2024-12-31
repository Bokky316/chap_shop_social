package com.javalab.shop.dto;

import com.javalab.shop.constant.OrderStatus;
import com.javalab.shop.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 정보를 전달하기 위한 DTO
 * - 주문 목록 조회할 떄 사용
 *
 */
@Getter @Setter
public class OrderHistDto {

    /**
     * Order 엔티티를 받아서 OrderHistDto로 변환
     * - order : 데이터베이스에서 조회한 Order 엔티티
     * @param order
     */
    public OrderHistDto(Order order){
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
    }

    private Long orderId; //주문아이디
    private String orderDate; //주문날짜
    private OrderStatus orderStatus; //주문 상태
    // 주문 상품을 담기 위한 리스트
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    //주문 상품을 추가하는 메서드
    public void addOrderItemDto(OrderItemDto orderItemDto){
        orderItemDtoList.add(orderItemDto);
    }

}