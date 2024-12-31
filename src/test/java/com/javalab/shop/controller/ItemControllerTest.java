package com.javalab.shop.controller;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Log4j2
public class ItemControllerTest {
    @Autowired
    MockMvc mockMvc;

    /**
     * 상품 등록 페이지 권한 테스트
     * - 상품 등록 페이지 접근 권한이 있는지 테스트
     * - 가상의 사용자를 만들고 그 사용자의 권한으로 상품 등록 페이지를 요청할 수 있는지 테스트
     * - @WithMockUser : 가상의 사용자를 만들어서 테스트에 사용하는 어노테이션, username과 roles를 지정할 수 있음,
     *   중요한 건 roles에 ADMIN을 지정해주면 ADMIN 권한을 가진 사용자로 테스트를 진행할 수 있음.
     *   andDo() : 테스트 수행중에 추가적인 작업을 할 수 있는 메소드, log::info 를  사용하면 로그를 출력할 수 있음.
     *   log::info는  log.info()와 같은 의미
     */
    @Test
    @DisplayName("상품 등록 페이지 권한 테스트")
    @WithMockUser(username = "test@test.com", roles = "ADMIN")
    public void itemFormTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/item/new"))
                .andDo(log::info)
                .andExpect(status().isOk()); //.andExpect() : 결과를 검증하는 메서드, status().isOk()는 HTTP 상태 코드가 200인지 검증
    }

    /**
     * 상품 등록 페이지 권한 테스트 - 실패
     * @throws Exception
     */
    @Test
    @DisplayName("상품 등록 페이지 권한 테스트")
    @WithMockUser(username = "test@test.com", roles = "USER")
    public void itemFormTest2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/item/new"))
                .andDo(log::info)
                .andExpect(status().isForbidden()); //status().isForbidden()는 HTTP 상태 코드가 403인지 검증
    }
}
