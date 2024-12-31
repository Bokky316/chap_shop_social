package com.javalab.shop.service;

import com.javalab.shop.entity.Member;
import com.javalab.shop.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 회원 서비스
 * - 회원과 관련된 비즈니스 로직을 처리하는 서비스 클래스
 * - 회원가입, 회원조회, 회원 정보 수정, 회원 탈퇴 등의 기능을 제공한다.
 * - UserDetailsService 인터페이스를 구현하여 회원의 정보를 조회하는 기능을 제공한다.
 * - loadUserByUsername() 메서드를 구현하여 회원의 정보를 조회한다.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService /* implements UserDetailsService*/ {
    private final MemberRepository memberRepository;
    /*
        * 회원가입
        * - 회원가입 폼 DTO를 전달받아 회원 엔티티를 생성하고 저장하는 메서드
        * - 회원 데이터 저장 전에 중복된 이메일이 있는지 검사한다.
        * - 작업 두 건 이상을 하나의 트랜잭션으로 처리하기 위해 클래스에 @Transactional annotation을 사용한다.
     */
    public Member saveMember(Member member) {
        // 1. 중복 회원 검증
        validateDuplicateMember(member);
        // 2. 회원 저장
        return memberRepository.save(member);
    }

    /*
        * 회원 조회
        * - 회원이 존재할 경우 예외 발생 시킴
     */
    private void validateDuplicateMember(Member member) {
        // 리포지토리 레이어에 만든 메소드를 통해서 사용자의 존재유무 확인
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember != null) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원의 정보를 조회하는 메서드
     * - 회원의 이메일을 전달받아 회원 정보를 조회하는 메서드
     * - 반환타입이 UserDetails 타입이므로, 조회된 회원 정보를 UserDetails 타입으로 변환하여 반환한다.
     * - UserDetails 인터페이스를 구현한 스프링이 제공하는 User 클래스를 사용하면 된다.
     * loadUserByUsername 메서드는 아이디로 받은거를 사용자 정보를 요청한데다가 사용자를 넘겨주는데 UserDetails얘를 구현한 애들 만들어서 반환시키던지 얘 타입으로 반환 함
     * 만드는거 ? User 얘는 UserDetails얘를 상속받오록 되어있음 아무튼 User 얘로 유저 만듬
     * User는 가보면 username password authorities 3개 있다 (크리티컬만보면) 일단 이 3가지만 세팅해주면 됨
     * Set<GrantedAuthority> authorities 여기서 GrantedAuthority 는 권한을 담는 그릇  set으로 감싸져있다? 1개가 아니라는 뜻 근데 우리는 1개만 사용함
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    /*
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. 회원 조회 멤버는 디비에서 조회해온거
        Member member = memberRepository.findByEmail(username);

        // 2. 회원이 존재하지 않는 경우 예외 발생 그러면 쨰 호출
        if (member == null) {
            throw new UsernameNotFoundException(username);
        }

        // 3. 있으면 ? 담아주는 작업해여함 회원 정보를 UserDetails 타입으로 변환하여 반환, User 클래스를 사용
        // User 객체를 생성해서 부모 타입인 UserDetails로 반환
        // username : 아이디 (외국에선 이게 아이디임)
        // 부모타입으로 형변환 해서 나감
        UserDetails user = User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();

        return user;
    }
    */
}
