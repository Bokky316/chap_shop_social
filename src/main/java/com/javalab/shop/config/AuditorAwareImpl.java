package com.javalab.shop.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * JPA Auditing을 위한 AuditorAware 구현체
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * 현재 로그인한 사용자를 가져옴
     * @return
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        // 스프링 시큐리티 컨텍스트에서 인증 객체를 가져옴, 인증 객체에는 사용자의 아이디,권한 등이 들어있음
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = "";
        if(authentication != null){
            // 현재 로그인한 사용자의 아이디를 가져옴, 이걸 통해 누가 생성했는지, 수정했는지 알 수 있음. 즉 등록자, 수정자로 넣어줌
            userId = authentication.getName(); // 사용자의 아이디, 즉 이메일을 가져옴
        }
        return Optional.of(userId);
    }
}
