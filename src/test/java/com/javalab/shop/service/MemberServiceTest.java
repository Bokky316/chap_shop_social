package com.javalab.shop.service;


import com.javalab.shop.dto.MemberFormDto;
import com.javalab.shop.entity.Member;
import com.javalab.shop.service.MemberService;
import jakarta.persistence.Column;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberService memberService;

    /**
     * 테스트 메소드가 공통으로 사용할 회원 엔티티 생성 메소드
     */
    public Member createMember() {

        MemberFormDto dto = MemberFormDto.builder()
                .email("test@test.com")
                .name("홍길동")
                .address("서울시 마포구 합정동")
                .password("1234")
                .build();

        Member member = Member.createMember(dto, passwordEncoder);
        return member;
    }

    /**
     * 회원가입 테스트
     */
    @Test
    @DisplayName("회원가입테스트")
//    @Commit // 테스트 완료 후 롤백하지 않고 커밋
    void saveMemberTest() {
        Member member = createMember();
        System.out.println(member);
        Member member1 = memberService.saveMember(member);
        System.out.println(member);
    }

    /**
     * 중복 회원가입 테스트
     * - 중복된 이메일로 회원가입 시도 시 IllegalStateException 발생
     *  - "이미 가입된 회원입니다." 메시지가 예외 메시지로 발생하는지 확인
     * - 첫번째에세 성공해도 두번째에서 실패하면 첫번째 작업도 롤백되어야 한다.
     */
    @Test
    @DisplayName("중복 회원가입테스트")
//    @Commit
    void saveDuplicateMemberTest() {
        // Given : 이미 가입된 회원이 존재하는 상황
        Member member1 = createMember();
        Member member2 = createMember();

        // When : 첫번째 회원가입 시도(성공)
        memberService.saveMember(member1);

        // When : 두번째 회원가입 시도(실패 - 예외)
        Throwable e = assertThrows(IllegalStateException.class, () -> memberService.saveMember(member2));

        // Then : 예외 메시지가 "이미 존재하는 회원입니다."인지 확인
        assertEquals("이미 존재하는 회원입니다.", e.getMessage());

    }
}
