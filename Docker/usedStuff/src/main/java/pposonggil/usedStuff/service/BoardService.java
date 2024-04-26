package pposonggil.usedStuff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.TransactionAddress;
import pposonggil.usedStuff.repository.board.BoardRepository;
import pposonggil.usedStuff.repository.member.MemberRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    /**
     * 전체 게시글 조회
     */
    public List<Board> findBoards() {
        return boardRepository.findAll();
    }

    /**
     * 게시글 상세 조회
     */
    public Board findOne(Long boardId) {
        return boardRepository.findOne(boardId);
    }

    /**
     * 작성자 아이디로 게시글 조회
     */
    public List<Board> findBoardsByWriterId(Long writerId) {
        return boardRepository.findBoardsByWriterId(writerId);
    }

    /**
     * 게시글 & 작성자 조회
     */
    public List<Board> findAllWithMember() {
        return boardRepository.findAllWithMember();
    }

    /**
     * 게시글 작성
     */
    @Transactional
    public Long createBoard(Long writerId, String title, String content, LocalDateTime startTime,
                            LocalDateTime  endTime, TransactionAddress address, Long price, boolean isFreebie) {
        Member writer = memberRepository.findOne(writerId);

        Board board = Board.buildBoard(writer, title, content, startTime, endTime, address, price, isFreebie);
        board.setWriter(writer);

        boardRepository.save(board);

        return board.getId();
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public void updateBoard(Long boardId, String title, String content, LocalDateTime startTime,
                            LocalDateTime endTime, TransactionAddress address, Long price, boolean isFreebie) {
        Board board = boardRepository.findOne(boardId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
        String formattedStartTime = startTime.format(formatter);
        String formattedEndTime = endTime.format(formatter);

        if(!board.getTitle().equals(title))
            board.changeTitle(title);
        if(!board.getContent().equals(content))
            board.changeContent(content);
        if(!board.getStartTimeString().equals(formattedStartTime)){
            board.changeStartTimeString(formattedStartTime);
            board.changeStartTime(startTime);
        }
        if(!board.getEndTimeString().equals(formattedEndTime)) {
            board.changeEndTimeString(formattedEndTime);
            board.changeEndTime(endTime);
        }
        if(!board.getAddress().equals(address))
            board.changeAddress(address);
        if(!board.getPrice().equals(price))
            board.changePrice(price);
        if(board.isFreebie() != isFreebie)
            board.changeIsFreebie(isFreebie);

        boardRepository.save(board);
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = boardRepository.findOne(boardId);
        if (board == null) {
            throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
        }

        boardRepository.delete(board);
    }
}
