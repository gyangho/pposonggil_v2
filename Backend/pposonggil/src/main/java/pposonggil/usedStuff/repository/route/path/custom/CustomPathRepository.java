package pposonggil.usedStuff.repository.route.path.custom;

import pposonggil.usedStuff.domain.Route.Path;

import java.util.List;

public interface CustomPathRepository  {
    List<Path> findAllWithMember();

    List<Path> findPathsWithByMemberByRequesterId(Long requesterId);
}
