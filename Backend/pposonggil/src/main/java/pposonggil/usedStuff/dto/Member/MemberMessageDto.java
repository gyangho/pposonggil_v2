package pposonggil.usedStuff.dto.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.Message.MessageDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class MemberMessageDto {
    private Long memberId;
    private String name;
    private String nickName;
    private String phone;
    private Double ratingScore;
    private LocalDateTime createdAt;
    private boolean isActivated;
    private List<MessageDto> messageDtos;

    public static MemberMessageDto fromEntity(Member member){
        return MemberMessageDto.builder()
                .memberId(member.getId())
                .name(member.getName())
                .nickName(member.getName())
                .ratingScore(member.getRatingScore())
                .createdAt(member.getCreatedAt())
                .isActivated(member.isActivated())
                .messageDtos(member.getMessages().stream()
                        .map(MessageDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
