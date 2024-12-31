package com.javalab.shop.entity;

import com.javalab.shop.constant.ItemSellStatus;
import com.javalab.shop.repository.ItemRepository;
import com.javalab.shop.repository.MemberRepository;
import com.javalab.shop.repository.OrderItemRepository;
import com.javalab.shop.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class OrderTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private EntityManager em;

    // 테스트용 아이템(상품) 생성 메소드
    public Item createItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    //@Commit // 테스트 종료 시 롤백하지 않고 DB에 반영, 테스트 끝나면 주석 처리해서 롤백되도록 할 것.
    public void cascadeTest() {
        // Given: Prepare an order with multiple order items
        Order order = new Order();

        // 3개의 Item 엔티티를 생성하여 Order 엔티티에 연관관계 설정
        for(int i=0;i<3;i++) {
            Item item = this.createItem(); // createItem() 메서드로 Item 객체 생성
            itemRepository.save(item); // item 엔티티 영속화, 이렇게 영속화된 item 엔티티는 OrderItem 엔티티에 연관관계 설정 시 사용 / 1차 캐시에 들어간거임
            OrderItem orderItem = new OrderItem(); // OrderItem 객체 생성
            orderItem.setItem(item); // 위에서 영속화환 item 엔티티를 OrderItems에 설정
            orderItem.setCount(10); // OrderItem에 수량 설정
            orderItem.setOrderPrice(1000); // OrderItem에 가격 설정
            orderItem.setOrder(order); // OrderItem에 Order 객체 설정
            order.getOrderItems().add(orderItem); // 위에서 생성한 OrderItem을 Order에 추가
         }

        // When : Save the order and clear the persistense context
        // Order 엔티티 영속화, saveAndFlush() 메서드를 사용하여 영속화환 엔티티를 즉시 DB에 반영
        // CascadeType.All 설정으로 Order 엔티티를 저장하면 연관된 OrderItem 엔티티도 함께 저장
        orderRepository.saveAndFlush(order);
        em.clear(); // 영속성 컨텍스트 초기화, 영속성 컨텍스트에 있는 엔티티를 DB에 반영하고 영속성 컨텍스트를 초기화

        // Then: check if order and order itmes are saved
        // 위에서 영속화한 Order 엔티티를 조회, 위에서 clear()  메서드로 영속성 컨텍스트 초기화 했으므로 DB에서 조회
        // 조회한 Order 엔티티가 없을 경우 EntityNotFoundException 발생
        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new);
        // Order 엔티티의 OrderItem 엔티티 개수가 3개인지 확인
        assertEquals(3, savedOrder.getOrderItems().size());
    }

    /**
     * Order 엔티티 생성 메서드
     * - Order 엔티티를 생성하고 OrderItem 엔티티를 생성하여 Order 엔티티에 추가
     * - Member 엔티티를 생성하여 Order 엔티티에 추가
     * - Order 엔티티를 영속화하여 DB에 저장
     */
    public Order createOrder() {
        // Order 엔티티 생성
        Order order = new Order();

        // Order 엔티티에 OrderItem 3ro cnrk
        for(int i=0;i<3;i++) {
            Item item = createItem(); // createItem() 메서드로 Item 엔티티 생성
            itemRepository.save(item); // item 엔티티 영속화
            OrderItem orderItem = new OrderItem(); // OrderItem 엔티티 생성
            orderItem.setItem(item); // OrderItem 엔티티에 Item 엔티티 연관관계 설정
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem); // OrderItem 엔티티를 OrderItem 엔티티에 추가
        }
        Member member = new Member(); // Member 엔티티 생성
        memberRepository.save(member); // Member 엔티티 영속화

        order.setMember(member); // Order 엔티티에 Member 엔티티 연관관계 설정
        orderRepository.save(order); // Order 엔티티 영속화
        return order;
    }

    /**
     * 고아객체 제거 테스트
     * - Order 엔티티의 OrderItem 엔티티 1개 제거
     * - Order 엔티티의 OrderItem 엔티티 1개를 제거하고 DB에 반영
     * - orphanRemoval = true를 @OneToMany 매핑에서 설정하면,
     *   부모 엔티티에서 참조가 제거된 자식 엔티티(고아 객체)가
     *   자동으로 데이터베이스에서도 삭제
     */
    @Test
    @DisplayName("고아객체 제거 테스트")
    //@Commit
    public void orphanRemovalTest() {
        Order order = this.createOrder(); // createOrder() 메서드로 Order 엔티티 생성, 여기에는 OrderItem 엔티티 3개가 포함
        // 영속성 컨텍스트에서 변경 감지, JPA는 @OneToMany 관계에 orphanRemoval = true가 설정된 경우,
        // 리스트에서 제거된 자식 엔티티가 데이터베이스에서 삭제되어야 한다고 판단하게 됨,
        order.getOrderItems().remove(0); // Order 엔티티의 OrderItem 엔티티 중에서 첫번째 OrderItem 제거
        em.flush(); // 영속성 컨텍스트 변경사항을 DB에 반영 즉, Order 엔티티의 OrderItem 엔티티 1개 제거
    }

    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest() {
        // Order 엔티티 생성, OrderItem 엔티티 3개 추가, Member 엔티티 생성, 영속화, Order 엔티티도 영속화
        Order order = this.createOrder();

        // getId() 메서드를 호출하여 Order 엔티티가 갖고있는 OrderItem 엔티티의 첫번째 엔티티의 ID를 조회
        Long orderItemId = order.getOrderItems().get(0).getId();

        em.flush(); // 영속성 컨텍스트의 변경사항을 DB에 반영, Order 엔티티와 OrderItem 엔티티를 DB에 저장
        em.clear(); // 영속성 컨텍스트를 초기화하여 객체를 분리 상태로 만들고, 이후 데이터베이스에서 객체를 새로 로드하도록 강제합
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);

        // OrderItem 엔티티의 Order 엔티티 조회
        // getOrder() 메서드를 호출하여 OderItem 엔티티의 Order 엔티티를 조회
        // getClass() 메서드를 호출하여 Order 엔티티의 클래스를 출력
        System.out.println("Order class : " + orderItem.getOrder().getClass());

        System.out.println("===========================");
        orderItem.getOrder().getOrderDate();
        System.out.println("===========================");

    }
}
