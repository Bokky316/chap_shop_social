package com.javalab.shop.service;

import com.javalab.shop.dto.ItemFormDto;
import com.javalab.shop.dto.ItemImgDto;
import com.javalab.shop.dto.ItemSearchDto;
import com.javalab.shop.dto.MainItemDto;
import com.javalab.shop.entity.Item;
import com.javalab.shop.entity.ItemImg;
import jakarta.persistence.EntityNotFoundException;
import com.javalab.shop.repository.ItemImgRepository;
import com.javalab.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    // 의존성 주입
    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    // 상품 등록
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        // 1. 상품 등록, 저장(영속화)
        // 1.1. ItemFormDto의 crateItem() 메소드를 통해 Item 객체 생성(Dto -> Entity)
        Item item = itemFormDto.createItem();
        // 1.2. ItemRepository의 save() 메소드를 통해 Item 객체 저장
        // save(item) : JPA의 ENtityManager가 persist(item) 메소드 호출해서 해당 엔티티를 영속화
        // item 엔티티가 데이터베이스에 저장되고 기본키를 발급받아서 그 기본키로 영속성 컨텍스트에 저장됨
        itemRepository.save(item);

        // 2. 이미지 등록
        for(int i=0;i<itemImgFileList.size(); i++){
            // 2.1. ItemImg 객체 생성
            ItemImg itemImg = new ItemImg();
            // 2.2. Item 객체와 연관관계 설정
            itemImg.setItem(item);
            // 2.3. 대표 이미지 여부 설정
            if( i == 0)
                itemImg.setRepimgYn("Y");
            else
                itemImg.setRepimgYn("N");
            // 2.4. ItemImgService의 saveItemImg() 메소드를 통해 ItemImg 객체 저장
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }
        return item.getId();// 3. 등록된 Item ID 반환

    }

    /**
     * 상품 상세 조회
     * - 한 개의 상품과 여러 개의 상품 이미지 정보를 조회하는 메서드
     * - 상품 ID를 전달받아 상품 상세 정보를 조회하는 메서드 상품 이미지 정보를 조회한다.
     * - 상품 정보와 상품 이미지 정보를 조합하여 상품 상세 정보를 반환한다.
     * - 트랜잭션 내에서 INSERT, UPDATE, DELETE 쿼리가 발생하지 않도록 보장.
     *   혹시 다른 레이어에서 여기서 영속화한 엔티티를 수정하거나 삭제하는 경우가 있을 수 있기 때문에
     *   readOnly = true 옵션을 사용하여 트랜잭션 내에서 SELECT 쿼리만 실행하도록 설정한다.
     * - readOnly = true 옵션을 사용하여 트랜잭션 내에서 SELECT 쿼리만 실행하도록 설정한다.
     * @param itemId
     */
    @Transactional(readOnly = true)
    public ItemFormDto getItemDetail(Long itemId) {

        // 1. 상품 번호로 해당 상품의 이미지들을 조회한다.
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        // 2. 조회한 이미지들을 ItemImgDto로 변환하기 위해 List에 담는다.
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for(ItemImg itemImg : itemImgList){
            ItemImgDto itemImgDto = ItemImgDto.entityToDto(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        // 3. 상품 번호로 해당 상품을 조회한다. 이렇게 조회하면 영속성 컨텍스트에 해당 엔티티가 영속화된다.
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        // 조회한 3.상품 정보와 2.이미지 정보를 조합하여 ItemFormDto로 변환한다.
        // 변환하는 이유는 화면에 출력하기 위함이다.
        ItemFormDto itemFormDto = ItemFormDto.of(item);

        // 4. ItemFormDto에 이미지 정보를 설정한다.
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        // 상품정보와 상품의 이미지 정보들에 대한 조회가 완료

        return itemFormDto;
    }

    /**
     * 상품 수정
     * @param itemFormDto
     * @param itemImgFileList
     * @return
     * @throws Exception
     */
    public long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 1. 수정할 상품 조회, 영속화 - 상품 정보를 수정하기 위해 조회
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);

        // 2. 영속화 되어 있는 상품의 정보를 수정한다. - 변경감지(dirty checking) - 자동감지 후 자동 저장됨.
        item.updateItem(itemFormDto);

        // 3. 화면에서 전달된 상품 이미지의 키(기본키)를 arrayLIst로 받아온다.
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        // 4. 화면에서 전달된 상품 이미지 파일을 업데이트한다.
        for(int i=0; i<itemImgFileList.size(); i++){
            // 4.1 상품 이미지 파일을 업데이트 한다. (상품 이미지 id, 상품 이미지 파일)
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }
        return item.getId();
    }

    /**
     * 상품 목록 조회
     * @param itemSearchDto : 복잡한 검색 조건을 담은 DTO
     * @param pageable : 페이징 처리를 위한 Pageable 객체
     * @return
     */
    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }
}
