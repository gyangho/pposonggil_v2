package pposonggil.usedStuff.repository.route.subpath.custom;

import pposonggil.usedStuff.domain.Route.SubPath;

import java.util.List;

public interface CustomSubPathRepository {
    List<SubPath> findSubPathsWithPath();

    List<SubPath> findSubPathsByPathId(Long pathId);
}
