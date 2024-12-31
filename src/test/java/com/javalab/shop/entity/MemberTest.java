package com.javalab.shop.entity;

import com.javalab.shop.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@Transactional
public class MemberTest {

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager em;

    /**
     * Auditing 테스트
     * - 생성일, 수정일, 생성자, 수정자 테스트
     * @WithMockUser : 스프링 시큐리티에서 제공하는 어노테이션으로 특정 사용자가 로그인한 상태라고 가정하고 테스트 진행
     */
    @Test
    @DisplayName("Auditing 테스트")
    @WithMockUser(username = "gildong", roles = "USER")   // 특정 사용자가 로그인한 상태라고 가정하고 테스트 진행
    public void auditingTest(){
        Member newMember = new Member();    // 새로운 멤버 생성
        memberRepository.save(newMember);   // 멤버 저장(영속화), 이때 생성일, 수정일, 생성자, 수정자가 자동으로 들어감

        em.flush(); // 영속성 컨텍스트의 변경 내용을 데이터베이스에 반영
        em.clear(); // 영속성 컨텍스트 초기화

        Member member = memberRepository.findById(newMember.getId())
                .orElseThrow(EntityNotFoundException::new);

        System.out.println("register time : " + member.getRegTime());
        System.out.println("update time : " + member.getUpdateTime());
        System.out.println("register member : " + member.getCreatedBy());
        System.out.println("modify member : " + member.getModifiedBy());

    }

}
