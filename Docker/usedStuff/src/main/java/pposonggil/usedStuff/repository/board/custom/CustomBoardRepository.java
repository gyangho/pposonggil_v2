package pposonggil.usedStuff.repository.board.custom;

import pposonggil.usedStuff.domain.Board;

import java.util.List;

public interface CustomBoardRepository {
    List<Board> findAllWithMemberImage();
    List<Board> findBoardsByMember(Long writeId);
}