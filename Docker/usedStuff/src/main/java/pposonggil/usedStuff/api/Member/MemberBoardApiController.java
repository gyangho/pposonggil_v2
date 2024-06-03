package pposonggil.usedStuff.api.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.Member.MemberBoardDto;
import pposonggil.usedStuff.service.Member.MemberBoardService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberBoardApiController {
    private final MemberBoardService memberBoardService;

    /**
     * 게시글 정보 포함 전체 회원 조회
     * @return 게시글 회원 Dto 리스트
     */
    @GetMapping("/api/members/with-board")
    public List<MemberBoardDto> membersWithBoard() {
        return memberBoardService.findMembersWithBoards();
    }

    /**
     * 게시글 정보 포함 특정 회원 상세 정보 조회
     * @param memberId : 조회하려는 회원 아이디
     * @return 게시글 회원 Dto
     */
    @GetMapping("/api/member//with-board/by-member/{memberId}")
    public MemberBoardDto getMemberWithBoard(@PathVariable Long memberId) {
        return memberBoardService.findOneWithBoard(memberId);
    }
}
