package pposonggil.usedStuff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Member;

import java.time.LocalDate;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class MemberDto {
    private Long memberId;
    private String name;
    private String nickName;
    private String phone;
    private Double ratingScore;
    private LocalDate createdAt;
    private boolean isActivated;

    public static MemberDto fromEntity(Member member){
        return MemberDto.builder()
                .memberId(member.getId())
                .name(member.getName())
                .nickName(member.getNickName())
                .phone(member.getPhone())
                .ratingScore(member.getRatingScore())
                .createdAt(member.getCreatedAt())
                .isActivated(member.isActivated())
                .build();
    }

    public static Member toEntity(MemberDto dto) {
        return Member.builder(dto.getNickName())
                .id(dto.getMemberId())
                .phone(dto.getPhone())
                .ratingScore(dto.getRatingScore())
                .createdAt(dto.getCreatedAt())
                .isActivated(dto.isActivated())
                .build();
    }
}
