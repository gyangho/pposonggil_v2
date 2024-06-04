package pposonggil.usedStuff.dto.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.Trade.TradeDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class MemberTradeDto {
    private Long memberId;
    private String name;
    private String nickName;
    private String phone;
    private Double ratingScore;
    private LocalDateTime createdAt;
    private boolean isActivated;
    private List<TradeDto> tradeSubjectDtos;
    private List<TradeDto> tradeObjectDtos;

    public static MemberTradeDto fromEntity(Member member){
        return MemberTradeDto.builder()
                .memberId(member.getId())
                .name(member.getName())
                .nickName(member.getName())
                .ratingScore(member.getRatingScore())
                .createdAt(member.getCreatedAt())
                .isActivated(member.isActivated())
                .tradeSubjectDtos(member.getTradeSubjects().stream()
                        .map(TradeDto::fromEntity)
                        .collect(Collectors.toList()))
                .tradeObjectDtos(member.getTradeObjects().stream()
                        .map(TradeDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
