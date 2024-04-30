package pposonggil.usedStuff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.TransactionAddress;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private Long price;
    private boolean isFreebie;

    public static BoardDto fromEntity(Board board) {
        return BoardDto.builder()
                .boardId(board.getId())
                .writerId(board.getWriter().getId())
                .writerNickName(board.getWriter().getNickName())
                .ratingScore(board.getWriter().getRatingScore())
                .title(board.getTitle())
                .content(board.getContent())
                .startTimeString(board.getStartTimeString())
                .endTimeString(board.getEndTimeString())
                .address(board.getAddress())
                .createdAt(board.getCreatedAt())
                .price(board.getPrice())
                .build();
    }

    public static Board toEntity(BoardDto dto, Member writer) {
        LocalDateTime startTime = LocalDateTime.parse(dto.getStartTimeString(), DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm"));
        LocalDateTime endTime = LocalDateTime.parse(dto.getEndTimeString(), DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm"));

        return Board.builder(writer, dto.getTitle(), startTime, endTime, dto.getAddress(), dto.getPrice())
                .id(dto.getBoardId())
                .content(dto.getContent())
                .createdAt(dto.getCreatedAt())
                .isFreebie(dto.isFreebie())
                .build();
    }
}
