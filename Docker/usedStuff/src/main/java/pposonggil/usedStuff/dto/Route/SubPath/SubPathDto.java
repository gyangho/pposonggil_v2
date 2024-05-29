package pposonggil.usedStuff.dto.Route.SubPath;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pposonggil.usedStuff.domain.Route.SubPath;
import pposonggil.usedStuff.dto.Color.BusColor;
import pposonggil.usedStuff.dto.Color.SubwayColor;
import pposonggil.usedStuff.dto.Route.Point.PointDto;
import pposonggil.usedStuff.dto.Route.PointInformation.PointInformationDto;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Map.entry;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;
import static pposonggil.usedStuff.dto.Color.BusColor.*;

@Data
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class SubPathDto {
    private Long subPathId;
    private Long pathId;
    private String type;
    private Long distance;
    private Long time;
    private Long stationCount;
    private String subwayName;
    private String busNo;
    private String subwayColor;
    private String busColor;
    private PointInformationDto startInfoDto;
    private PointInformationDto endInfoDto;
    private List<PointDto> pointDtos;

    public static SubPathDto fromEntity(SubPath subPath){
        return SubPathDto.builder()
                .subPathId(subPath.getId())
                .pathId(subPath.getPath().getId())
                .type(subPath.getType())
                .distance(subPath.getDistance())
                .time(subPath.getTime())
                .stationCount(subPath.getStationCount())
                .subwayName(subPath.getSubwayName())
                .busNo(subPath.getBusNo())
                .subwayColor(subPath.getSubwayColor())
                .busColor(subPath.getBusColor())
                .startInfoDto(PointInformationDto.fromEntity(subPath.getStartInfo()))
                .endInfoDto(PointInformationDto.fromEntity(subPath.getEndInfo()))
                .pointDtos(subPath.getPoints().stream()
                        .map(PointDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }

    public static SubPathDto fromJsonNode(JsonNode node) {
        return SubPathDto.builder()
                .type(Optional.ofNullable(node.get("trafficType"))
                        .map(JsonNode::asInt)
                        .map(TYPE_MAP::get)
                        .orElse("null"))
                .distance(node.get("distance").asLong(0L))
                .time(node.get("sectionTime").asLong(0L))
                .stationCount(Optional.ofNullable(node.get("stationCount")).map(JsonNode::asLong).orElse(null))
                        .subwayName(Optional.ofNullable(node.get("lane"))
                        .filter(JsonNode::isArray)
                        .map(laneNode -> laneNode.get(0))
                        .map(laneElement -> laneElement.get("name"))
                        .map(JsonNode::asText)
                        .orElse("null"))
                .busNo(Optional.ofNullable(node.get("lane"))
                        .filter(JsonNode::isArray)
                        .map(laneNode -> laneNode.get(0))
                        .map(laneElement -> laneElement.get("busNo"))
                        .map(JsonNode::asText)
                        .orElse("null"))
                .subwayColor(Optional.ofNullable(node.get("lane"))
                        .filter(JsonNode::isArray)
                        .map(laneNode -> laneNode.get(0))
                        .map(laneElement -> laneElement.get("name"))
                        .map(JsonNode::asText)
                        .map(SubPathDto::getLaneName)
                        .map(SubwayColor::valueOf)
                        .map(SubwayColor::getColorCode)//  지하철 색
                        .orElse("null"))
                .busColor(Optional.ofNullable(node.get("lane"))
                        .filter(JsonNode::isArray)
                        .map(laneNode -> laneNode.get(0))
                        .map(laneElement -> laneElement.get("type"))
                        .map(JsonNode::asInt)
                        .map(BusColor::getByNumber)
                        .map(BusColor::getColorCode)//  지하철 색
                        .orElse("null"))
                .pointDtos(Optional.ofNullable(node.get("passStopList"))
                        .map(passStopList -> passStopList.get("stations"))
                        .filter(JsonNode::isArray)
                        .map(JsonNode::elements)
                        .map(stations -> StreamSupport.stream(Spliterators.spliteratorUnknownSize(stations, Spliterator.ORDERED), false))
                        .orElseGet(Stream::empty)
                        .map(PointDto::fromJsonNode)
                        .collect(Collectors.toList()))
                .build();
    }

    private static final Map<Integer, String> TYPE_MAP = Map.ofEntries(
            entry(1, "subway"),
            entry(2, "bus"),
            entry(3, "walk")
    );

    public static String getLaneName(String name) {
        return name.replaceAll(" ", "_").replaceAll("\\.", "");
    }

    public static BusColor getByNumber(int number) {
        switch (number) {
            case 1:
                return ONE;
            case 2:
                return TWO;
            case 3:
                return THREE;
            case 4:
                return FOUR;
            case 5:
                return FIVE;
            case 6:
                return SIX;
            case 11:
                return ELEVEN;
            case 12:
                return TWELVE;
            case 13:
                return THIRTEEN;
            case 14:
                return FOURTEEN;
            case 15:
                return FIFTEEN;
            default:
                throw new IllegalArgumentException("Invalid bus number: " + number);
        }
    }

}
