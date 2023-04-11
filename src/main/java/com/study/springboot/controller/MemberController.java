package com.study.springboot.controller;

import com.study.springboot.dto.MemberJoinDto;
import com.study.springboot.dto.MemberLoginDto;
import com.study.springboot.entity.Member;
import com.study.springboot.entity.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MemberController {

 private final MemberRepository memberRepository; // 생성자 주입

    // url : localhost:8080/loginForm
    @GetMapping("/loginForm")
    public String loginForm(){
        return "loginForm"; // loginForm.html로 응답
    }

    @PostMapping("/loginAction")
    @ResponseBody
    public String loginAction(@Valid MemberLoginDto dto,
                              BindingResult bindingResult,
                              HttpServletRequest request,
                              Model model){
        if(bindingResult.hasErrors()){
            // DTO에 설정한 message 값을 가져온다.
            String detail = bindingResult.getFieldError().getDefaultMessage();
            // DTO에 유효성 체크를 걸어놓은 어노테이션명을 가져온다.
            String bindResultCode = bindingResult.getFieldError().getCode();
            System.out.println(detail + ":" + bindResultCode);
            return "<script>alert('" + detail + "'); history.back();</script>";
        }

        System.out.println(dto.getUser_id());
        System.out.println(dto.getUser_pw());

        // 로그인 액션 처리 : 실제는 DB에 쿼리를 던진다.
        List<Member> list = memberRepository.findByUserIdAndUserPw(dto.getUser_id(), dto.getUser_pw());


        HttpStatus status = HttpStatus.OK;
        if( list.isEmpty() ){
            status = HttpStatus.NOT_FOUND;
        }else {
            status = HttpStatus.OK;
        }
        if(status == HttpStatus.OK){

            Member entity = list.get(0);
            // 로그아웃시까지 로그인한 회원정보(Member테이블)을 가지고 있다.
            request.getSession().setAttribute("memberEntity",entity);

            // 세션 객체에 로그인 성공값 저장(로그아웃시까지)
            request.getSession().setAttribute("isLogin",true);
            request.getSession().setAttribute("user_id",dto.getUser_id());
            request.getSession().setAttribute("user_role",entity.getUser_role());

            return "<script>alert('로그인 성공!'); location.href='/';</script>";
        }else {
            return "<script>alert('로그인 실패!'); history.back();</script>";
        }
    }

    @GetMapping("/logoutAction")
    @ResponseBody
    public String logoutAction(HttpServletRequest request){
        // 세션 종료
        request.getSession().invalidate();  // 세션값을 모두 삭제시킴
        return "<script>alert('로그아웃 성공!'); location.href='/';</script>";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "joinForm"; // joinForm.html로 응답함
    }

    @PostMapping("/joinAction")
    @ResponseBody
    public String joinAction(@Valid MemberJoinDto dto, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            // DTO에 설정한 message 값을 가져온다.
            String detail = bindingResult.getFieldError().getDefaultMessage();
            // DTO에 유효성 체크를 걸어놓은 어노테이션명을 가져온다.
            String bindResultCode = bindingResult.getFieldError().getCode();
            System.out.println(detail + ":" + bindResultCode);
            return "<script>alert('" + detail + "'); history.back();</script>";
        }

        System.out.println(dto.getUser_id());
        System.out.println(dto.getUser_pw());

        // 회원가입 DB액션 수행
        // 기존에 같은 아이디의 회원이 있는지 중복 체크
        // select * from member where user_id ='';
        // insert into member user_id = '', user_pw='';
        // 지금은 무조건 성공시킴
        HttpStatus status = HttpStatus.OK;

        if( status == HttpStatus.OK){
            System.out.println("회원가입 성공");
            return "<script>alert('회원가입 성공!'); location.href='/loginForm';</script>";
        }else {
            return "<script>alert('회원가입 실패!'); history.back();</script>";
        }

    }
    @GetMapping("/admin")
    public String admin( Model model){
        long listCount = memberRepository.count();
        model.addAttribute("listCount", listCount);

        List<Member> list = memberRepository.findAll();
        model.addAttribute("list", list);

        return "admin"; // admin.html로 이동
    }

    @RequestMapping("/viewDTO")
    public String viewDTO(@RequestParam("id") Long id, Model model) throws Exception {

        Optional<Member> optional = memberRepository.findById(id);
        if(!optional.isPresent()) {
            throw new Exception("member id is wrong");
        }
        Member entity = optional.get();
        model.addAttribute("member", entity);

        return "modifyForm";
    }

    @RequestMapping("/modifyAction")
    @ResponseBody
    public String modifyAction(MemberJoinDto memberJoinDto){

        try {
            Member entity = memberJoinDto.toUpdateEntity();
            memberRepository.save(entity);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            return "<script>alert('회원정보 수정 실패!'); history.back();</script>";

        }
        return "<script>alert('회원정보 수정 성공!'); location.href='/viewDTO?id=" + memberJoinDto.getId() + "';</script>";
    }

    @RequestMapping("/deleteDTO")
    @ResponseBody
    public String deleteDTO(@RequestParam("id") Long id) throws Exception {

        Optional<Member> optional = memberRepository.findById(id);
        if(!optional.isPresent()) {
            throw new Exception("member id is wrong");
        }
        Member entity = optional.get();

        try{
            memberRepository.delete(entity);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            return "<script>alert('회원정보 삭제 실패!'); history.back();</script>";

        }

        return "<script>alert('회원정보 삭제 성공!'); location.href='/admin';</script>";
    }

}
