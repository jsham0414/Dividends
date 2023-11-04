package com.example.Dividends.controller;

import com.example.Dividends.model.Auth.SignIn;
import com.example.Dividends.model.Auth.SignUp;
import com.example.Dividends.persist.entity.MemberEntity;
import com.example.Dividends.security.TokenProvider;
import com.example.Dividends.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 인증을 위한 컨트롤러
 */

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignUp request) {
        return ResponseEntity.ok(memberService.register(request));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody SignIn request) {
        MemberEntity member = memberService.authenticate(request);
        var token = tokenProvider.generateToken(member.getUsername(), member.getRoles());
        log.info("user login -> " + request.getUsername());
        return ResponseEntity.ok(token);
    }

}
