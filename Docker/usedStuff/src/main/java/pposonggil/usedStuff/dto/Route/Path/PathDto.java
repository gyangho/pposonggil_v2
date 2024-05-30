package pposonggil.usedStuff.dto.Route.Path;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Route.Path;
import pposonggil.usedStuff.domain.Route.SubPath;
import pposonggil.usedStuff.dto.Route.PointInformation.PointInformationDto;
import pposonggil.usedStuff.dto.Route.SubPath.SubPathDto;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class PathDto {
    private Long pathId;
    private Long routeRequesterId;
    private Long totalTime;
    private Long price;
    private Long totalDistance;
    private Long totalWalkDistance;
    private Long totalWalkTime;
    private Long busTransitCount;
    private Long subwayTransitCount;
    private Long totalTransitCount;
    private Long busStationCount;
    private Long subwayStationCount;
    private PointInformationDto startDto;
    private PointInformationDto endDto;
    private List<SubPathDto> subPathDtos;

    public static PathDto fromEntity(Path path) {
        return PathDto.builder()
                .pathId(path.getId())
                .routeRequesterId(path.getRouteRequester().getId())
                .totalTime(path.getTotalTime())
                .price(path.getPrice())
                .totalDistance(path.getTotalDistance())
                .totalWalkDistance(path.getTotalWalkDistance())
                .totalWalkTime(path.getTotalWalkTime())
                .busTransitCount(path.getBusTransitCount())
                .subwayTransitCount(path.getSubwayTransitCount())
                .totalTransitCount(path.getTotalTransitCount())
                .busStationCount(path.getBusStationCount())
                .subwayStationCount(path.getSubwayStationCount())
                .startDto(PointInformationDto.fromEntity(path.getStart()))
                .endDto(PointInformationDto.fromEntity(path.getEnd()))
                .subPathDtos(path.getSubPaths().stream()
                        .map(SubPathDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }

    public Path toEntity() {
        Path path = Path.builder(this.startDto.toEntity(), this.endDto.toEntity())
                .totalTime(this.totalTime)
                .price(this.price)
                .totalDistance(this.totalDistance)
                .totalWalkDistance(this.totalWalkDistance)
                .totalWalkTime(this.totalWalkTime)
                .busTransitCount(this.busTransitCount)
                .subwayTransitCount(this.subwayTransitCount)
                .totalTransitCount(this.totalTransitCount)
                .busStationCount(this.busStationCount)
                .subwayStationCount(this.subwayStationCount)
                .build();

        List<SubPath> subPaths = this.subPathDtos.stream()
                .map(subPath -> subPath.toEntity(path))
                .collect(Collectors.toList());

        path.setSubPaths(subPaths);

        return path;
    }

    public static PathDto fromJsonNode(JsonNode node, PointInformationDto start, PointInformationDto end) {
        JsonNode info = node.get("info");

        return PathDto.builder()
                .totalTime(info.path("totalTime").asLong(0L))
                .price(info.path("payment").asLong(0L))
                .totalDistance(info.path("totalDistance").asLong(0L))
                .totalWalkDistance(info.path("totalWalk").asLong(0L))
                .totalWalkTime(info.path("totalWalkTime").asLong(0L))
                .busTransitCount(info.path("busTransitCount").asLong(0L))
                .subwayTransitCount(info.path("subwayTransitCount").asLong(0L))
                .totalTransitCount(info.path("busTransitCount").asLong(0L)
                        + info.path("subwayTransitCount").asLong(0L))
                .busStationCount(info.path("busStationCount").asLong(0L))
                .subwayStationCount(info.path("subwayStationCount").asLong(0L))
                .startDto(start)
                .endDto(end)
                .subPathDtos(Optional.ofNullable(node.get("subPath"))
                        .map(JsonNode::elements)
                        .map(elements -> StreamSupport.stream(Spliterators.spliteratorUnknownSize(elements, Spliterator.ORDERED), false))
                        .orElseGet(Stream::empty)
                        .map(SubPathDto::fromJsonNode)
                        .collect(Collectors.toList()))
                .build();
    }
}