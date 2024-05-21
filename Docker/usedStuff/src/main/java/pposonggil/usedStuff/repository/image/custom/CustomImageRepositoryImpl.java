package pposonggil.usedStuff.repository.image.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.Image;

import java.util.List;

import static pposonggil.usedStuff.domain.QBoard.board;
import static pposonggil.usedStuff.domain.QImage.image;

@Repository
public class CustomImageRepositoryImpl implements CustomImageRepository{
    private final JPAQueryFactory query;

    public CustomImageRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<Image> findAllWithBoard() {
        return query
                .select(image)
                .from(image)
                .join(image.imageBoard, board).fetchJoin()
                .limit(1000)
                .fetch();
    }
}
