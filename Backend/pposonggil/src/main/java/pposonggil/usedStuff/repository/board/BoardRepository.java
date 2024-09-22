package pposonggil.usedStuff.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.repository.board.custom.CustomBoardRepository;

public interface BoardRepository extends JpaRepository<Board, Long>, CustomBoardRepository {

}
