package com.javalab.shop.repository;

import com.javalab.shop.dto.ItemSearchDto;
import com.javalab.shop.dto.MainItemDto;
import com.javalab.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * ItemRepositoryCustom 인터페이스
 * - queryDsl을 사용한 동적 쿼리를 위한 ItemRepositoryCustom 인터페이스
 */
public interface ItemRepositoryCustom {

    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

}
