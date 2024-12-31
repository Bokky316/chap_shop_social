package com.javalab.shop.service;

import com.javalab.shop.constant.ItemSellStatus;
import com.javalab.shop.dto.CartItemDto;
import com.javalab.shop.entity.CartItem;
import com.javalab.shop.entity.Item;
import com.javalab.shop.entity.Member;
import com.javalab.shop.repository.CartItemRepository;
import com.javalab.shop.repository.ItemRepository;
import com.javalab.shop.repository.MemberRepository;
import jakarta.persistence.Column;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class CartServiceTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CartService cartService;

    @Autowired
    CartItemRepository cartItemRepository;

    /**
     * 장바구니에 담을 상품정보 저장
     * @return
     */
    public Item saveItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    /**
     * 장바구니에 담을 회원정보 저장
     * @return
     */
    public Member saveMember(){
        Member member = new Member();
        member.setEmail("test3@test.com");
        return memberRepository.save(member);
    }

    @Test
    @DisplayName("장바구니 담기 테스트")
    @Commit
    public void addCart(){
        Item item = saveItem();
        Member member = saveMember();   // 실제 디비에 없는 이메일을 넣어야 함

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCount(5);
        cartItemDto.setItemId(item.getId());

        Long cartItemId = cartService.addCart(cartItemDto, member.getEmail());
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        assertEquals(item.getId(), cartItem.getItem().getId());
        assertEquals(cartItemDto.getCount(), cartItem.getCount());
    }

}