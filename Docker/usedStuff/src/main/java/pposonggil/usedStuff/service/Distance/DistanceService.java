package pposonggil.usedStuff.service.Distance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Distance;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.Trade;
import pposonggil.usedStuff.dto.Distance.DistanceDto;
import pposonggil.usedStuff.dto.Route.PointInformation.PointInformationDto;
import pposonggil.usedStuff.dto.TransactionAddres.TransactionAddressDto;
import pposonggil.usedStuff.repository.Distance.DistanceRepository;
import pposonggil.usedStuff.repository.member.MemberRepository;
import pposonggil.usedStuff.repository.trade.TradeRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DistanceService {
    private final DistanceRepository distanceRepository;
    private final TradeRepository tradeRepository;
    private final MemberRepository memberRepository;
    // 지구의 반지름 (미터 단위)
    private static final double EARTH_RADIUS = 6371e3;

    /**
     * 모든 거래 조회
     */
    public List<DistanceDto> findDistanceWithTrade() {
        List<Distance> distances = distanceRepository.findDistancesWithTrade();
        return distances.stream()
                .map(DistanceDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 거래 아이디로 거리 조회
     */
    public DistanceDto findDistacneByTradeId(Long tradeId) {
        Distance distance = distanceRepository.findDistanceByTrade(tradeId)
                .orElseThrow(NoSuchFieldError::new);
        return DistanceDto.fromEntity(distance);
    }

    /**
     * 거리 아이디로 거리 조회
     */
    public DistanceDto findOne(Long distanceId) {
        Distance distance = distanceRepository.findById(distanceId)
                .orElseThrow(NoSuchElementException::new);
        return DistanceDto.fromEntity(distance);
    }

    /**
     * 거래에 해당하는 거리가 있는지 확인하고, 이미 존재하면 예외 던짐
     */
    public void checkDistanceExists(Long tradeId) {
        distanceRepository.findDistanceByTrade(tradeId).ifPresent(distance -> {
            throw new IllegalArgumentException("이미 거리가 생성됐습니다.");
        });
    }

    /**
     * 거리 생성
     */
    @Transactional
    public Long createDistance(DistanceDto distanceDto) {
        Trade trade = tradeRepository.findById(distanceDto.getTradeId())
                .orElseThrow(() -> new NoSuchElementException("Trade not found with id: " + distanceDto.getTradeId()));

        // 거리가 이미 존재하는지 확인
        checkDistanceExists(distanceDto.getTradeId());

        Distance distance = Distance.buildDistance(trade);

        distanceRepository.save(distance);
        return distance.getId();
    }

    /**
     * 주체자 거리 계산
     */
    @Transactional
    public DistanceDto calSubjectDistance(PointInformationDto startDto, DistanceDto distanceDto, Long memberId) {
        Member subject = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + memberId));
        Trade trade = tradeRepository.findById(distanceDto.getTradeId())
                .orElseThrow(() -> new NoSuchElementException("Trade not found with id: " + distanceDto.getTradeId()));
        Distance distance = distanceRepository.findById(distanceDto.getDistanceId())
                .orElseThrow(() -> new NoSuchElementException("Distance not found with id: " + distanceDto.getDistanceId()));
        if (!trade.getTradeSubject().getId().equals(subject.getId())) {
            throw new IllegalArgumentException("회원이 주체가 아닙니다.");
        }

        Long subjectDistance = calculateDistance(startDto, distanceDto.getTransactionAddressDto());
        if (distance.getSubjectTotalDistance() == -1L) {
            distance.changeSubjectTotalDistance(subjectDistance);
            distance.changeSubject(subjectDistance, 0L);
        } else {
            distance.changeSubject(subjectDistance,
                    100 - Math.round((double) subjectDistance * 100 / distance.getSubjectTotalDistance()));
        }
        distanceRepository.save(distance);
        return DistanceDto.fromEntity(distance);
    }

    /**
     * 객체자 거리 계산
     */
    @Transactional
    public DistanceDto calObjectDistance(PointInformationDto startDto, DistanceDto distanceDto, Long memberId) {
        Member object = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + memberId));
        Trade trade = tradeRepository.findById(distanceDto.getTradeId())
                .orElseThrow(() -> new NoSuchElementException("Trade not found with id: " + distanceDto.getTradeId()));
        Distance distance = distanceRepository.findById(distanceDto.getDistanceId())
                .orElseThrow(() -> new NoSuchElementException("Distance not found with id: " + distanceDto.getTradeId()));
        if (!trade.getTradeObject().getId().equals(object.getId())) {
            throw new IllegalArgumentException("회원이 객체가 아닙니다.");
        }

        Long objectDistance = calculateDistance(startDto, distanceDto.getTransactionAddressDto());
        if (distance.getObjectTotalDistance() == -1L) {
            distance.changeObjectTotalDistance(objectDistance);
            distance.changeObject(objectDistance, 0L);
        } else {
            distance.changeObject(objectDistance,
                    100 - Math.round((double) objectDistance * 100 / distance.getObjectTotalDistance()));
        }

        distanceRepository.save(distance);
        return DistanceDto.fromEntity(distance);
    }

    /**
     * 거리 삭제
     */
    @Transactional
    public void deleteDistance(Long distanceId) {
        Distance distance = distanceRepository.findById(distanceId)
                .orElseThrow(NoSuchElementException::new);
        distanceRepository.delete(distance);
    }

    /**
     * 거리 계산
     */
    public static Long calculateDistance(PointInformationDto startDto, TransactionAddressDto transactionAddressDto) {
        double startLatRad = Math.toRadians(startDto.getLatitude());
        double startLonRad = Math.toRadians(startDto.getLongitude());
        double endLatRad = Math.toRadians(transactionAddressDto.getLatitude());
        double endLonRad = Math.toRadians(transactionAddressDto.getLongitude());

        double deltaLatRad = endLatRad - startLatRad;
        double deltaLonRad = endLonRad - startLonRad;

        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                Math.cos(startLatRad) * Math.cos(endLatRad) *
                        Math.sin(deltaLonRad / 2) * Math.sin(deltaLonRad / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS * c;
        return Math.round(distance);
    }
}