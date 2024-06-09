package pposonggil.usedStuff.repository.Distance.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import pposonggil.usedStuff.domain.Distance;

import java.util.List;
import java.util.Optional;

import static pposonggil.usedStuff.domain.QDistance.distance;
import static pposonggil.usedStuff.domain.QTrade.trade;

public class CustomDistanceRepositoryImpl implements CustomDistanceRepository{
    private final JPAQueryFactory query;

    public CustomDistanceRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<Distance> findDistancesWithTrade(){
        return query
                .select(distance)
                .from(distance)
                .join(distance.distanceTrade, trade).fetchJoin()
                .limit(1000)
                .fetch();
    }

    @Override
    public Optional<Distance> findDistanceByTrade(Long tradeId){
        List<Distance> distances = findDistancesWithTrade();

        return distances.stream()
                .filter(distance -> distance.getDistanceTrade().getId().equals(tradeId))
                .findFirst();
    }
}
