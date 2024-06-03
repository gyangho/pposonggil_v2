package pposonggil.usedStuff.api.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.Member.MemberDto;
import pposonggil.usedStuff.service.Member.MemberService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

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
