package com.javalab.shop.config;


import com.javalab.shop.security.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정 파일
 * - 인증, 권한 설정
 * @Configuration :
 * - 이 클래스가 Spring의 설정 파일임을 명시, 여기에는 하나 이상의 @Bean이 있음.
 * - Spring 컨테이너가 이 클래스를 읽어들여 Bean으로 등록 *
 * @EnableWebSecurity :
 * - Spring Security 설정을 활성화하며 내부적으로 시큐리티 필터 체인을 생성,
 *   이를 통해서 애플리케이션이 요청을 처리할 때 필터 체인을 거쳐 (인증) 및 (인가)를-(권한검사) 수행하게 된다.
 * - 시큐리티 필터 체인은 여러 개의 필터로 구성되면 디스패처 서블릿 앞에 위치하게 된다.
 * - CSRF, 세션 관리, 로그인, 로그아웃, 권한, XSS방지 등을 처리하는 기능들이 활성화 된다.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Oauth2 소셜 로그인을 처리하는 서비스 객체 의존성 주입
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        /*
         * formLogin() : 폼 로그인 설정
         * loginPage() :
         *  - loginPage("/members/login") : 사용자가 만든 로그인 페이지를 요청하는 URL
         *    시큐리티가 인증이 필요하게 되면 /members/login 을 요청하게 된다. -> 컨트롤러에다가 로그인폼을 제공하는걸 추가해야함
         *  - 시큐리티에서 제공하는 기본 로그인 페이지가 아닌 사용자가 만든 로그인 페이지를 요청하는 URL
         *  - 사용자가 인증이 필요한 URL을 요청했을 때 해당 자원에 대한 권한이 없을 때 이동할 로그인 페이지
         *  - /members/login 요청을 받을 수 있는 컨트롤러와 뷰를 만들어야 함.
         *  - 로그인 페이지에서 로그인 시도할때 반드시 POST 방식으로 만들어야 한다. 비록 컨트롤러에는
         *    POST요청으로 처리하는 메소드가 없지만 시큐리티가 자동으로 처리해준다.
         * defaultSuccessUrl() : 로그인 성공 후 이동할 URL
         * failureUrl() : 로그인 실패 후 이동할 URL
         * usernameParameter() : 로그인 페이지에서 사용자명 입력 필드의 name 속성값(기본값: username)
         * passwordParameter() : 로그인 페이지에서 비밀번호 입력 필드의 name 속성값(기본값: password)
         * permitAll() :
         * -  로그인과 관련된 모든 URL에 대해 인증 없이 접근을 허용하여, 사용자가 인증되지 않은 상태에서도
         *    로그인 페이지나 실패 페이지에 접근할 수 있도록 만듭니다.(/members/login, /members/login/error)
         */
        http.formLogin(form -> form
                .loginPage("/members/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/members/login/error")
                .usernameParameter("email")
                .passwordParameter("password")
                .permitAll());

        /*
         * logout() : 스프링의 기본 로그아웃 관련 설정
         * Customizer.withDefaults() : 기본 로그아웃 설정을 사용
         * - /logout 을 기본 로그아웃 요청을 처리하는 URL로 하겠다.
         * - 로그아웃 성공 시 / 경로로 리디렉트
         * - 로그아웃 성공 시 세션 무효화
         * - 로그아웃 성공 시 쿠키 삭제 등의 업무가 자동으로 설정된다.
         */
        http.logout(Customizer.withDefaults());

        /*
         * 정적 자원 및 URL에 대한 접근 제어 설정(인가) 로드맵
         * authorizeRequests() : 애플리케이션의 접근 제어(Authorization) 정책을 정의
         * requestMatchers() : 요청에 대한 보안 검사를 설정
         * permitAll() : 모든 사용자에게 접근을 허용
         * hasRole() : 특정 권한을 가진 사용자만 접근을 허용
         * anyRequest() : 모든 요청에 대해 접근을 허용
         * authenticated() : 인증된 사용자만 접근을 허용
         * favicon.ico : 파비콘 요청은 인증 없이 접그 가능, 이 코드를 누락시키면 계속 서버에 요청을 보내서 서버에 부하를 줄 수 있다. 클라이언트 : 파비콘 내놔 -> 서버 -> 너 권한없어 -> 클라이언트: 파비콘 내놔 -> 무한루프
         */
        http .authorizeRequests(request -> request
                .requestMatchers("/images/**", "/static-images/**", "/css/**", "/favicon.ico", "/error", "/img/**").permitAll()// 여기 설정된 정적리소스 URL은 인증 없이 접근 가능, 에러를 안하면 무한 리디렉트함, 파비콘=아이콘임
                .requestMatchers("/", "/members/**").permitAll() // /, /member/** URL은 인증 없이 접근 가능
                .requestMatchers("/admin/**").hasRole("ADMIN") // /admin/** URL은 ADMIN 권한을 가진 사용자만 접근 가능
                .anyRequest().authenticated()); // 그 외의 URL은 인증된 사용자만 접근 가능

        // 인증 실패 시 처리할 핸들러를 설정
        // 권한이 없는 페이지에 접근 시 처리할 핸들러
        // 예외가 발생하면 예외를 전달해줘서 어떤 핸들러가 처리할지 지정해주는 방법
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()));

        // http.csrf(csrf -> csrf.disable()); // CSFR 보안 설정을 비활성화
        // 시큐리티를 통해서 최초로 테스트 할 때는 이 코드를 사용하고
        // 회원가입 폼이 정상적으로 오픈된 후에는 이 코드를 주석처리해야 한다.
        // 그렇지 않으면 memberForm.html에서 csrf 토큰을 받지 못해 403 에러가 발생한다.
        // http.csrf(csrf -> csrf.disable()); // CSFR 보안 설정을 비활성화

        /*
         * 소셜 로그인 설정
         *  - oauth2Login() 메소드 : 소셜(OAuth2) 로그인을 활성화하는 설정의 시작점.
         *  - 이 메서드를 호출함으로써, 애플리케이션은 OAuth2 공급자(Google, Kakao 등)를
         *    통해 사용자 인증을 수행할 수 있게 된다.
         *  - loginPage() : 사용자가 인증되지 않은 상태에서 보호된 리소스에 접근시 여기로 리디렉트
         *    loginPage()를 설정하지 않으면 스프링 시큐리티는 기본 로그인 페이지(/login)를 사용.
         *  - userInfoEndpoint() : OAuth2 공급자로부터 사용자 정보를 가져오는 엔드포인트를 구성
         *  - userService() : 사용자 정보를 가져오는 서비스를 구현한 객체를 지정
         * - customOAuth2UserService : OAuth2 공급자로부터 사용자 정보를 가져오는 엔드포인트를 구성하는 실제 서비스 클래스
         */
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/members/login")
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
        );

        // 지금까지 설정한 내용을 빌드하여 반환, 반환 객체는 SecurityFilterChain 객체
        return http.build();
    }

    /**
     * 비밀번호 암호화를 위한 PasswordEncoder 빈 등록
     * - BCryptPasswordEncoder : BCrypt 해시 함수를 사용하여 비밀번호를 암호화
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}