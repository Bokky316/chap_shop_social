package com.javalab.shop.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * MemberSecurityDto 클래스
 * - Spring Security의 인증 객체로 사용되며, 사용자 정보를 담는 DTO(Data Transfer Object) 역할
 * - 일반 로그인 사용자와 소셜 로그인 사용자 모두 처리 가능
 * - Spring Security의 User 클래스를 상속받아 기본 인증 정보(username, password, 권한)를 처리합니다.
 * - 소셜 로그인 사용자를 지원하기 위해 OAuth2User 인터페이스를 구현하여 소셜 사용자 정보(attributes)와 이름(getName())을 반환합니다
 * - Spring Security의 User 클래스와 OAuth2User 인터페이스를 상속받아 추가적인 기능을 확장
 * - 여기에는 User 클래스의 속성들인 username, password, authorities 외에 소셜 로그인 사용자 정보를 저장하는 속성들이 추가됨
 * - authorities : 사용자의 권한 정보로, GrantedAuthority 인터페이스를 구현한 객체의 컬렉션입니다.
 *   예: ROLE_ADMIN, ROLE_USER
 *   Spring Security에서 접근 제어를 위해 사용됩니다.
 */
@Getter
@Setter
@ToString
public class MemberSecurityDto extends User implements OAuth2User {

    /**
     * email: 사용자의 이메일 주소, Spring Security의 User 객체의 username으로 사용됨
     */
    private String email;
    /**
     * social: 소셜 로그인 여부를 나타내는 필드
     * - true: 소셜 로그인 사용자
     * - false: 일반 로그인 사용자
     */
    private boolean social;
    /**
     * provider: 소셜 로그인 제공자 정보 (예: "kakao", "google")
     */
    private String provider;
    /**
     * attributes: 소셜 로그인 사용자 정보
     * - OAuth2User 인터페이스를 구현하면서 필수로 포함되는 사용자 속성 정보
     * - 소셜 로그인 제공자로부터 받은 사용자 정보를 key-value 형태로 저장
     */
    private Map<String, Object> attributes; // 소셜 사용자 정보

    /**
     * MemberSecurityDto 생성자
     * - Spring Security의 User 객체를 생성하고, 추가 필드를 초기화
     *
     * @param username    사용자명 (여기서는 이메일로 사용)
     * @param password    비밀번호
     * @param authorities 사용자 권한 정보 (GrantedAuthority 리스트)
     * @param social      소셜 로그인 여부
     * @param provider    소셜 로그인 제공자 정보
     * @param attributes  소셜 로그인 사용자 속성 정보
     */
    public MemberSecurityDto(String username,
                             String password,
                             Collection<? extends GrantedAuthority> authorities,
                             boolean social,
                             String provider,
                             Map<String, Object> attributes) {
        // 부모 클래스 User의 생성자 호출
        super(username, password, authorities);
        this.email = username;
        this.social = social;
        this.provider = provider;
        this.attributes = attributes;
    }

    /**
     * getAttributes: OAuth2User 인터페이스의 메서드 구현
     * - 소셜 로그인 사용자 속성 정보를 반환
     *
     * @return 사용자 속성 정보 Map
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    /**
     * getName: OAuth2User 인터페이스의 메서드 구현
     * - 사용자의 고유 이름 반환 (여기서는 이메일로 사용)
     *
     * @return 사용자 이름 (이메일)
     */
    @Override
    public String getName() {
        return email;
    }
}
