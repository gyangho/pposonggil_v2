package pposonggil.usedStuff.service.Board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.dto.Board.BoardImagesDto;
import pposonggil.usedStuff.repository.board.BoardRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardImageService {
    private final BoardRepository boardRepository;

    /**
     * 전체 이미지 포함한 게시글 조회
     */
    public List<BoardImagesDto> findBoardsWithImages() {
        List<Board> boards = boardRepository.findAll();
        return boards.stream()
                .map(BoardImagesDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 이미지 포함한 게시글 상세 조회
     */
    public BoardImagesDto findOne(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(NoSuchElementException::new);
        return BoardImagesDto.fromEntity(board);
    }

    /**
     * 작성자 아이디로 이미지 포함한 게시글 조회
     */
    public List<BoardImagesDto> findBoardsWithImagesByWriterId(Long writerId) {
        List<Board> boards = boardRepository.findBoardsWithMemberByWriterId(writerId);

        return boards.stream()
                .map(BoardImagesDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 작성자 & 이미지 & 게시글 조회
     */
    public List<BoardImagesDto> findAllWithMemberImages() {
        List<Board> boards = boardRepository.findAllWithMember();
        return boards.stream()
                .map(BoardImagesDto::fromEntity)
                .collect(Collectors.toList());
    }
}
