package com.javalab.shop.controller;

import com.javalab.shop.dto.MemberFormDto;
import com.javalab.shop.entity.Member;
import com.javalab.shop.service.MemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * 테스트 메소드에서 공용으로 사용하는 메소드
     * - 회원가입 테스트시 사용
     */
    public Member createMember(String email, String password) {
        MemberFormDto dto = MemberFormDto.builder()
                .email(email)
                .name("홍길동")
                .address("서울시 마포구 합정동")
                .password(password)
                .build();

        Member member = Member.createMember(dto, passwordEncoder); // Member 엔티티 생성
        Member savedMember = memberService.saveMember(member);  // Member 저장
        return savedMember;
    }

    @Test
    @DisplayName("로그인 테스트(성공)")
    public void liginSuccessTest() throws Exception {
        // Given
        String email = "abc@naver.com"; // db에 없는 이메일
        String password = "1234"; // 비밀번호
        Member member = createMember(email, password); // 저장할 Member 엔티티 생성

        // When & then
        // formLogin() : 메소드를 사용하여 로그인 요청을 보내고 로그인 성공 여부를 확인
        mockMvc.perform(formLogin()
                        .userParameter("email")
                        .loginProcessingUrl("/members/login")
                        .user(email)        // 로그인 시도할 이메일, username() 대신에 user() 메소드 사용
                        .password(password)
                )
                .andExpect(SecurityMockMvcResultMatchers.authenticated()); // 로그인 인증이 성공했을거라고 기대(단정)
    }

    /**
     * 로그인 실패 테스트
     * @throws Exception
     */
    @Test
    @DisplayName("로그인 테스트(실패)")
    public void liginFailTest() throws Exception {
        // Given
        String email = "abc@naver.com"; // db에 없는 이메일
        String password = "1234"; // 비밀번호
        Member member = createMember(email, password); // 저장할 Member 엔티티 생성

        // When & then
        // formLogin() : 메소드를 사용하여 로그인 요청을 보내고 로그인 성공 여부를 확인
        mockMvc.perform(formLogin()
                        .userParameter("email")
                        .loginProcessingUrl("/members/login")
                        .user(email)        // 로그인 시도할 이메일, username() 대신에 user() 메소드 사용
                        .password("123")
                )
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated()); // 로그인 인증이 실패했을거라고 기대(단정)
    }


}
