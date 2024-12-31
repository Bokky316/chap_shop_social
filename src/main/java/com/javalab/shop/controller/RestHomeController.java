package com.javalab.shop.controller;

import com.javalab.shop.dto.BoardDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestHomeController {

    @GetMapping("/test")
    public /* String */ BoardDto test() {
        logMessage("test() started"); // 시작 로그
        String response = processRequest(); // 다음 메소드 호출
        logMessage("test() ended"); // 종료 로그

        // @Builder 적용된 BoardDto 객체 생성해서 반환
        BoardDto boardDto = BoardDto.builder()
                .boardNo(1)
                .title("제목")
                .content("내용")
                .memberId("작성자")
                .hitNo(0)
                .replyGroup(0)
                .replyOrder(0)
                .replyIndent(0)
                .build();

        //return response;
        return boardDto;
    }

    private String processRequest() {
        logMessage("processRequest() started");
        String result = generateResponse(); // 또 다른 메소드 호출
        logMessage("processRequest() ended");
        return result;
    }

    private String generateResponse() {
        logMessage("generateResponse() started");
        String message = "Hello, Spring Boot!"; // 응답 메시지 생성
        logMessage("generateResponse() ended");
        return message;
    }

    private void logMessage(String message) {
        System.out.println(message); // 로그 출력
    }
}
