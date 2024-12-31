package com.javalab.shop.repository;

import com.javalab.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/*
    * Member엔티티를 기준으로 CRUD가만들어지는데 Long 타입이다.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);
}
