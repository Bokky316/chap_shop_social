package com.javalab.shop.repository;

import com.javalab.shop.constant.ItemSellStatus;
import com.javalab.shop.entity.Item;
import com.javalab.shop.entity.QItem;
import com.javalab.shop.exception.ItemNotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import javax.swing.text.Style;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.ClassUtils.isPresent;



/**
 * 상품 레파지토리 테스트
 * @SpringBootTest : 스프링 부트 테스트 어노테이션
 * - 스프링 부트 애플리케이션 컨텍스트를 로드하여 테스트에 사용
 * - 테스트 클래스에 선언하면 테스트 클래스 내에서 스프링 빈을 주입받을 수 있음
 * @Log4j2 : 롬복 로깅 어노테이션, log 변수를 사용하여 로깅 기능을 사용할 수 있음.
 * - 롬복을 사용하여 로깅 기능을 사용할 수 있음
 * - build.gradle testCompileOnly, testAnnotationProcessor에 롬복 테스트 의존성 추가
 * @Transactional : 테스트 메서드 실행 후 자동 롤백 처리
 */
@SpringBootTest
@Log4j2
@Transactional
public class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository; // 테스트할 ItemRepository 빈 주입

    // 아이템 한개 저장 테스트
    @Test
    @DisplayName("상품 저장 테스트")
//    @Rollback(false) // 롤백방지 / 하게되면 db에 실제로 넣게 된다
    public void saveItemTest() {
        // Given
        Item item = Item.builder()
                .itemNm("테스트 상품 한개 저장")
                .price(10000)
                .stockNumber(50)
                .itemDetail("테스트 상품 상세 설명")
                .itemSellStatus(ItemSellStatus.SELL)
                //.regTime(LocalDateTime.now())
                .build();

        // When(작동) : 테스트 대상 메서드 호출
        // save() : Item 엔티티를 저장하는 메서드(영속 상태로 전환)
        Item savedItem = itemRepository.save(item); // 저장된 Item 엔티티 반환

        // Then(검증) : 위에서 생성한 엔티티와 영속화된 엔티티를 비교해서 검증
        // import static org.assertj.core.api.Assertions.assertThat; 추가
        // 1차 검증, 저장된 Item 엔티티의 ID가 null이 아닌지 검증
        assertThat(savedItem.getId()).isNotNull();
        // 2. 검증, 저장된 Item 엔티티의 상품명과 테스트 Item 엔티티의 상품명이 같은지 검증
        assertThat(savedItem.getItemNm()).isEqualTo(item.getItemNm());
        // 저장된 값 확인
        log.info("savedItem: {}", savedItem);
    }



    // 아이템 목록 조회 테스트
    @Test
    @DisplayName("상품 목록 조회 테스트")
    public void findAllItemsTest() {
        // Given : 테스트용 Item 엔티티 10개 저장
        saveTestItems(); // 아이템 여러개를 영속화 시키는 메서드(영속 영역에 저장)

        // When : 테스트 대상 메서드 호출
        // findAll() : Item 엔티티 전체 목록을 영속 영역에서 조회, 영속 영역에 없으면 DB에서 조회
        List<Item> items = itemRepository.findAll(); // findAll() : ListCrudRepository 인터페이스의

        // Then : 저장된 Item 엔티티의 개수가 10개 인지 검증
        assertThat(items).isNotEmpty(); // 1차 검증(단정) / 당연히 낫널이어야하고 비어있지 않아야 한다. 낫널의 업글버전
        assertThat(items.size()).isGreaterThan(10); // 2차 검증(isGreaterThan(10) 10개 이상인지 검증
        // 저장된 값 확인
        items.forEach(item -> log.info(item));
    }

    /**
     * 상품 한개 조회(목록에서 번호 눌렀을 떄)
     * @return
     */
    @Test
    @DisplayName("상품 한 개 조회 테스트")
    public void findItemByIdTest() {
        // Given
        Item savedItem = saveTestItem(); // 테스트용 Item 엔티티 저장

        // When
        // findById() : Item 엔티티 ID로 Item 엔티티를 조회
        Optional<Item> item = itemRepository.findById(savedItem.getId()); // Optional로 반환
        // Item item = itemRepository.findById(savedItem.getId()).orElse(null); // Optional로 반환
//        Item item2 = itemRepository.findById(101L).orElseThrow(() -> new NoSuchElementException("해당 ID의 상품이 존재하지 않습니다."));
        // 사용자 정의 예외 (내가 만든 것)
//        Item item2 = itemRepository.findById(101L).orElseThrow(() -> new ItemNotFoundException("해당 ID의 상품이 존재하지 않습니다."));

        // Then
        // isPresent() : Optional 객체에 값이 존재하는지 확인
        assertThat(item).isPresent(); // 1차 검증 (조회한 Item 엔티티가 존재하는지 검증)
        // 2차 검증 (조회한 Item 엔티티의 상품명이 저장된 Item 엔티티의 상품명과 같은지 검증)
        // get() : Optional 객체에 저장된 Item 엔티티를 꺼냄(반환)
        assertThat(item.get().getItemNm()).isEqualTo(savedItem.getItemNm());
        log.info(item.get()); // get() : Optional 타입 반환 값에서  Item 엔티티 꺼냄
    }

    /**
     * 상품 수정 테스트
     */
    @Test
    @DisplayName("상품 수정 테스트")
//    @Rollback(false) // 롤백 방지
    public void updateItemTest() {
        // Given : 테스트용 Item 엔티티 저장
        Item savedItem = saveTestItem();    // 테스트 Item 엔티티 저장(영속 상태로 전환)

        // when : 테스트 대상 메서드 호출
        // 영속화된 엔티티를 수정하면 Dirty Checking으로 인해 수정된 내용이 자동으로 반영
        // 구제적으로 영속영역에 있는 엔티티를 수정하면 오렴되고 나중에 트랜잭션 끝나는 시점에 자동 반영
        savedItem.setPrice(15000);      // 가격 수정
        savedItem.setStockNumber(100);  // 재고수량 수정

        // save() : Item 엔티티를 저장하는 메서드(영속 상태로 전환)
        //Item updatedItem = itemRepository.save(savedItem); // 수정된 Item 엔티티 반환
        // insert때와 똑같은 save를 해도 이미 연속영역에 있으면 insert를 안하고 update를 알아서 한다
        // 지금 상황은 save해서 insert가 데베로 들어가진 않고 연속영역에 있는 상황에서 똑같은걸 save한거임

        Item updatedItem = itemRepository.findById(savedItem.getId()).orElseThrow(); // 수정된
        // 얘는 왜 save도 안했는데 저장이 되었을까? dirty checking과 관련있음

        // Then : 수정된 Item 엔티티의 가격과 재고수량이 수정된것과 같은지 검증
        assertThat(updatedItem.getPrice()).isEqualTo(15000);
        assertThat(updatedItem.getStockNumber()).isEqualTo(100);
        // 수정된 값 확인
        log.info(updatedItem);
    }


    /**
     * 상품 삭제 테스트
     */
    @Test
    @DisplayName("상품 삭제 테스트")
    public void deleteItemTest() {
        // Given
        Item savedItem = saveTestItem();    // 테스트용 Item 엔티티 저장(영속 영역에 저장)
        Long itemId = savedItem.getId();    // 저장된 Item 엔티티 ID 저장

        // When
        itemRepository.deleteById(itemId);  // 위에서 저장한  Item 엔티티 ID로 삭제

        // Then
        Optional<Item> deleteItem = itemRepository.findById(itemId);
        assertThat(deleteItem).isNotPresent();  // 삭제된 Item 엔티티가 존재하지 않는지 검즌
        log.info("상품 ID " + itemId + " 는 삭제되었습니다.");
    }

    /**
     * 상품명으로 상품 검색 테스트
     */
    @Test
    @DisplayName("상품명으로 상품 검색 테스트")
    public void findByItemNmTest() {
        // Given
        saveTestItems(); // 테스트용 데이터 저장

        // When
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품 1");

        // Then 검증
        assertThat(itemList).isNotEmpty(); // 결과가 비어 있지 않은지 검증

        log.info("검색된 상품 목록:");
        itemList.forEach(item -> log.info(item.toString()));
    }

    /**
     * 상품명과 상품 설명으로 상품 검색 테스트
     */
    @Test
    @DisplayName("상품명과 상품 설명으로 상품 검색 테스트")
    public void findByItemNmOrItemDetailTest() {
        // Given
        saveTestItems(); // 테스트용 데이터 저장

        // When
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품 1","테스트 상품 상세 설명 5");
        // Then
        assertThat(itemList).isNotEmpty();  // 결과가 비어 있지 않은지 검증

        log.info("검색된 상품 목록: ");
        itemList.forEach(item -> log.info(item.toString()));
    }

    /**
     * 가격이 price보다 작은 상품을 조회하는 테스트
     */
    @Test
    @DisplayName("가격이 price보다 작은 상품을 조회하는 테스트")
    public void findByPriceLessThanTest() {
        // Given
        saveTestItems(); // 테스트용 데이터 저장

        // 가격 조건 설정
        int searchPrice = 10005;

        // When 10005보다 작은거
        List<Item> itemList = itemRepository.findByPriceLessThan(searchPrice);

        // Then 검증
        assertThat(itemList).isNotEmpty();  // 결과가 비어 있지 않은지 검증
        assertThat(itemList.size()).isGreaterThanOrEqualTo(1); // 결과가 1개 이상인지 검증
        log.info("상품 가격이 " + searchPrice + "보다 작은 상품 목록 ====================");
        itemList.forEach(item -> log.info(item.toString()));
        log.info("검색된 상품 목록: ");
        itemList.forEach(item -> log.info(item.toString()));
    }

    /**
     * 상품 상세 설명을 받아서 조회하고 정렬순서는 가격이 높은 순서로 조회하는 테스트
     */
    @Test
    @DisplayName("상품 상세 설명을 받아서 조회하고 정렬순서는 가격이 높은 순서로 조회하는 테스트")
    public void findByItemDetailTest() {
        // Given
        saveTestItems(); // 테스트용 데이터 저장

        // 상품 상세 설명 조건 설정
        String searchItemDetail = "테스트 상품 상세 설명";

        // When
        List<Item> itemList = itemRepository.findByItemDetail(searchItemDetail);

        // Then 검증
        assertThat(itemList).isNotEmpty();  // 결과가 비어 있지 않은지 검증
        assertThat(itemList.size()).isGreaterThanOrEqualTo(1); // 결과가 1개 이상인지 검증
        log.info("상품 상세 설명이 " + searchItemDetail + "인 상품 목록 ====================");
        itemList.forEach(item -> log.info(item.toString()));
    }

    /**
     * 네이티브 쿼리를 이용한 메소드 테스트
     * @return
     */
    @Test
    @DisplayName("네이티브 쿼리를 이용한 상품 설명으로 조회 메소드 테스트")
    public void findByItemDetailByNativeTest() {
        // Given
        saveTestItems(); // 테이스용 데이터 저장

        // 상품 상세 설명 조건 설명
        String searchItemDetail = "테스트 상품 상세 설명";

        // When
        List<Item> itemList = itemRepository.findByItemDetailByNative(searchItemDetail);

        // Then 검증
        assertThat(itemList).isNotEmpty();  // 결과가 비어 있지 않은지 검증
        assertThat(itemList.size()).isGreaterThanOrEqualTo(1); // 결과가 1개 이상인지 검증
        log.info("상품 상세 설명이 " + searchItemDetail + "인 상품 목록 ====================");
        itemList.forEach(item -> log.info(item.toString()));
    }

    /**************************************************************************/
    /*************    QueryDSL을 사용한 상품 조회 테스트  *************************/
    /**************************************************************************/
    // QItem이 필요한 이유는 QueryDSL에서 타입 안전한 쿼리를 작성하기 위해서이다.
    // QueryDSL 테스트를 위한 JPAQueryFactory 주입
    // JPAQueryFactory : QueryDSL을 사용하기 위한 팩토리 클래스로 EntityManeger를 주입받아 생성
    // JPAQueryFactory queryFactory;

    /**
     * QueryDSL을 사용하기 위한 JPAQueryFactory 를 세터 주입
     * - EntityManeger를 주입받아 JPAQueryFactory 생성
     * @param entityManager
     * @return
     */
//    @Autowired
//    public void setQueryFactory(EntityManager entityManager) {
//        this.queryFactory = new JPAQueryFactory(entityManager);
//    }

    // EntityManager 의존성 주입
    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("QueryDSL로 상품 상태와 상세 설명으로 조회 테스트")
    public void findItemsByStatusAndDetailTest() {
        // Given
        saveTestItems(); // 테스트용 데이터 저장

        // QueryDSL 조건
        ItemSellStatus status = ItemSellStatus.SELL; // 판매 상태가 SELL인 상품
        String searchDetail = "상세 설명 1";    // 상품 상세 설명에 "상세 설명 1"이 포함된 상품

        // JPAQueryFactory : QueryDSL을 사용하기 위한 팩토리 클래스로 EntityManager를 주입받아 생성
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        // Q-type Domain 클래스 생성, QItem.item으로 Item 엔티티를 조회할 수 있는 QItem 클래스 객체 참조
        // item : QItem 클래스의 인스턴스
        QItem qItem = QItem.item;

        // When
        // queryFactory의 역할 : JPAQuery(쿼리) 객체를 생성gkdu QueryDSL을 사용하여 쿼리를 작성
        // selectForm() : 조회할 엔티티를 지정, 매개변수 QItem.item으로 Item 엔티티를 조회
        // where() : 조회 조건을 지정, 매개변수로 조건을 지정, 매개변수 : 조건식
        // fetch() : 조회 결과를 반환, 조회 결과를 List로 반환
        List<Item> items = queryFactory
                .selectFrom(qItem)                                  // 조회할 엔티티 지정
                .where(                                             // where를 만약 wheere 하면 바로 틀린거 잡아냄
                        qItem.itemSellStatus.eq(status),            // 상태가 SELL인 상품 / itemSellStatus 오타나도 바로 잡아냄
                        qItem.itemDetail.contains(searchDetail)     // 안에 한번에 썼으니 AND 조건, 상세 설명에 특정 키워드 포함(contains= like)
                )
                .orderBy(qItem.price.asc())                         // 가격 기준 오름차순 정렬
                .fetch();                                           // 결과 조회

        // Then
        assertThat(items).isNotEmpty(); // 결과가 비어있지 않은지 검증
        items.forEach(item -> {
            log.info(item.toString());
        });

    }

    /**
     * QueryDSL로 상품 가격, 판매상태, 페이징, 정렬 조회 테스트
     * - 가격이 price > 10003 인 상품을 조회
     * - itemSellStatus = "SELL"인 상품만 조회
     * - pageable 설정 : 페이지 번호 0, 한 페이지 크기 5
     * - 정렬 : 가격 기준 내림차순
     */
    @Test
    @DisplayName("QueryDSL 조건 조회 및 페이징 테스트")
    public void findItemsWithConditionsTest() {
        // Given
        saveTestItems(); // 테스트용 데이터 저장

        // JPAQueryFactory : QueryDSL을 사용하기 위한 팩토리 클래스로 EntityManager를 주입받아 생성
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        // QueryDSL용 QItem 객체 생성
        QItem qItem = QItem.item;

        // 조건 생성
        // BooleanBuilder : QueryDSL에서 사용하는 조건을 생성하는 클래스
        // booleanBuilder.and(조건식) : AND 조건을 추가
        // booleanBuilder.or(조건식) : OR 조건을 추가
        BooleanBuilder builder = new BooleanBuilder();

        // 검색 조건 설정
        String searchDetail = "테스트 상품 상세 설명";
        int priceCondition = 10003;

        // booleanBuilder를 통해서 QueryDSL 조건 설정
        // builder.and(조건식) : 첫번째 AND 조건을 추가, 이 조건은 첫번째 조건과 그 다음에 오는 조건의 연결을 결정한다. 여기도 마찬가지로 조건이 한개면(두번째 조건이 없으면 무 의미하다! !! !!!)
        // 두번째 and() : 두번째 조건과 세번째 조건을 AND로 연결
        // 세번째 and() : 세번째 조건과 네번째 조건을 AND로 연결한다. 이때!!!! 4번째가 안오면 무의미하다.!!!! ! ! ! ! !
//        builder.and(qItem.itemDetail.contains(searchDetail)) // itemDetail LIKE 검색
//                .and(qItem.price.gt(priceCondition))          // price > 10003
//                .and(qItem.itemSellStatus.eq(ItemSellStatus.SELL)); // itemSellStatus = "SELL"

        // QueryDSL 조건 설정 or() 사용 (or and 섞어서 사용했을 경우!!)
        // 첫번째 or() : 첫번째 조건과 두번째 조건을 OR로 연결
        // 두번째 or() : 사용 안되며 첫번째 조건과 두번째 조건이 그룹으로 묶임
        // 세번째 and() : 이전의 그룹 조건(첫번째와 두번째의 or)과 세번째 조건을 AND로 연결
        // ※ 중요 섞어서 쓰면 1번째 랑 2번째가 or로 묶이고 그거랑 3번째를 and 하게 된다.
        builder.or(qItem.itemDetail.contains(searchDetail)) // itemDetail LIKE 검색
                .or(qItem.price.gt(priceCondition))          // price > 10003
                .and(qItem.itemSellStatus.eq(ItemSellStatus.SELL)); // itemSellStatus = "SELL"

        // Pageable 설정 (페이지 번호: 0 =1페이지를말하는 것, 페이지 크기: 5 =한페이지에 노출될 상품의 갯수)
        Pageable pageable = PageRequest.of(0, 5, Sort.by("price").descending());

        // When: QueryDSL 쿼리 실행
        // 위에서 만든 조회조건(booleanBuilder)와 페이지 설정(pageable)을 사용하여 쿼리 만들고 실행
        List<Item> items = queryFactory
                .selectFrom(qItem)  // qitem 엔티티 조회할 수 있는 쿼리(JPAQuery) 객체 생성
                .where(builder)     // 위에서 쿼리 객체가 만들어졌고 거기에 .(쩜)을 찍어서 메소드 체이닝 방식으로 조건을 덧붙임
                .orderBy(qItem.price.desc()) // 가격 기준 정렬, pageable에 있으므로 주석처리
                .offset(pageable.getOffset()) // 페이징 시작 위치
                .limit(pageable.getPageSize()) // 페이지 크기
                .fetch();

        // Then: 검증
        assertThat(items).isNotEmpty(); // 결과가 비어 있지 않은지 검증
        assertThat(items.size()).isLessThanOrEqualTo(5); // 결과가 5개 이하인지 검증

        items.forEach(item -> {
            log.info(item.toString()); // 결과 로그 출력
        });
    }




    /**
     * QueryDSL로 상품 가격, 판매상태, 페이징, 정렬 조회 테스트 #2
     * - 가격이 price > 10003 인 상품을 조회
     * - itemSellStatus = "SELL"인 상품만 조회
     * - pageable 설정 : 페이지 번호 0, 한 페이지 크기 5
     * - 정렬 : 가격 기준 내림차순
     * QuerydslPredicateExecutor 인터페이스를 상속받은 ItemRepository를 사용하여 테스트
     */
    @Test
    @DisplayName("QueryDSL 조건 조회 및 페이징 테스트")
    public void findItemsWithConditionsTest2() {
        // Given
        saveTestItems(); // 테스트용 데이터 저장

        // JPAQueryFactory : QueryDSL을 사용하기 위한 팩토리 클래스로 EntityManager를 주입받아 생성
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        // QueryDSL용 QItem 객체 생성
        QItem qItem = QItem.item;

        // 조건 생성
        // BooleanBuilder : QueryDSL에서 사용하는 조건을 생성하는 클래스
        // booleanBuilder.and(조건식) : AND 조건을 추가
        // booleanBuilder.or(조건식) : OR 조건을 추가
        BooleanBuilder builder = new BooleanBuilder();

        // 검색 조건 설정
        String searchDetail = "테스트 상품 상세 설명";
        int priceCondition = 10003;

        // booleanBuilder를 통해서 QueryDSL 조건 설정
        // builder.and(조건식) : 첫번째 AND 조건을 추가, 이 조건은 첫번째 조건과 그 다음에 오는 조건의 연결을 결정한다. 여기도 마찬가지로 조건이 한개면(두번째 조건이 없으면 무 의미하다! !! !!!)
        // 두번째 and() : 두번째 조건과 세번째 조건을 AND로 연결
        // 세번째 and() : 세번째 조건과 네번째 조건을 AND로 연결한다. 이때!!!! 4번째가 안오면 무의미하다.!!!! ! ! ! ! !
//        builder.and(qItem.itemDetail.contains(searchDetail)) // itemDetail LIKE 검색
//                .and(qItem.price.gt(priceCondition))          // price > 10003
//                .and(qItem.itemSellStatus.eq(ItemSellStatus.SELL)); // itemSellStatus = "SELL"

        // QueryDSL 조건 설정 or() 사용 (or and 섞어서 사용했을 경우!!)
        // 첫번째 or() : 첫번째 조건과 두번째 조건을 OR로 연결
        // 두번째 or() : 사용 안되며(위랑은 다르게 섞어서 써서 그런 듯) 첫번째 조건과 두번째 조건이 그룹으로 묶임
        // 세번째 and() : 이전의 그룹 조건(첫번째와 두번째의 or)과 세번째 조건을 AND로 연결
        // ※ 중요 섞어서 쓰면 1번째 랑 2번째가 or로 묶이고 그거랑 3번째를 and 하게 된다.(이 부분은 쌤도 예상 못한 듯? 다시 알아보고 알려주시기로 함)
        // 보경님 의영님 이거 찾으면 캡쳐해서 보내기 안보내면 공부안한거 바로걸림ㅋ
        builder.or(qItem.itemDetail.contains(searchDetail)) // itemDetail LIKE 검색
                .or(qItem.price.gt(priceCondition))          // price > 10003
                .and(qItem.itemSellStatus.eq(ItemSellStatus.SELL)); // itemSellStatus = "SELL"

        // Pageable 설정 (페이지 번호: 0 =1페이지를말하는 것, 페이지 크기: 5 =한페이지에 노출될 상품의 갯수)
        Pageable pageable = PageRequest.of(0, 5, Sort.by("price").descending());

        // When:
        // QuerydslPredicateExecutor 인터페이스의 findAll() 메서드를 사용하여 쿼리 실행
        // Page<Item> itemPageResult : 페이징 처리된 결과를 반환
        // findAll(조회조건, 페이징정보) : 조회조건과 페이징정보를 사용하여 쿼리를 실행하고 결과를 반환
        // itemPageResult : 1) 페이징 처리된 결과(알맹이)와, 2)페이징 정보를 가지고 있는 Page 객체
        Page<Item> itemPageResult = itemRepository.findAll(builder, pageable);

        // page 객체에서 content를 꺼내서 List로 변환
        List<Item> items = itemPageResult.getContent(); // 아이템 목록만 꺼냄

        // Then: 검증
        assertThat(items).isNotEmpty(); // 결과가 비어 있지 않은지 검증
        assertThat(items.size()).isLessThanOrEqualTo(5); // 결과가 5개 이하인지 검증

        // 결과 출력(itemPageResult)
        log.info("============= 페이징 처리 결과 =============");
        log.info("총 페이지 수: " + itemPageResult.getTotalPages());
        log.info("총 데이터 수: " + itemPageResult.getTotalElements());
        log.info("현재 페이지 번호: " + itemPageResult.getNumber());
        log.info("페이지 크기: " + itemPageResult.getSize());

        items.forEach(item -> {
            log.info(item.toString()); // 결과 로그 출력
        });
    }


    /*
      * 테스트 클래스에서 사용할 메소드
      * 상품 한 개 저장
     */
    private Item saveTestItem() {
        LocalDateTime now = LocalDateTime.now();

        Item item = Item.builder()
                .itemNm("테스트 상품")
                .price(10000)
                .stockNumber(50)
                .itemDetail("테스트 상품 상세 설명")
                .itemSellStatus(ItemSellStatus.SELL)
                //.regTime(now)
                //.updateTime(now)
                .build();
        return itemRepository.save(item);
    }

    /**
     * 상품 여러개 저장 메소드
     */
    private void saveTestItems() {
        IntStream.rangeClosed(1, 10)
                .forEach(i -> {
                    Item item = Item.builder()
                            .itemNm("테스트 상품 " + i)
                            .price(10000 + i)
                            .itemDetail("테스트 상품 상세 설명 " + i)
                            .itemSellStatus(ItemSellStatus.SELL)
                            .stockNumber(10+i)
                            //.regTime(LocalDateTime.now())
                            .build();

                    Item savedItem = itemRepository.save(item);
                });
    }
}
