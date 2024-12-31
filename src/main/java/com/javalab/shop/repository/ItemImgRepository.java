package com.javalab.shop.repository;

import com.javalab.shop.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 상품 이미지 엔티티에 대한 CRUD Repository 인터페이스
 */
public interface ItemImgRepository extends JpaRepository<ItemImg, Long>  {

    /**
     * 상품 이미지 조회
     * - 상품 이미지를 상품 ID로 조회한다.
     * - 상품 이미지는 ID 오름차순으로 정렬한다.
     * - 상품 이미지가 여러개일 수 있으므로 List로 반환한다.
     * - 상품 이미지가 없을 경우 빈 List를 반환한다.
     * @param itemId
     */
    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);

    /**
     * 대표 이미지 조회
     * - 상품 ID와 대표 이미지 여부로 조회한다.
     * - 대표 이미지가 있으면 ItemImg를 반환한다.
     * - 대표 이미지가 없을 경우 null을 반환한다.
     * @param itemId
     * @param repimgYn
     */
    ItemImg findByItemIdAndRepimgYn(Long itemId, String repimgYn);
}
