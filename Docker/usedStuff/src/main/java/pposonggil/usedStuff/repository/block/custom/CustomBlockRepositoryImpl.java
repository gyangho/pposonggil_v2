package pposonggil.usedStuff.repository.block.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import pposonggil.usedStuff.domain.Block;
import pposonggil.usedStuff.domain.QBlock;
import pposonggil.usedStuff.domain.QMember;

import java.util.List;

@Repository
public class CustomBlockRepositoryImpl implements CustomBlockRepository{
    private final JPAQueryFactory query;
    QBlock block = QBlock.block;
    QMember subjectMember = new QMember("subjectMember");
    QMember objectMember = new QMember("objectMember");

    public CustomBlockRepositoryImpl(EntityManager em){
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<Block> findAllWithMember() {
        return query
                .select(block)
                .from(block)
                .join(block.blockSubject, subjectMember).fetchJoin()
                .join(block.blockObject, objectMember).fetchJoin()
                .limit(1000)
                .fetch();
    }


}
