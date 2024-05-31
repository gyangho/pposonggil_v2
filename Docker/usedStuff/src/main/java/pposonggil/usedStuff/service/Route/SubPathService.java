package pposonggil.usedStuff.service.Route;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.LatLng;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.dto.Route.Path.PathDto;
import pposonggil.usedStuff.dto.Route.Point.PointDto;
import pposonggil.usedStuff.dto.Route.PointInformation.PointInformationDto;
import pposonggil.usedStuff.dto.Route.SubPath.SubPathDto;
import pposonggil.usedStuff.repository.route.path.PathRepository;
import pposonggil.usedStuff.repository.route.subpath.SubPathRepository;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubPathService {
    private final PathRepository pathRepository;
    private final SubPathRepository subPathRepository;

    public List<SubPathDto> createDefaultSubPaths(PathDto pathDto) throws IOException {
        List<SubPathDto> subPathDtos = pathDto.getSubPathDtos();

        List<SubPathDto> result = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (SubPathDto subPathDto : subPathDtos) {
            if (subPathDto.getType().equals("walk")) {
                String urlInfo = buildUrl(subPathDto.getStartDto(), subPathDto.getEndDto());
                StringBuilder sb = getResponse(urlInfo);
                JsonNode jsonNode = objectMapper.readTree(sb.toString());

                JsonNode routesNode = jsonNode.path("routes").get(0);

                double totalDistance = routesNode.path("distance").asDouble();
                double totalTime = routesNode.path("duration").asDouble();
                JsonNode legsNode = routesNode.path("legs").get(0);
                JsonNode stepsNode = legsNode.path("steps");

                List<PointDto> pointDtos = new ArrayList<>();

                for (JsonNode step : stepsNode) {
                    String polyline = step.path("geometry").asText();

                    List<LatLng> decode = PolylineEncoding.decode(polyline);
                    for (LatLng latLng : decode) {
                        Double latitude = latLng.lat;
                        Double longitude = latLng.lng;

                        PointInformationDto pointInformationDto = PointInformationDto.builder()
                                .latitude(latitude)
                                .longitude(longitude)
                                .build();

                        PointDto pointDto = PointDto.builder()
                                .subPathId(subPathDto.getSubPathId())
                                .pointInformationDto(pointInformationDto)
                                .build();

                        pointDtos.add(pointDto);
                    }
                }
                subPathDto.setPointDtos(pointDtos);
            }
            result.add(subPathDto);
        }
        return result;
    }

    private static StringBuilder getResponse(String urlInfo) throws IOException {
        URL url = new URL(urlInfo);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }

        bufferedReader.close();
        conn.disconnect();

        return sb;
    }

    private String buildUrl(PointInformationDto start, PointInformationDto end) {
        String urlInfo = String.format(
                "https://routing.openstreetmap.de/routed-foot/route/v1/foot/%s,%s;%s,%s?overview=false&steps=true",
                URLEncoder.encode(Double.toString(start.getLongitude()), StandardCharsets.UTF_8),
                URLEncoder.encode(Double.toString(start.getLatitude()), StandardCharsets.UTF_8),
                URLEncoder.encode(Double.toString(end.getLongitude()), StandardCharsets.UTF_8),
                URLEncoder.encode(Double.toString(end.getLatitude()), StandardCharsets.UTF_8)
        );
        return urlInfo;
    }
}
