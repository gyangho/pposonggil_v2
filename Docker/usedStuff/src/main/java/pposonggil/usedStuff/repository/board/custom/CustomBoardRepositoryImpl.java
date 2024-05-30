package pposonggil.usedStuff.repository.board.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.Board;

import java.util.List;
import java.util.stream.Collectors;

import static pposonggil.usedStuff.domain.QBoard.board;
import static pposonggil.usedStuff.domain.QMember.member;

@Repository
public class CustomBoardRepositoryImpl implements CustomBoardRepository {
    private final JPAQueryFactory query;

    public CustomBoardRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<Board> findAllWithMember() {
        return query
                .select(board)
                .from(board)
                .join(board.writer, member).fetchJoin()
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Board> findBoardsWithMemberByWriterId(Long writeId) {
        List<Board> boards = findAllWithMember();
        return boards.stream()
                .filter(board -> board.getWriter().getId().equals(writeId))
                .collect(Collectors.toList());
    }
}

