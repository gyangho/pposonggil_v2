package pposonggil.usedStuff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.domain.TransactionAddress;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class BoardImagesDto {
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
    private List<ImageDto> images;

    public static BoardImagesDto fromEntity(Board board) {
        return BoardImagesDto.builder()
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
                .updateAt(board.getUpdateAt())
                .price(board.getPrice())
                .isFreebie(board.isFreebie())
                .images(board.getImages().stream()
                        .map(ImageDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }

}
