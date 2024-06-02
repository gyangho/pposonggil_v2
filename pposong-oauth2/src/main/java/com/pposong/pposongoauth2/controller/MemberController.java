package com.pposong.pposongoauth2.controller;

import com.pposong.pposongoauth2.Service.MemberService;
import com.pposong.pposongoauth2.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping
    public Member createMember(@RequestBody Member member) {
        return memberService.createMember(member.getName(), member.getEmail(), member.getProvider());
    }

    @GetMapping("/{name}")
    public Optional<Member> getMember(@PathVariable String name) {
        return memberService.findMemberByName(name);
    }
}