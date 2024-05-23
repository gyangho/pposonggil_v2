package pposonggil.usedStuff.repository.board.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.Board;

import java.util.List;
import java.util.stream.Collectors;

import static pposonggil.usedStuff.domain.QBoard.board;
import static pposonggil.usedStuff.domain.QImage.image;
import static pposonggil.usedStuff.domain.QMember.member;

@Repository
public class CustomBoardRepositoryImpl implements CustomBoardRepository {
    private final JPAQueryFactory query;

    public CustomBoardRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<Board> findAllWithMemberImages() {
        return query
                .select(board)
                .from(board)
                .join(board.writer, member).fetchJoin()
                .leftJoin(board.images, image).fetchJoin()
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Board> findBoardsWithMemberImagesByMember(Long writeId) {
        List<Board> boards = findAllWithMemberImages();
        return boards.stream()
                .filter(board -> board.getWriter().getId().equals(writeId))
                .collect(Collectors.toList());
    }
}

