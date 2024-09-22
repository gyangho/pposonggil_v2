package pposonggil.usedStuff.repository.Distance.custom;

import pposonggil.usedStuff.domain.Distance;

import java.util.List;
import java.util.Optional;

public interface CustomDistanceRepository {
    List<Distance> findDistancesWithTrade();
    Optional<Distance> findDistanceByTrade(Long tradeId);
}
