package pposonggil.usedStuff.repository.board.custom;

import pposonggil.usedStuff.domain.Board;

import java.util.List;

public interface CustomBoardRepository {
    List<Board> findAllWithMember();
    List<Board> findBoardsWithMemberByWriterId(Long writeId);
}