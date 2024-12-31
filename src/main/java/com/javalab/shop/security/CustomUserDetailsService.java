package com.javalab.shop.security;

import com.javalab.shop.entity.Member;
import com.javalab.shop.repository.MemberRepository;
import com.javalab.shop.security.dto.MemberSecurityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 사용자 정보를 가져오는 클래스
 * - UserDetailsService : 사용자 정보를 가져오기 위한 인터페이스
 * - loadUserByUsername() : 인증을 위해서 사용자 정보를 가져오는 메소드
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 사용자 정보를 가져옴(영속영역에 저장)
        Member member = memberRepository.findByEmail(username);
        // 2. 사용자 정보가 없을 경우 예외 처리(여기 넘어오면 사용자 정보가 없단얘기져)
        if (member == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        // 3. 소셜 로그인 사용자는 일반 로그인을 할 수 없다. 이제 두 부류가 생긴거임 . 회원가입을 하세요 / true = 일반로그인 할수없음
        if (member.isSocial()) {
            throw new UsernameNotFoundException("소셜 로그인 사용자는 일반 로그인을 할수 없습니다. 회원가입을 하세요.");
        }
        // 아까 주석한건 유저객체를 만듬 이제는 멤버 시큐리티 디티오가 유저를 상속받아서 그 일을 함 그래서 이젠 유절 안만들고 멤버시큐리티디티오에 샛팅
        // 소셜 false =왜? 일반사용자니까
        // 멤버 시큐리티 디티오는 일반(User) , 소셜로그인 (OAuth2User) 두개 있음
        // 4. 사용자 정보를 반환, 일반 사용자의 경우 Social = false, provider = null, attributes = null(이건 소셜로그인용 일반사용자라서 여기선 null)
        // Collections 타입쓰는 이유 MemberSecurityDto에 그렇게 되어있으니까 저렇게 만들어서 저짝에다 보내주는거 그럼 저기가서 일반사용자가 만들어짐
        return new MemberSecurityDto(
                member.getEmail(),
                member.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + member.getRole().toString())),
                false,
                null,
                null
        );
    }
}
