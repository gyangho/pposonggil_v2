package pposonggil.usedStuff.api.Distance;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.Distance.DistanceDto;
import pposonggil.usedStuff.dto.Route.PointInformation.PointInformationDto;
import pposonggil.usedStuff.service.Distance.DistanceService;
import pposonggil.usedStuff.service.Trade.TradeService;

import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DistanceApiController {
    public final DistanceService distanceService;
    public final TradeService tradeService;

    /**
     * 거래를 포함한 전체 거리 조회
     *
     * @return 거리 Dto 리스트
     */
    @GetMapping("/api/distances")
    public List<DistanceDto> distances() {
        return distanceService.findDistanceWithTrade();
    }

    /**
     * 거리 아이디로 거리 조회
     * @param distanceId
     * @return 거리 아이디가 일치하는 거리 Dto
     */
    @GetMapping("/api/distance/by-distance/{distanceId}")
    public DistanceDto getDistanceByDistanceId(@PathVariable Long distanceId) {
        return distanceService.findOne(distanceId);
    }

    /**
     * 거래 아이디로 거리 조회
     * @param tradeId : 거래 아이디
     * @return 거래 아이디가 일치하는 거리 Dto
     */
    @GetMapping("/api/distance/by-trade/{tradeId}")
    public DistanceDto findDistanceWithTradeByTradeId(@PathVariable Long tradeId) {
        return distanceService.findDistacneByTradeId(tradeId);
    }

    /**
     * 거리 생성
     * @param distanceDto : 거리 Dto
     * @return : 생성한 거리 아이디
     */
    @PostMapping("/api/distance")
    public ResponseEntity<Object> createDistance(@RequestBody DistanceDto distanceDto) {
        Long distanceId = distanceService.createDistance(distanceDto);
        return ResponseEntity.status(HttpStatus.OK).body(distanceId);
    }

    /**
     * 주체자 아이디로 주체자의 거리 업데이트
     * @param memberId : 주체자 아이디
     * @param startDto : 주체자의 출발지 Dto
     * @param distanceDto : 업데이트할 거리 Dto
     * @return : 성공 --> "주체자 거리를 업데이트 하였습니다."
     */
    @PutMapping("/api/distance/by-subject/{memberId}")
    public ResponseEntity<Object> updateSubjectDistance(@PathVariable Long memberId,
                                                         @RequestPart PointInformationDto startDto,
                                                         @RequestPart DistanceDto distanceDto){
        DistanceDto oldDistanceDto = distanceService.findOne(distanceDto.getDistanceId());
        if (oldDistanceDto == null) {
            return ResponseEntity.notFound().build();
        }
        distanceService.calSubjectDistance(startDto, distanceDto, memberId);

        HashMap<Object, Object> response = new HashMap<>();
        response.put("distanceId", distanceDto.getDistanceId());
        response.put("message", "주체자 거리를 업데이트 하였습니다.");
        return ResponseEntity.ok(response);
    }

    /**
     * 객체자 아이디로 객체자의 거리 업데이트
     * @param memberId : 객체자 아이디
     * @param startDto : 객체자의 출발지 Dto
     * @param distanceDto : 업데이트할 거리 Dto
     * @return : 성공 --> "객체자 거리를 업데이트 하였습니다."
     */
    @PutMapping("/api/distance/by-object/{memberId}")
    public ResponseEntity<Object> updateObjectDistance(@PathVariable Long memberId,
                                                         @RequestPart PointInformationDto startDto,
                                                         @RequestPart DistanceDto distanceDto){
        DistanceDto oldDistanceDto = distanceService.findOne(distanceDto.getDistanceId());
        if (oldDistanceDto == null) {
            return ResponseEntity.notFound().build();
        }
        distanceService.calObjectDistance(startDto, distanceDto, memberId);

        HashMap<Object, Object> response = new HashMap<>();
        response.put("distanceId", distanceDto.getDistanceId());
        response.put("message", "객체자 거리를 업데이트 하였습니다.");
        return ResponseEntity.ok(response);
    }

    /**
     * 거리 아이디로 거리 삭제
     * @param distanceId: 거리 아이디
     * @return 성공 --> "거리를 삭제하였습니다."
     */
    @DeleteMapping("/api/distance/{distanceId}")
    public ResponseEntity<String> deleteDistance(@PathVariable Long distanceId) {
        distanceService.deleteDistance(distanceId);

        return ResponseEntity.ok("거리를 삭제하였습니다.");
    }
}
