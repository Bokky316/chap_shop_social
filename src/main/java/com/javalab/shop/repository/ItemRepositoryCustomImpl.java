package com.javalab.shop.repository;

import com.javalab.shop.constant.ItemSellStatus;
import com.javalab.shop.dto.ItemSearchDto;
import com.javalab.shop.dto.MainItemDto;
import com.javalab.shop.dto.QMainItemDto;
import com.javalab.shop.entity.Item;
import com.javalab.shop.entity.QItem;
import com.javalab.shop.entity.QItemImg;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ItemRepositoryCustom interface 구현하는 클래스
 * - querydsl을 사용하여 동적으로 쿼리를 생성하기 위한 메서드를 구현한다.
 * - BooleanExpression : querydsl의 조건을 표현하는 인터페이스, where 조건을 만듬.
 */
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    // 동적으로 쿼리를 생성하기 위해 JPAQueryFactory를 사용한다.
    private JPAQueryFactory queryFactory;

    // 생성자를 통해 EntityManager를 전달 받는다.
    public ItemRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 상품의 판매 상태와 검색 조건을 이용하여 상품 목록을 조회하는 메서드
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    // 상품 등록일 검색 조건을 이용하여 상품 목록을 조회하는 메서드
    private BooleanExpression regDtsAfter(String searchDateType){

        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        } else if(StringUtils.equals("1d", searchDateType)){
            dateTime = dateTime.minusDays(1);
        } else if(StringUtils.equals("1w", searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        } else if(StringUtils.equals("1m", searchDateType)){
            dateTime = dateTime.minusMonths(1);
        } else if(StringUtils.equals("6m", searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }

    // 상품명, 상품 등록자 검색 조건을 이용하여 상품 목록을 조회하는 메서드
    private BooleanExpression searchByLike(String searchBy, String searchQuery){

        if(StringUtils.equals("itemNm", searchBy)){
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("createdBy", searchBy)){
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }

        return null;
    }

    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }

    /**
     * 관리자 페이지에서 상품 목록을 조회하기 위한 메서드
     * 검색 조건을 기반으로 QueryDSL을 사용하여 동적으로 쿼리를 실행하고,
     * 조회된 상품 목록을 페이징 처리하여 반환
     * @param itemSearchDto
     * @param pageable
     * @return
     */
    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        // quertFactory : 동적으로 쿼리를 만들 수 있는 쿼리 객체를 생성
        // QuertyResults : querydsl의 페이징 처리를 위한 클래스로  fetchResults()메서드를 통해
        // 조회된 데이터와 전체 데이터 수를 가져온다.
        QueryResults<Item> results = queryFactory
                .selectFrom(QItem.item) // selectFrom() : 어떤 엔티티를 조회할지 지정(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()), // regDtsAfter() : 상품 등록일 검색 조건
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Item> content = results.getResults();  // 조회된 데이터 즉, content
        long total = results.getTotal();        // 전체 데이터 수 즉, total(위 조회 조건에 합당한 전체건수)

        // contest : 조회된 데이터
        // total : 전체 데이터 수
        // pageable : 페이지 정보
        return new PageImpl<>(content, pageable, total);
    }


    /**
     * 메인 페이지에 상품 목록을 조회하는 메서드
     * - QueryDSL을 사용하여 상품 데이터를 조회하고, 페이징 처리를 수행하는 로직이다.
     * - 상품 검색: ItemSearchDto에 포함된 검색 조건을 기반으로 데이터를 필터링합니다.
     * - 대표 이미지 필터링: 대표 이미지(repimgYn = "Y")만 조회합니다.
     * - 페이징 처리: Pageable 객체를 기반으로 결과 데이터를 페이징 처리합니다.
     * - DTO 매핑: 조회 결과를 MainItemDto 객체로 매핑하여 반환합니다.
     * @param itemSearchDto
     * @param pageable
     * @return
     */
    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        // 1. QueryDSL 객체 선언
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        // 2. 콘텐츠 조회
        // - 현재 페이지에 표시할 데이터를 QueryDSL로 조회한다.
        List<MainItemDto> content = queryFactory
                .select(
                        // QMainItemDto를 사용하여 조회 결과를 MainItemDto로 매핑한다.
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)  // itemImg 테이블을 기준으로 조회한다.
                .join(itemImg.item, item)   // itemImg 테이블과 item 테이블을 조인한다.
                .where(itemImg.repimgYn.eq("Y"))        // 대표 이미지만 조회한다.
                .where(itemNmLike(itemSearchDto.getSearchQuery()))  // 상품명 검색 조건을 적용한다.
                .orderBy(item.id.desc())    // 상품 번호를 기준으로 내림차순 정렬한다.
                .offset(pageable.getOffset())   // 페이지 시작 위치를 설정한다.
                .limit(pageable.getPageSize())  // 페이지 당 조회할 데이터 수를 설정한다.
                .fetch();   // 조회 결과를 반환한다.

        /*
         * 3. 전체 데이터 수를 조회한다.
         * Wildcard : QueryDSL에서 전체 데이터 개수를 세기 위한 객체.
         */
        long total = queryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .fetchOne()
                ;

        // 4. 결과 반환
        // - 조회된 콘텐츠와 전체 데이터 수를 사용해 PageImpl 객체 생성
        return new PageImpl<>(content, pageable, total);
    }

}