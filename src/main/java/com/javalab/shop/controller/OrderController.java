package com.javalab.shop.controller;

import com.javalab.shop.dto.OrderDto;
import com.javalab.shop.dto.OrderHistDto;
import com.javalab.shop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 페이지
     * @ResponseBody : 요청의 결과를 뷰를 사용하지 않고 JSON 형태 HTTP 응답의 바디에 직접 쓰기 위해 사용
     * @RequestBody : html에서 전송된 값들이 JSON 형태로 전달되었을 때, 해당 값을 객체로 변환하기 위해 사용
     * @ResponseEntity : HTTP 응답의 상태 코드, 헤더, 바디에
     * @param orderDto : 주문 정보 바인딩
     * @param bindingResult : 유효성 검사 결과
     * @param principal : 시큐리티 Principal 객체로 로그인한 사용자 정보를 얻을 수 있다.
     *
     * @return
     */
    @PostMapping("/order")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto
            , BindingResult bindingResult, Principal principal) {
        // 주문 정보 유효성 검사
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            // 유효성 검사 실패 시 에러 메시지 반환, getFieldErrors() : 빌드 에러 목록 반환
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);  // 유효성 검사 실패 시 에러 메시지 반환
        }

        String email = principal.getName(); // 로그인한 사용자 이메일
        Long orderId;

        try {
            orderId = orderService.order(orderDto, email);  // 주문 서비스 호출
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    /**
     * 주문 내역 조회
     * @param page : 페이지 번호
     * @param principal : 시큐리티 Principal 객체로 로그인한 사용자 정보를 얻을 수 있다.
     * @param model : 뷰에 전달할 데이터
     * @return
     */
    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page, Principal principal, Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4); // 하드코딩하는건 좋은건 아님
        // Page 객체에는 주문 내역과 페이지 정보가 함께 담겨있다.
        Page<OrderHistDto> ordersHistDtoList = orderService.getOrderList(principal.getName(), pageable);

        model .addAttribute("orders", ordersHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "order/orderHist";
    }

    /**
     * 주문 취소
     * - 주문 취소 권한이 있는지 확인한다.
     * - 주문 취소 서비스 호출
     * - 주문 취소 성공 시 주문 번호 반환
     * - 주문 취소 실패 시 에러 메시지 반환
     * - 주문 취소 권한이 없을 경우 403 에러 반환
     *
     * @param orderId : 주문 번호
     * @param principal : 시큐리티 Principal 객체로 로그인한 사용자 정보를 얻을 수 있다.
     * @return
     */
    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId , Principal principal){

        if(!orderService.validateOrder(orderId, principal.getName())){
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        orderService.cancelOrder(orderId);
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }
}
