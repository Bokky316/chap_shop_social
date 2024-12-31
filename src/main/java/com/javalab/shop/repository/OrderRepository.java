package com.javalab.shop.repository;

import com.javalab.shop.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 주문 CRUD Repository
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * 회원 이메일로 주문 조회, 주문은 최신순으로 정렬
     * @param email
     * @param pageable
     * @return
     */
    @Query("select o from Order o " +
            "where o.member.email = :email " +
            "order by o.orderDate desc"
    )
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    /**
     * 회원 이메일로 주문 수 조회
     * - 페이징 처리를 위해 count 쿼리를 별도로 작성
     * @param email
     * @return
     */
    @Query("select count(o) from Order o " +
            "where o.member.email = :email"
    )
    Long countOrder(@Param("email") String email);
}
