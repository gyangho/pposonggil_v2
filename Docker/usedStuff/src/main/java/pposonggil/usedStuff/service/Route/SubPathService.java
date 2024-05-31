package pposonggil.usedStuff.service.Route;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.LatLng;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.dto.Route.Path.PathDto;
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

//    public String createWalkSubPaths(SubPathDto subPathDto) throws IOException {
//        List<SubPath> subPaths = subPathRepository.findSubPathsByPathId(subPathDto.getPathId());
//
//        for (SubPath subPath : subPaths) {
//            String urlInfo = buildUrl(subPathDto.getStartDto(), subPathDto.getEndDto());
//            StringBuilder sb = getResponse(urlInfo);
//            System.out.println(sb.toString());
//            return sb.toString();
//
//        }
//        return null;
//    }
//

    public List<SubPathDto> createWalkSubPaths(PathDto pathDto) throws IOException {
        List<SubPathDto> subPathDtos = pathDto.getSubPathDtos();
        List<SubPathDto> result = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (SubPathDto subPathDto : subPathDtos) {
            String urlInfo = buildUrl(subPathDto.getStartDto(), subPathDto.getEndDto());
            StringBuilder sb = getResponse(urlInfo);
            JsonNode jsonNode = objectMapper.readTree(sb.toString());

            JsonNode routesNode = jsonNode.path("routes");
            JsonNode firstRoute = routesNode.get(0); // 첫 번째 route 접근

            double totalDistance = firstRoute.path("distance").asDouble();
            double totalTime = firstRoute.path("duration").asDouble();
            JsonNode legsNode = firstRoute.path("legs");
            JsonNode firstLeg = legsNode.get(0); // 첫 번째 leg 접근
            JsonNode stepsNode = firstLeg.path("steps");

            List<PointInformationDto> pointDtos = new ArrayList<>();
            for (JsonNode step : stepsNode) {
                String polyline = step.path("geometry").asText();
                List<LatLng> decode = PolylineEncoding.decode(polyline);
                System.out.println(decode);
            }
//            result.add();
        }
        return null;
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
