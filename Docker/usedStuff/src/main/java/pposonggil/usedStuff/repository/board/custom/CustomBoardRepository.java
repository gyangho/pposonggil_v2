package pposonggil.usedStuff.repository.board.custom;

import pposonggil.usedStuff.domain.Board;

import java.util.List;

public interface CustomBoardRepository {
    List<Board> findAllWithMemberImages();
    List<Board> findBoardsWithMemberImagesByMember(Long writeId);
}