package pposonggil.usedStuff.dto.Board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.domain.TransactionAddress;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class BoardDto {
    private Long boardId;
    private Long writerId;
    private String writerNickName;
    private Double ratingScore;
    private String title;
    private String content;
    private String startTimeString;
    private String endTimeString;
    private TransactionAddress address;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private Long price;
    private boolean isFreebie;

    public static BoardDto fromEntity(Board board) {
        return BoardDto.builder()
                .boardId(board.getId())
                .writerId(board.getWriter().getId())
                .writerNickName(board.getWriter().getName())
                .ratingScore(board.getWriter().getRatingScore())
                .title(board.getTitle())
                .content(board.getContent())
                .startTimeString(board.getStartTimeString())
                .endTimeString(board.getEndTimeString())
                .address(board.getAddress())
                .createdAt(board.getCreatedAt())
                .updateAt(board.getUpdateAt())
                .price(board.getPrice())
                .isFreebie(board.isFreebie())
                .build();
    }
}
