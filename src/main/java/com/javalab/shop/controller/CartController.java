package com.javalab.shop.controller;

import com.javalab.shop.dto.*;
import com.javalab.shop.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**************************************************************************************
 * 장바구니 컨트롤러
 *  - 장바구니 담기
 *  - 장바구니 목록
 *  - 장바구니 수정
 *  - 장바구니 삭제
 *  - 장바구니 주문
 **************************************************************************************/
@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
                                              BindingResult bindingResult, Principal principal){
        // 1. 오류 검증
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        // 2. 현재 로그인한 회원의 이메일 정보 가져옴
        String email = principal.getName();
        Long cartItemId;

        try {
            // 화면으로 부터 전달된 장바구니에 담을 상품정보와 현재 로그인한 회원의 이메일 정보를 이용하여
            // 장바구니에 상품을 담는 메소드 호출
            cartItemId = cartService.addCart(cartItemDto, email);
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    /**
     * 장바구니 페이지로 이동
     * @param principal
     * @param model
     * @return
     */
    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model){
        List<CartDetailDto> cartDetailList = cartService.getCartList(principal.getName());
        model.addAttribute("cartItems", cartDetailList);
        return "cart/cartList";
    }

    /**
     * 장바구니 상품 수량 수정
     * @param cartItemId
     * @param count
     * @param principal
     */
    @PatchMapping("/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                       @RequestParam("count") int count,
                                                       Principal principal){

        if(count <= 0){
            return new ResponseEntity<String>("최소 1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
        } else if(!cartService.validateCartItem(cartItemId, principal.getName())){
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItemCount(cartItemId, count);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    /**
     * 장바구니 상품 삭제
     * @param cartItemId
     * @param principal
     * @return
     */
    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId, Principal principal){

        if(!cartService.validateCartItem(cartItemId, principal.getName())){
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.deleteCartItem(cartItemId);

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    /**
     * 장바구니 상품 주문
     * - 장바구니에서 여러 상품을 선택하여 주문
     */
    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderRequestDto cartOrderRequestDto, Principal principal) {

        List<CartOrderItemDto> cartOrderItems = cartOrderRequestDto.getCartOrderItems();

        if (cartOrderItems == null || cartOrderItems.isEmpty()) {
            return new ResponseEntity<>("주문할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }

        for (CartOrderItemDto cartOrderItem : cartOrderItems) {
            if (!cartService.validateCartItem(cartOrderItem.getCartItemId(), principal.getName())) {
                return new ResponseEntity<>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }

        Long orderId = cartService.orderCartItem(cartOrderItems, principal.getName());
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

}