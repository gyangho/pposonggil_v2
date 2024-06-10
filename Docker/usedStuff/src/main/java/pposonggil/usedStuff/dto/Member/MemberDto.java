package pposonggil.usedStuff.dto.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.Role;

import java.time.LocalDateTime;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class MemberDto {
    private Long memberId;
    private String name;
    private Double ratingScore;
    private LocalDateTime createdAt;
    private boolean isActivated;
    private Set<Role> roles;



    public static MemberDto fromEntity(Member member){
        return MemberDto.builder()
                .memberId(member.getId())
                .name(member.getName())
                .ratingScore(member.getRatingScore())
                .createdAt(member.getCreatedAt())
                .isActivated(member.isActivated())
                .roles(member.getRoles())
                .build();
    }
}
