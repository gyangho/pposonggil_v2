package pposonggil.usedStuff.service.Trade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.ChatRoom;
import pposonggil.usedStuff.domain.Distance;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.Trade;
import pposonggil.usedStuff.dto.Trade.TradeDto;
import pposonggil.usedStuff.repository.Distance.DistanceRepository;
import pposonggil.usedStuff.repository.chatroom.ChatRoomRepository;
import pposonggil.usedStuff.repository.member.MemberRepository;
import pposonggil.usedStuff.repository.trade.TradeRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TradeService {
    private final TradeRepository tradeRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final DistanceRepository distanceRepository;
    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");

    /**
     * 전체 거래 조회
     */
    public List<TradeDto> findTrades() {
        List<Trade> trades = tradeRepository.findAll();
        return trades.stream()
                .map(TradeDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 거래 상세 조회
     */
    public TradeDto findOne(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(NoSuchElementException::new);
        return TradeDto.fromEntity(trade);
    }

    /**
     * 게시글 작성한 회원 아이디로 거래 조회
     */
    public List<TradeDto> findTradesBySubjectId(Long memberId) {
        List<Trade> trades = tradeRepository.findTradesBySubjectId(memberId);

        return trades.stream()
                .map(TradeDto::fromEntity)
                .sorted((trade1, trade2) -> {
                    LocalDateTime startTime1 = LocalDateTime.parse(trade1.getStartTimeString(), inputFormatter);
                    LocalDateTime startTime2 = LocalDateTime.parse(trade2.getStartTimeString(), inputFormatter);
                    return startTime1.compareTo(startTime2);
                })
                .collect(Collectors.toList());
    }

    /**
     * 게시글 작성하지 않은 회원 아이디로 거래 조회
     */
    public List<TradeDto> findTradesByObjectId(Long memberId) {
        List<Trade> trades = tradeRepository.findTradesByObjectId(memberId);

        return trades.stream()
                .map(TradeDto::fromEntity)
                .sorted((trade1, trade2) -> {
                    LocalDateTime startTime1 = LocalDateTime.parse(trade1.getStartTimeString(), inputFormatter);
                    LocalDateTime startTime2 = LocalDateTime.parse(trade2.getStartTimeString(), inputFormatter);
                    return startTime1.compareTo(startTime2);
                })
                .collect(Collectors.toList());
    }

    /**
     * 회원 아이디로 거래 조회
     */
    public List<TradeDto> findTradesByMemberId(Long memberId) {
        List<Trade> trades = tradeRepository.findTradesByMemberId(memberId);

        return trades.stream()
                .map(TradeDto::fromEntity)
                .sorted((trade1, trade2) -> {
                    LocalDateTime startTime1 = LocalDateTime.parse(trade1.getStartTimeString(), inputFormatter);
                    LocalDateTime startTime2 = LocalDateTime.parse(trade2.getStartTimeString(), inputFormatter);
                    return startTime1.compareTo(startTime2);
                })
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 아이디로 거래 조회
     */
    public TradeDto findTradeByBoardId(Long chatRoomId) {
        Trade trade = tradeRepository.findTradeByChatRoomId(chatRoomId)
                .orElseThrow(() -> new NoSuchElementException("Trade not found with boardId: " + chatRoomId));

        return TradeDto.fromEntity(trade);
    }

    /**
     * 게시글 & 회원 & 거래 조회
     */
    public List<TradeDto> findTradesWithBoardMember() {
        List<Trade> trades = tradeRepository.findTradesWithMember();

        return trades.stream()
                .map(TradeDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 거래 생성
     */
    @Transactional
    public Long createTrade(TradeDto tradeDto) {
        ChatRoom tradeChatRoom = chatRoomRepository.findById(tradeDto.getChatRoomId())
                .orElseThrow(() -> new NoSuchElementException("Board not found with id: " + tradeDto.getChatRoomId()));
        Member tradeSubject = memberRepository.findById(tradeDto.getSubjectId())
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + tradeDto.getSubjectId()));
        Member tradeObject = memberRepository.findById(tradeDto.getObjectId())
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + tradeDto.getObjectId()));

        if (!(tradeChatRoom.getChatBoard().getWriter().getId().equals(tradeDto.getSubjectId()) && tradeChatRoom.getRequester().getId().equals(tradeDto.getObjectId())) &&
                !(tradeChatRoom.getChatBoard().getWriter().getId().equals(tradeDto.getObjectId()) && tradeChatRoom.getRequester().getId().equals(tradeDto.getObjectId()))) {
            throw new IllegalArgumentException("거래멤버가 채팅방 멤버가 아닙니다.");
        }

        if (tradeSubject.equals(tradeObject)) {
            throw new IllegalArgumentException("자기 자신과 거래할 수 없습니다.");
        }

        Trade trade = Trade.buildTrade(tradeChatRoom, tradeSubject, tradeObject);

        trade.setTradeChatRoom(tradeChatRoom);
        trade.setTradeSubject(tradeSubject);
        trade.setTradeObject(tradeObject);

        tradeRepository.save(trade);

        return trade.getId();
    }

    /**
     * 거래 삭제
     */
    @Transactional
    public void deleteTrade(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(NoSuchElementException::new);

        distanceRepository.findDistanceByTrade(tradeId)
                .ifPresent(distanceRepository::delete);

        tradeRepository.delete(trade);
    }

    /**
     * 거래 시작 시각이 지나고
     * 상대방의 거리가 500m 초과했을 때만
     * 거래 취소 가능
     */
    @Transactional
    public void deleteTradeByMember(Long tradeId, Long memberId) throws IllegalAccessException {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(NoSuchElementException::new);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NoSuchElementException::new);
        Distance distance = distanceRepository.findDistanceByTrade(tradeId)
                .orElseThrow(NoSuchElementException::new);

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
        String startTimeString = trade.getStartTimeString();
        LocalTime startTime = LocalTime.parse(startTimeString, inputFormatter);
        LocalTime curTime = LocalTime.now(ZoneId.of("Asia/Seoul"));

        if (!trade.getTradeSubject().getId().equals(memberId) && !trade.getTradeObject().getId().equals(memberId)) {
            throw new IllegalArgumentException("거래 회원이 아닙니다.");
        }
        if ((trade.getTradeSubject().getId().equals(memberId) && distance.getObjectDistance() < 500) ||
                (trade.getTradeObject().getId().equals(memberId) && distance.getSubjectDistance() < 500)) {
            throw new IllegalArgumentException("상대방이 주변에 있습니다. 조금만 기다려주세요");
        }

        distanceRepository.findDistanceByTrade(tradeId)
                .ifPresent(distanceRepository::delete);

        tradeRepository.delete(trade);
    }

}