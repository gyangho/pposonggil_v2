package pposonggil.usedStuff.service.Trade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.ChatRoom;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.Trade;
import pposonggil.usedStuff.dto.Trade.TradeDto;
import pposonggil.usedStuff.repository.chatroom.ChatRoomRepository;
import pposonggil.usedStuff.repository.member.MemberRepository;
import pposonggil.usedStuff.repository.trade.TradeRepository;

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
                .collect(Collectors.toList());
    }

    /**
     * 게시글 작성하지 않은 회원 아이디로 거래 조회
     */
    public List<TradeDto> findTradesByObjectId(Long memberId) {
        List<Trade> trades = tradeRepository.findTradesByObjectId(memberId);

        return trades.stream()
                .map(TradeDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 회원 아이디로 거래 조회
     */
    public List<TradeDto> findTradesByMemberId(Long memberId) {
        List<Trade> trades = tradeRepository.findTradesByMemberId(memberId);

        return trades.stream()
                .map(TradeDto::fromEntity)
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
        tradeRepository.delete(trade);
    }
}
