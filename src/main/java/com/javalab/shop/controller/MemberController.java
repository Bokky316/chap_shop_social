package com.javalab.shop.controller;

import com.javalab.shop.dto.MemberFormDto;
import com.javalab.shop.entity.Member;
import com.javalab.shop.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 회원 관련 컨트롤러
 * - 회원 관련 기능을 제공하는 컨트롤러
 */
@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
@Log4j2 // 로그를 사용하기위한 어노테이션, log.info() 사용가능
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암복호화하는 역할

    @GetMapping("/new")
    public String memberForm(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "members/memberForm";
    }

    /**
     * 회원가입 처리
     * - 회원가입 처리를 하는 컨트롤러
     * - 회원가입 폼에서 전달되는 데이터가 정상인지 검증하는 로직이 필요함.
     * - valid() : MemberFormDto에 정의된 검증 로직을 수행함
     * - BindingResult : 검증 결과를 담고 있는 객체, 오류 발생시 오류 내용을 담고 있음.
     * - 회원가입 폼에서 전달되는 데이터가 정상이 아닌 경우, 다시 회원가입 폼으로 이동해야 함.
     */
    @PostMapping("/new")
    public String memberCreate(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model) {

        // 1. 만약에 오류가 있다면 회원가입 폼으로 이동
        if(bindingResult.hasErrors()){
            return "members/memberForm";
        }
        try {
            // 2. 회원가입 처리
            // 2.1 MemberFormDto를 Member Entity로 변환
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            // 2.2 회원가입 처리메소드 호출
            memberService.saveMember(member);
        } catch (IllegalStateException e) {
            // 3. 회원가입 중복 오류 발생시
            // - 중복 오류 메시지를 로그에 기록
            // - 회원가입 폼으로 이동
            log.info("MemberController 회원가입시 중복 오류 : " + e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "members/memberForm";
        }
        return "redirect:/";    // 회원가입 성공시 메인 페이지로 이동
    }

    /**
     * 로그인 화면 오픈 메소드
     * @return
     */
    @GetMapping("/login")
    public String loginMember(){
        log.info("로그인 화면 오픈 메소드");
        return "members/memberLoginForm";
    }

    /**
     * 로그인 실패시 오류 메시지를 전달하는 메소드
     * @param model
     * @return
     */
    @GetMapping("/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호 확인해주세요");
        return "members/memberLoginForm";
    }

    /**
     * 로그아웃 성공시 메인페이지로 이동
     */
    @GetMapping("/logout")
    public String performLogout(HttpServletRequest request, HttpServletResponse response) {
        // 1. 현재 로그인한 사용자 정보를 시큐리티 컨텍스트 홀더에서 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 2. 로그인한 사용자 정보가 있다면 로그아웃 처리
        if (authentication != null) {
            // 2.1 로그아웃 처리
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        // 3. 메인페이지로 이동
        return "redirect:/";
    }

    /**
     * 파비콘 요청 무시
     */
    @GetMapping("/favicon.ico")
    @ResponseBody
    void disableFavicon() {
        // 아무 작업도 하지 않음
    }

}
