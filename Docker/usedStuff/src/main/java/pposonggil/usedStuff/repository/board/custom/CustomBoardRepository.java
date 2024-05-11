package pposonggil.usedStuff.repository.board.custom;

import pposonggil.usedStuff.domain.Board;

import java.util.List;

public interface CustomBoardRepository{
    public List<Board> findAllWithMember();
}
