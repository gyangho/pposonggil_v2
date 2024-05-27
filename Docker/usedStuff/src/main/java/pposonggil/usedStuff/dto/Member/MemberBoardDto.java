package pposonggil.usedStuff.dto.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.Board.BoardDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class MemberBoardDto {
    private Long memberId;
    private String name;
    private String nickName;
    private String phone;
    private Double ratingScore;
    private LocalDateTime createdAt;
    private boolean isActivated;
    private List<BoardDto> boardDtos;

    public static MemberBoardDto fromEntity(Member member){
        return MemberBoardDto.builder()
                .memberId(member.getId())
                .name(member.getName())
                .nickName(member.getNickName())
                .phone(member.getPhone())
                .ratingScore(member.getRatingScore())
                .createdAt(member.getCreatedAt())
                .isActivated(member.isActivated())
                .boardDtos(member.getBoards().stream()
                        .map(BoardDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
