package com.javalab.shop.security.oauth;

import com.javalab.shop.entity.Member;
import com.javalab.shop.repository.MemberRepository;
import com.javalab.shop.security.dto.MemberSecurityDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 소셜 로그인을 통해서 인증을 진행하는 클래스
 * - 소셜 로그인 제공자로부터 사용자 정보를 가져옵니다.
 * - 사용자 정보를 사용하여 데이터베이스에서 회원 정보를 조회하거나, 없으면 새로운 회원을 생성합니다.
 * - 스프링 시큐리티의 인증 객체를 생성하고 반환합니다.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    /**
     * 소셜 로그인 인증 진행 메소드
     *  - 일반 인증에서는 loadUserByUsername 메소드가 진행.
     * 파라미터인 OAuth2UserRequest 에 포함된 정보
     *   1. Registration ID : 여러 소셜 로그인 업체 중에서 어떤 업체를 사용할지 정보
     *   2. Client ID & Client Secret, Redirect URI 정보등(application.properties 에 설정한 값)
     *   3. 이 모든 정보는 application.properties 에 설정 해놓을것.\
     *   로드유저를 통해서 오스투를 상속받은 애들에다가 정보를 보내주는 것?
     *   즉 우리는 로드유저메서드만 구현해주면 된다.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        /**
         * DefaultOAuth2UserService 클래스의 loadUser() 메소드 호출.
         *
         * 소셜 로그인 업체 식별번호, 소셜 로그인 Api에서 발급받은
         * 1)client-id 2)client-secret 3)Redirect-Url 정보를 담고 있는
         * userRequest 를 제공해주면  카카오 소셜 로그인을 진행해주고
         * 그 결과를 OAuth2User 객체에 담아서 보내줌.
         *
         * OAuth2User 객체에는 카카오에서 제공하는 사용자의 이메일, 이름 등의 정보 포함
         *
         * 정리하면 super.loadUser(userRequest) 호출로 카카오 소셜 로그인이
         * 진행되고 거기서 받아온 값이 OAuth2User 객체에 담겨온다. 우리는 그
         * 정보를 사용하여 데이터베이스에 소셜 로그인 관련 정보를 저장하거나
         * 업데이트하고, 시큐리티 객체를 생성하여 반환하면 된다.
         */
        OAuth2User oAuth2User = super.loadUser(userRequest);
        /**
         * oAuth2User 에서 정보를 Key-value 형태로 추출하여 map 형태로 보관.
         * 주로 이메일 또는 닉네임 정도. 소셜 업체에 따라서 다름.
         */
        Map<String, Object> attributes = oAuth2User.getAttributes();
        // 소셜 로그인 제공자 정보, 카카오, 구글, 네이버 등
        String provider = userRequest.getClientRegistration().getRegistrationId();
        // 소셜 로그인 제공자로 부터 받은 사용자 정보에서 이메일 추출
        String email = extractEmail(attributes, provider);
        // 소셜 로그인 제공자로 부터 받은 사용자 프로필 이름 추출
        String name = extractName(attributes, provider);

        // 사용자 저장 또는 업데이트(최초소셜로그인->저장, 이미소셜로그인한적있음->업데이트) (아래 참고 세이브업뎃멤버메서드)
        Member member = saveOrUpdateMember(email, name, provider);

        // 시큐리티 객체 생성
        // 멤버 객체랑 attributes 를 전달 (아래참고)
        return createSecurityDto(member, attributes);  // 이게 제일 중요함 왜??(아래)
    }

    /**
     * 시큐리티 객체 생성
     * - 스프링 시큐리티 인증 객체 생성
     * 영속화시킨 데이터를 가지고와서 MemberSecurityDto에다가 보내줌?
     * 소셜로그인으로 가입한사람은 비밀번호가 없음 null임 디비가서 보삼 <- 쌤이 더 찾아보고 나중에 추가설명해주기로함
     */
    private MemberSecurityDto createSecurityDto(Member member, Map<String, Object> attributes) {
        return new MemberSecurityDto(
                member.getEmail(),
                member.getPassword() == null ? "N/A" : member.getPassword(), // 소셜 로그인 사용자는 패스워드 없음
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + member.getRole().toString())), // 권한 만들어서 저짝으로 반환
                member.isSocial(), // 소셜로그인 트루 아님 풜스
                member.getProvider(), // 카카오냐 구글이냐
                attributes // 카카오에서 받은 여러 정보들
        );
    }

    private String extractEmail(Map<String, Object> attributes, String provider) {
        if ("kakao".equals(provider)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            return (String) kakaoAccount.get("email");
        }
        // Other providers...
        return (String) attributes.get("email");
    }

    /**
     * 카카오 닉네임 추출
     */
    private String extractName(Map<String, Object> attributes, String provider) {
        if ("kakao".equals(provider)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            String name = (String) profile.get("nickname");
            // 기본 사용자 이름 설정
            return (name != null && !name.isEmpty()) ? name : "기본사용자";
        }
        // Other providers can be handled here if needed
        return "Unknown User"; // 기본값
    }


    /**
     * 사용자 저장 또는 업데이트
     * - 소셜 로그인 사용자가 최초이면 저장, 이미 소셜 로그인 사용자이면 업데이트
     */
    private Member saveOrUpdateMember(String email, String name, String provider) {

        // 1. 소셜 로그인 업체에서 받은 이메일로 우리 데이터베이스에서 조회
        Member member = memberRepository.findByEmail(email);
        // 2. 조회된 사용자가 없으면 최소 소셜 로그인 사용자이므로 생성
        if (member == null) {   // 최초로 소셜 로그인하는 사용자
            member = Member.createSocialMember(email, provider);    // 소셜 로그인 사용자 생성
            member.setName(name);   // 카카오에서 가져온 사용자 이름 설정
            member = memberRepository.save(member);     // 저장(영속화) -> 최초로 소셜 로그인하는 사용자 데이터베이스에 저장
        } else {    // 이미 소셜 로그인으로 데이터베이스에 관련 정보가 있는 사용자
            // 사용자가 소셜 로그인 카카오, 구글에서 이름 또는 이메일과 같은 정보를 변경했을 수 있기 때문에 업데이트
            member.setProvider(provider);               // 소셜 로그인 제공자 업데이트
            member.setName(name);                       // 이름 업데이트
            member = memberRepository.save(member);     // 업데이트(영속화), 트랜잭션 종료 시점에 업데이트 쿼리가 실햄됨 왜? 저 위에서 기존멤버는 이미 디비 영속영역1차캐시에 올라가져있음 즉 디비에 이미 있는사람은 최신 정보로 업데이트가 되는 것 그래서 비록 save 명령일 지라도 업데이트 쿼리가 나가는 것임 (예: 카카오로그인 -> 구글로그인으로 변경시 업데이트쿼리 나감)
        }                                               // 기존 로그인 했던 사용자가 로그인 누르면 loadUser호출되서 바로 로그인 되는것임
        return member;  // 사용자 반환
    }
}
