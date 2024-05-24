package pposonggil.usedStuff.service.Trade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Trade;
import pposonggil.usedStuff.dto.Trade.TradeReviewDto;
import pposonggil.usedStuff.repository.trade.TradeRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TradeReviewService {
    private final TradeRepository tradeRepository;

    /**
     * 리뷰 포함한 거래 상세 조회
     */
    public TradeReviewDto findOneWithReview(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(NoSuchElementException::new);
        return TradeReviewDto.fromEntity(trade);
    }

    /**
     * 게시글 작성한 회원 아이디로 리뷰 포함한 거래 조회
     */
    public List<TradeReviewDto> findTradesWithReviewBySubjectId(Long memberId) {
        List<Trade> trades = tradeRepository.findTradesBySubjectId(memberId);

        return trades.stream()
                .map(TradeReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 게시글 작성하지 않은 회원 아이디로 리뷰 포함한 거래 조회
     */
    public List<TradeReviewDto> findTradesWithReviewByObjectId(Long memberId) {
        List<Trade> trades = tradeRepository.findTradesByObjectId(memberId);

        return trades.stream()
                .map(TradeReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 회원 아이디로 리뷰 포함한 거래 조회
     */
    public List<TradeReviewDto> findTradesWithReviewByMemberId(Long memberId) {
        List<Trade> trades = tradeRepository.findTradesByMemberId(memberId);

        return trades.stream()
                .map(TradeReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 게시글 아이디로 리뷰 포함한 거래 조회
     */
    public TradeReviewDto findTradeWithReviewByBoardId(Long boardId) {
        Trade trade = tradeRepository.findTradeByBoardId(boardId)
                .orElseThrow(() -> new NoSuchElementException("Trade not found with boardId: " + boardId));

        return TradeReviewDto.fromEntity(trade);
    }

    /**
     * 게시글 & 회원 & 리뷰 & 거래 조회
     */
    public List<TradeReviewDto> findTradesWithBoardMemberReview() {
        List<Trade> trades = tradeRepository.findTradesWithBoardMember();

        return trades.stream()
                .map(TradeReviewDto::fromEntity)
                .collect(Collectors.toList());
    }
}
