package pposonggil.usedStuff.dto.Route.Path;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Route.LatXLngY;
import pposonggil.usedStuff.domain.Route.Path;
import pposonggil.usedStuff.domain.Route.PointInformation;
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
    private Double totalRain;
    private Long busTransitCount;
    private Long subwayTransitCount;
    private Long totalTransitCount;
    private Long busStationCount;
    private Long subwayStationCount;
    private PointInformationDto startDto;
    private PointInformationDto endDto;
    private PointInformationDto midDto;
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
                .totalRain(path.getTotalRain())
                .busTransitCount(path.getBusTransitCount())
                .subwayTransitCount(path.getSubwayTransitCount())
                .totalTransitCount(path.getTotalTransitCount())
                .busStationCount(path.getBusStationCount())
                .subwayStationCount(path.getSubwayStationCount())
                .startDto(getPointXYDto(path.getStart()))
                .endDto(getPointXYDto(path.getEnd()))
                .midDto(getPointMidDto(path.getStart(), path.getEnd()))
                .subPathDtos(path.getSubPaths().stream()
                        .map(SubPathDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }

    public Path toEntity() {
        Path path = Path.builder(getPointXYDto(this.startDto.toEntity()).toEntity(),
                        getPointXYDto(this.endDto.toEntity()).toEntity())
                .totalTime(this.totalTime)
                .price(this.price)
                .totalDistance(this.totalDistance)
                .totalWalkDistance(this.totalWalkDistance)
                .totalWalkTime(this.totalWalkTime)
                .totalRain(this.totalRain)
                .busTransitCount(this.busTransitCount)
                .subwayTransitCount(this.subwayTransitCount)
                .totalTransitCount(this.totalTransitCount)
                .busStationCount(this.busStationCount)
                .subwayStationCount(this.subwayStationCount)
                .mid(midDto.toEntity())
                .build();

        List<SubPath> subPaths = this.subPathDtos.stream()
                .map(subPath -> subPath.toEntity(path))
                .collect(Collectors.toList());

        path.setSubPaths(subPaths);

        return path;
    }

    public static PathDto fromJsonNode(JsonNode node, PointInformationDto startDto, PointInformationDto endDto) {
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
                .startDto(getPointXYDto(startDto.toEntity()))
                .endDto(getPointXYDto(endDto.toEntity()))
                .midDto(getPointMidDto(startDto.toEntity(), endDto.toEntity()))
                .subPathDtos(Optional.ofNullable(node.get("subPath"))
                        .map(JsonNode::elements)
                        .map(elements -> StreamSupport.stream(Spliterators.spliteratorUnknownSize(elements, Spliterator.ORDERED), false))
                        .orElseGet(Stream::empty)
                        .map(SubPathDto::fromJsonNode)
                        .collect(Collectors.toList()))
                .build();
    }

    private static PointInformationDto getPointXYDto(PointInformation pointInfo) {
        LatXLngY midLatXLngY = LatXLngY.convertGRID_GPS(LatXLngY.TO_GRID, pointInfo.getLatitude(), pointInfo.getLongitude());

        return PointInformationDto.builder()
                .name(pointInfo.getName())
                .latitude(midLatXLngY.lat)
                .longitude(midLatXLngY.lng)
                .x((long) midLatXLngY.x)
                .y((long) midLatXLngY.y)
                .build();
    }

    private static PointInformationDto getPointMidDto(PointInformation startDto, PointInformation endDto) {
        LatXLngY midLatXLngY = LatXLngY.convertGRID_GPS(LatXLngY.TO_GRID,
                ((startDto.getLatitude() + endDto.getLatitude()) / 2),
                ((startDto.getLongitude() + endDto.getLongitude()) / 2));

        return PointInformationDto.builder()
                .latitude(midLatXLngY.lat)
                .longitude(midLatXLngY.lng)
                .x((long) midLatXLngY.x)
                .y((long) midLatXLngY.y)
                .build();
    }
}