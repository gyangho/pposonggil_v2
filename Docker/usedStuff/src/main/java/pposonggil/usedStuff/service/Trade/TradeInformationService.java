package pposonggil.usedStuff.service.Trade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Trade;
import pposonggil.usedStuff.dto.Trade.TradeInformationDto;
import pposonggil.usedStuff.repository.trade.TradeRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TradeInformationService {
    private final TradeRepository tradeRepository;

    /**
     *  정보 포함한 전체 거래 조회
     */
    public List<TradeInformationDto> findTradesWithInformation() {
        List<Trade> trades = tradeRepository.findAll();
        return trades.stream()
                .map(TradeInformationDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 정보 포함한 거래 상세 조회
     */
    public TradeInformationDto findOneWithInformation(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(NoSuchElementException::new);
        return TradeInformationDto.fromEntity(trade);
    }

    /**
     * 게시글 작성한 회원 아이디로 정보 포함한 거래 조회
     */
    public List<TradeInformationDto> findTradesWithInformationBySubjectId(Long memberId) {
        List<Trade> trades = tradeRepository.findTradesBySubjectId(memberId);

        return trades.stream()
                .map(TradeInformationDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 게시글 작성하지 않은 회원 아이디로 정보 포함한 거래 조회
     */
    public List<TradeInformationDto> findTradesWithInformationByObjectId(Long memberId) {
        List<Trade> trades = tradeRepository.findTradesByObjectId(memberId);

        return trades.stream()
                .map(TradeInformationDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 회원 아이디로 정보 포함한 거래 조회
     */
    public List<TradeInformationDto> findTradesWithInformationByMemberId(Long memberId) {
        List<Trade> trades = tradeRepository.findTradesByMemberId(memberId);

        return trades.stream()
                .map(TradeInformationDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 게시글 아이디로 정보 포함한 거래 조회
     */
    public TradeInformationDto findTradeWithInformationByBoardId(Long boardId) {
        Trade trade = tradeRepository.findTradeByBoardId(boardId)
                .orElseThrow(() -> new NoSuchElementException("Trade not found with boardId: " + boardId));

        return TradeInformationDto.fromEntity(trade);
    }

    /**
     * 게시글 & 회원 & 정보 & 거래 조회
     */
    public List<TradeInformationDto> findTradesWithBoardMemberInformation() {
        List<Trade> trades = tradeRepository.findTradesWithBoardMember();

        return trades.stream()
                .map(TradeInformationDto::fromEntity)
                .collect(Collectors.toList());
    }
}
