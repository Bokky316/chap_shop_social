package com.javalab.shop.repository;

//import com.javalab.shop.dto.CartDetailDto;
import com.javalab.shop.dto.CartDetailDto;
import com.javalab.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 장바구니 상품 Repository
 * - 장바구니에 들어갈 상품 조회, 저장, 삭제 기능 제공
 */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * 장바구니에 담긴 상품을 조회하는 메서드
     * @param cartId
     * @param itemId
     */
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    /**
     * 장바구니에 담긴 상품 조회
     * new com.javalab.shop.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) : CartDetailDto 객체 생성
     * 이 쿼리에서 new com.javalab.shop.dto.CartDetailDto(...)는 CartDetailDto의 생성자를 호출하여 JPQL 쿼리 결과를 DTO 객체로 변환합니다.
     * 이 방식으로 데이터를 엔티티로 불필요하게 매핑하지 않고 직접 DTO로 반환할 수 있어 성능상 유리합니다.
     * JPQL이다 객체지향적인 쿼리이고 일반적인 쿼리랑 다름
     * 쿼리문에다가 new를 써놓음 클래스의 풀경로도 들어가 있음
     * 원래는 쿼리의 실행결과를 받아서 담는거를 해야하는데 이렇게하면 안해도 됨
     * 쿼리 결과가 CartDetailDto에 담아져버린다.
     */
    @Query("select new com.javalab.shop.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) " +
            "from CartItem ci, ItemImg im " +
            "join ci.item i " +
            "where ci.cart.id = :cartId " +
            "and im.item.id = ci.item.id " +
            "and im.repimgYn = 'Y' " +
            "order by ci.regTime desc"
    )
    List<CartDetailDto> findCartDetailDtoList(@Param("cartId") Long cartId);
}
