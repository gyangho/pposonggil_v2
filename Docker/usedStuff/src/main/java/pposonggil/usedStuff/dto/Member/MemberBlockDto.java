package pposonggil.usedStuff.dto.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.Block.BlockDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class MemberBlockDto {
    private Long memberId;
    private String name;
    private String nickName;
    private String phone;
    private Double ratingScore;
    private LocalDateTime createdAt;
    private boolean isActivated;
    private List<BlockDto> subjectBlockDtos;
    private List<BlockDto> objectBlockDtos;

    public static MemberBlockDto fromEntity(Member member){
        return MemberBlockDto.builder()
                .memberId(member.getId())
                .name(member.getName())
                .nickName(member.getName())
                .ratingScore(member.getRatingScore())
                .createdAt(member.getCreatedAt())
                .isActivated(member.isActivated())
                .subjectBlockDtos(member.getBlockSubjects().stream()
                        .map(BlockDto::fromEntity)
                        .collect(Collectors.toList()))
                .objectBlockDtos(member.getBlockObjects().stream()
                        .map(BlockDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
