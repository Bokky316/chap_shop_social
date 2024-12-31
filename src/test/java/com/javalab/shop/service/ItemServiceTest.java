package com.javalab.shop.service;

import com.javalab.shop.constant.ItemSellStatus;
import com.javalab.shop.dto.ItemFormDto;
import com.javalab.shop.entity.Item;
import com.javalab.shop.entity.ItemImg;
import com.javalab.shop.repository.ItemImgRepository;
import com.javalab.shop.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Commit;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
//@Commit
public class ItemServiceTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemImgRepository itemImgRepository;

    /**
     * 테스트용 MultipartFile 생성
     */
    List<MultipartFile> createMultipartFiles() throws Exception{

        List<MultipartFile> multipartFileList = new ArrayList<>();

        for(int i=0;i<5;i++){
            String path = "C:/shop/item";
            String imageName = "image" + i + ".jpg";
            MockMultipartFile multipartFile
                    = new MockMultipartFile(path, imageName,
                    "image/jpg", new byte[]{1,2,3,4});
            multipartFileList.add(multipartFile);
        }

        return multipartFileList;
    }

    @Test
    @DisplayName("상품 등록 테스트")
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void saveItemTest() throws Exception {
        // given, ItemFormDto 생성(빌더패턴)
        ItemFormDto itemFormDTO = ItemFormDto.builder()
                .itemNm("테스트상품")
                .itemSellStatus(ItemSellStatus.SELL)
                .itemDetail("테스트 상품 입니다.")
                .price(1000)
                .stockNumber(100)
                .build();
        // when
        // 상품 등록시 사용할 MultipartFile 리스트 생성
        List<MultipartFile> multipartFileList = createMultipartFiles();
        // 상품 영속화(저장)
        Long itemId = itemService.saveItem(itemFormDTO, multipartFileList);

        // then
        // 위에서 영속화 시킨 상품 아이템을 다시 조회
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        // 상품 조회
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        // 검증, 상품명이 같은지 확인
        assertEquals(itemFormDTO.getItemNm(), item.getItemNm());
    }
}
