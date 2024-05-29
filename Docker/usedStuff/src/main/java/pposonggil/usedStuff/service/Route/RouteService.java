package pposonggil.usedStuff.service.Route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.dto.Path.PathDto;
import pposonggil.usedStuff.dto.Route.PointInformation.PointInformationDto;

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
public class RouteService {
    @Value("${api-key.ODsay-key}")
    private String apiKey;

//    /**
//     * 전체 경로 조회
//     */
//    public List<PathDto> findPaths() {
//        List<Path> paths = pathRepository.findAll();
//        return paths.stream()
//                .map(PathDto::fromEntity)
//                .collect(Collectors.toList());
//    }

    public List<PathDto> searchPubTransPath(PointInformationDto start, PointInformationDto end) throws IOException {
        String urlInfo = buildUrl(start, end);
        StringBuilder sb = getResponse(urlInfo);
        return getPathDtos(sb);
    }

    private static List<PathDto> getPathDtos(StringBuilder sb) throws JsonProcessingException {
        List<PathDto> pathDtos = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(sb.toString());
        JsonNode result = jsonNode.get("result");
        JsonNode path = result.get("path");
        for (JsonNode node : path) {
            PathDto pathDto = PathDto.fromJsonNode(node);
            pathDtos.add(pathDto);
        }

        return pathDtos;
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
                "https://api.odsay.com/v1/api/searchPubTransPathT?SX=%s&SY=%s&EX=%s&EY=%s&apiKey=%s",
                URLEncoder.encode(Double.toString(start.getLongitude()), StandardCharsets.UTF_8),
                URLEncoder.encode(Double.toString(start.getLatitude()), StandardCharsets.UTF_8),
                URLEncoder.encode(Double.toString(end.getLongitude()), StandardCharsets.UTF_8),
                URLEncoder.encode(Double.toString(end.getLatitude()), StandardCharsets.UTF_8),
                URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
        );
        return urlInfo;
    }
}