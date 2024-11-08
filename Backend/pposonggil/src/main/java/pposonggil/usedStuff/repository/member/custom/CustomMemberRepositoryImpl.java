package pposonggil.usedStuff.repository.member.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class CustomMemberRepositoryImpl implements  CustomMemberRepository{
    private final JPAQueryFactory query;

    public CustomMemberRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }
}
