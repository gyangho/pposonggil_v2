package pposonggil.usedStuff.repository.block.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.Block;
import pposonggil.usedStuff.domain.QMember;

import java.util.List;
import java.util.Optional;

import static pposonggil.usedStuff.domain.QBlock.block;

@Repository
public class CustomBlockRepositoryImpl implements CustomBlockRepository {
    private final JPAQueryFactory query;
    public CustomBlockRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }
    QMember sMember = new QMember("sMember");
    QMember oMember = new QMember("oMember");


    @Override
    public List<Block> findAllWithMember() {
        return query
                .select(block)
                .from(block)
                .join(block.blockSubject, sMember).fetchJoin()
                .join(block.blockObject, oMember).fetchJoin()
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Block> findBlocksBySubjectId(Long subjectId) {
        return query
                .select(block)
                .from(block)
                .join(block.blockSubject, sMember).fetchJoin()
                .join(block.blockObject, oMember).fetchJoin()
                .where(block.blockSubject.id.eq(subjectId))
                .limit(1000)
                .fetch();
    }

    @Override
    public List<Block> findBlocksByObjectId(Long objectId) {
        return query
                .select(block)
                .from(block)
                .join(block.blockSubject, sMember).fetchJoin()
                .join(block.blockObject, oMember).fetchJoin()
                .where(block.blockObject.id.eq(objectId))
                .limit(1000)
                .fetch();
    }
    @Override
    public Optional<Block> findByBlockSubjectAndBlockObject(Long subjectId, Long objectId){
        return Optional.ofNullable(query
                .select(block)
                .from(block)
                .join(block.blockSubject, sMember).fetchJoin()
                .join(block.blockObject, oMember).fetchJoin()
                .where(block.blockSubject.id.eq(subjectId)
                        .and(block.blockObject.id.eq(objectId)))
                .limit(1000)
                .fetchOne()
        );
    }

}
