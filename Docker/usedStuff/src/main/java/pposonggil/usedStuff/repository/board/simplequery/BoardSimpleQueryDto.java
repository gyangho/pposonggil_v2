package pposonggil.usedStuff.repository.board.simplequery;

import lombok.Data;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.domain.TransactionAddress;

import java.time.LocalDate;

@Data
public class BoardSimpleQueryDto {
    private Long boardId;
    private Long transactionInformationId;
    private Long chatRoomId;
    private String writerNickName;
    private Double ratingScore;
    private String title;
    private String content;
    private LocalDate startTime;
    private LocalDate endTime;
    private TransactionAddress address;
    private LocalDate createdAt;
    private Long price;

    public BoardSimpleQueryDto(Board board) {
        boardId = board.getId();
        transactionInformationId = board.getTransactionInformation().getId();
        chatRoomId = board.getChatRoom().getId();
        writerNickName = board.getWriter().getNickName();
        ratingScore = board.getWriter().getRatingScore();
        title = board.getTitle();
        content = board.getContent();
        startTime = board.getTransactionInformation().getStartTime();
        endTime = board.getTransactionInformation().getEndTime();
        address = board.getTransactionInformation().getAddress();
        createdAt = board.getCreatedAt();
    }
}
