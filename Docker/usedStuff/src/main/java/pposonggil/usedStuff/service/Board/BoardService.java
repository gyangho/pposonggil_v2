package pposonggil.usedStuff.service.Board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.dto.Board.BoardDto;
import pposonggil.usedStuff.repository.board.BoardRepository;
import pposonggil.usedStuff.repository.member.MemberRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    /**
     * 전체 게시글 조회
     */
    public List<BoardDto> findBoards() {
        List<Board> boards = boardRepository.findAll();
        return boards.stream()
                .map(BoardDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 게시글 상세 조회
     */
    public BoardDto findOne(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(NoSuchElementException::new);
        return BoardDto.fromEntity(board);
    }

    /**
     * 작성자 아이디로 게시글 조회
     */
    public List<BoardDto> findImageBoardsByWriterId(Long writerId) {
        List<Board> boards = boardRepository.findBoardsWithMemberByWriterId(writerId);

        return boards.stream()
                .map(BoardDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 작성자  & 게시글 조회
     */
    public List<BoardDto> findAllWithMember() {
        List<Board> boards = boardRepository.findAllWithMember();
        return boards.stream()
                .map(BoardDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 게시글 작성
     */
    @Transactional
    public Long createBoard(BoardDto boardDto) {
        Member writer = memberRepository.findById(boardDto.getWriterId())
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + boardDto.getWriterId()));

        Board board = Board.buildBoard(writer, boardDto.getTitle(), boardDto.getContent(), boardDto.getStartTimeString(),
                boardDto.getEndTimeString(), boardDto.getAddress(), boardDto.getPrice(), boardDto.isFreebie());

        board.setWriter(writer);
        boardRepository.save(board);

        return board.getId();
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public void updateBoard(BoardDto boardDto) {
        Board board = boardRepository.findById(boardDto.getBoardId())
                .orElseThrow(NoSuchElementException::new);

        if (!board.getTitle().equals(boardDto.getTitle()))
            board.changeTitle(boardDto.getTitle());
        if (!board.getContent().equals(boardDto.getContent()))
            board.changeContent(boardDto.getContent());
        if(!board.getStartTimeString().equals(boardDto.getStartTimeString()))
            board.changeStartTimeString(boardDto.getStartTimeString());
        if(!board.getEndTimeString().equals(boardDto.getEndTimeString()))
            board.changeEndTimeString(boardDto.getEndTimeString());
        if (!board.getAddress().equals(boardDto.getAddress()))
            board.changeAddress(boardDto.getAddress());
        if (!board.getPrice().equals(boardDto.getPrice()))
            board.changePrice(boardDto.getPrice());
        if (board.isFreebie() != boardDto.isFreebie())
            board.changeIsFreebie(boardDto.isFreebie());

        boardRepository.save(board);
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(NoSuchElementException::new);
        boardRepository.delete(board);
    }
}