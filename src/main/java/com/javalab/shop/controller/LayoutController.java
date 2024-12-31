package com.javalab.shop.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 레이아웃 컨트롤러
 * - 레이아웃 페이지를 만들고 테스트하는 컨트롤러
 */
@Controller
@Log4j2
public class LayoutController {

    @GetMapping("/layout")
    public String layout() {

        log.info("여기는 레이아웃 페이지 컨트롤러입니다.");

        return "content/content01"; // 타임리프 페이지(컨텐트 페이지)
    }
}
